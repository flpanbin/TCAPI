package com.tc.aly.utils;

import java.io.UnsupportedEncodingException;

import com.aliyuncs.http.HttpResponse;
import com.gexin.fastjson.JSON;
import com.gexin.fastjson.JSONArray;
import com.gexin.fastjson.JSONObject;
import com.tc.utils.StringUtil;

/**
 * @author PB
 *         <P>
 *         解析HttpResponse 返回的数据 1:含有敏感词汇 0：不拦截信息
 */
public class JsonDecoder {

	public static int jsonDecode(HttpResponse httpResponse) {

		if (httpResponse.isSuccess()) {
			JSONObject scrResponse = null;
			try {
				scrResponse = JSON.parseObject(new String(httpResponse
						.getContent(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
							System.out.println("args = [" + suggestion + "]");
							if (StringUtil.isEmpty(suggestion)) {
								return 0;
							}
							if (suggestion.equals("block"))
								return 1;
						}
					} else {
						System.out.println("task process fail:"
								+ ((JSONObject) taskResult).getInteger("code"));
						return 0;
					}
				}
			} else {
				System.out.println("detect not success. code:"
						+ scrResponse.getInteger("code"));
				return 0;
			}
		} else {
			System.out.println("response not success. status:"
					+ httpResponse.getStatus());
			return 0;
		}
		return 0;
	}
}
