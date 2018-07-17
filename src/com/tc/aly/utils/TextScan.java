package com.tc.aly.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.green.model.v20170112.TextScanRequest;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.gexin.fastjson.JSONObject;
import com.tc.conf.Config;

public class TextScan {

	public static TextScanRequest textScan(String text) {

		TextScanRequest textScanRequest = new TextScanRequest();
		textScanRequest.setAcceptFormat(FormatType.JSON); // 指定api返回格式
		textScanRequest.setContentType(FormatType.JSON);
		textScanRequest.setMethod(com.aliyuncs.http.MethodType.POST); // 指定请求方法
		textScanRequest.setEncoding("UTF-8");
		textScanRequest.setRegionId(Config.regionId);

		List<Map<String, Object>> tasks = new ArrayList<Map<String, Object>>();

		Map<String, Object> task1 = new LinkedHashMap<String, Object>();
		task1.put("dataId", UUID.randomUUID().toString());
		task1.put("content", text);

		tasks.add(task1);
		JSONObject data = new JSONObject();
		data.put("scenes", Arrays.asList("keyword"));
		data.put("tasks", tasks);

		try {
			textScanRequest.setContent(data.toJSONString().getBytes("UTF-8"),
					"UTF-8", FormatType.JSON);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		/**
		 * 请务必设置超时时间
		 */
		textScanRequest.setConnectTimeout(3000);
		textScanRequest.setReadTimeout(6000);
		return textScanRequest;
	}

	public static IAcsClient getIAcsClient() {
		// 请替换成你自己的accessKeyId、accessKeySecret
		IClientProfile profile = DefaultProfile.getProfile(Config.regionId,
				Config.accessKeyId, Config.accessKeySecret);
		try {
			DefaultProfile.addEndpoint(Config.regionId, Config.regionId,
					"Green", Config.domainName);
		} catch (ClientException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return new DefaultAcsClient(profile);
	}
}
