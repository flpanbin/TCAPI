package com.tc.sms;

import com.tc.sms.utils.MobClient;

/**
 * 服务端发起验证请求验证移动端(手机)发送的短信
 * 
 * @author Administrator
 *
 */
public class SmsVerifyKit {

	private String appkey;
	private String phone;
	private String zone;
	private String code;

	/**
	 * 
	 * @param appkey
	 *            应用KEY
	 * @param phone
	 *            电话号码 xxxxxxxxx
	 * @param zone
	 *            区号 86
	 * @param code
	 *            验证码 xx
	 */
	public SmsVerifyKit(String appkey, String phone, String zone, String code) {
		super();
		this.appkey = appkey;
		this.phone = phone;
		this.zone = zone;
		this.code = code;
	}

	/**
	 * 服务端发起验证请求验证移动端(手机)发送的短信
	 * 
	 * @return
	 * @throws Exception
	 */
	public String verify() throws Exception {

		String address = "https://webapi.sms.mob.com/sms/verify";
		MobClient client = null;
		try {
			client = new MobClient(address);
			client.addParam("appkey", appkey).addParam("phone", phone)
					.addParam("zone", zone).addParam("code", code);
			client.addRequestProperty("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			client.addRequestProperty("Accept", "application/json");
			String result = client.post();
			return result;
		} finally {
			client.release();
		}
	}

	public static void main(String[] args) throws Exception {

		String result = new SmsVerifyKit("1f8e561c03f0a", "15008437212", "86",
				"1234").verify();
		System.out.println("result--->" + result);
	}

}
