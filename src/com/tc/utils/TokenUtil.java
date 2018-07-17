package com.tc.utils;

/**
 * @author PB
 *
 */
public class TokenUtil {

	/**
	 * 生成token
	 * 
	 * @return
	 */
	public static String generateToken() {
		return RandomUtil.randomCustomUUID().concat(RandomUtil.randomString(6));
	}
}
