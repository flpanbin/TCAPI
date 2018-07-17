package com.tc.test;

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
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.green.model.v20170112.TextScanRequest;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.gexin.fastjson.JSON;
import com.gexin.fastjson.JSONArray;
import com.gexin.fastjson.JSONObject;
import com.jfinal.kit.HttpKit;
import com.tc.utils.DateUtil;

public class Test {

	public static void main(String[] args) {
		testFlj();
	}

	public static void testJson2() {
		ExamInfo examInfo;

		List<ExamInfo> lists = new ArrayList<ExamInfo>();

		for (int i = 1; i < 4; i++) {
			examInfo = new ExamInfo("00" + i, "A00" + i, "00" + i);
			lists.add(examInfo);
		}
		for (int i = 1; i < 5; i++) {
			examInfo = new ExamInfo("00100" + i, "A00100" + i, "001");
			lists.add(examInfo);
			examInfo = new ExamInfo("00200" + i, "A00200" + i, "002");
			lists.add(examInfo);
			examInfo = new ExamInfo("00300" + i, "A00300" + i, "003");
			lists.add(examInfo);
		}
		for (int i = 1; i < 4; i++) {
			examInfo = new ExamInfo("00100200" + i, "A00100200" + i, "001002");
			lists.add(examInfo);
			examInfo = new ExamInfo("00100300" + i, "A00100300" + i, "001003");
			lists.add(examInfo);
			examInfo = new ExamInfo("00200100" + i, "A00200100" + i, "002001");
			lists.add(examInfo);
		}

		JsonEncap jsonEncap = new JsonEncap();

		jsonEncap.getModelList().addAll(lists);
		String json = jsonEncap.toJson();
		System.out.println("json--->" + json);
	}

	public static void test1() {
		String keyId = "ae16f22b5089444cb51fd78cbef01c75";
		String keySecret = "6d41f55bb50e4e078e667d32e75d5c78";
		String url = "https://mosapi.meituan.com/mcs/v1";
		String sigAddress = "POST\nmosapi.meituan.com\n/mcs/v1\n";
		String sigPara = "";
	}

	public static void testFlj() {
		String accessKeyId = "LTAI2id4Elloioli";
		String accessKeySecret="l4BOkcFqqTidDzwnTaP7xqwuWduJHO";
		String domainName="green.cn-shanghai.aliyuncs.com";
		String regionId="cn-shanghai";
		// 请替换成你自己的accessKeyId、accessKeySecret
		IClientProfile profile = DefaultProfile.getProfile(regionId,
				accessKeyId, accessKeySecret);
		try {
			DefaultProfile.addEndpoint(regionId, regionId, "Green",
					domainName);
		} catch (ClientException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		IAcsClient client = new DefaultAcsClient(profile);

		TextScanRequest textScanRequest = new TextScanRequest();
		textScanRequest.setAcceptFormat(FormatType.JSON); // 指定api返回格式
		textScanRequest.setContentType(FormatType.JSON);
		textScanRequest.setMethod(com.aliyuncs.http.MethodType.POST); // 指定请求方法
		textScanRequest.setEncoding("UTF-8");
		textScanRequest.setRegionId(regionId);

		List<Map<String, Object>> tasks = new ArrayList<Map<String, Object>>();

		Map<String, Object> task1 = new LinkedHashMap<String, Object>();
		task1.put("dataId", UUID.randomUUID().toString());
		task1.put("content", "蒙3#4汗d药法#轮$s功sfd斯蒂芬斯蒂芬");

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
		try {
			HttpResponse httpResponse = client.doAction(textScanRequest);

			if (httpResponse.isSuccess()) {
				JSONObject scrResponse = JSON.parseObject(new String(
						httpResponse.getContent(), "UTF-8"));
				System.out.println(JSON.toJSONString(scrResponse, true));
				if (200 == scrResponse.getInteger("code")) {
					JSONArray taskResults = scrResponse.getJSONArray("data");
					for (Object taskResult : taskResults) {
						if (200 == ((JSONObject) taskResult).getInteger("code")) {
							JSONArray sceneResults = ((JSONObject) taskResult)
									.getJSONArray("results");
							for (Object sceneResult : sceneResults) {
								String scene = ((JSONObject) sceneResult)
										.getString("scene");
								String suggestion = ((JSONObject) sceneResult)
										.getString("suggestion");
								// 根据scene和suggetion做相关的处理
								// do something
								System.out.println("args = [" + scene + "]");
								System.out.println("args = [" + suggestion
										+ "]");
							}
						} else {
							System.out.println("task process fail:"
									+ ((JSONObject) taskResult)
											.getInteger("code"));
						}
					}
				} else {
					System.out.println("detect not success. code:"
							+ scrResponse.getInteger("code"));
				}
			} else {
				System.out.println("response not success. status:"
						+ httpResponse.getStatus());
			}
		} catch (ServerException e) {
			e.printStackTrace();
		} catch (ClientException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
