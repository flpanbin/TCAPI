package com.tc.token;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.tc.model.TcType;
import com.tc.model.User;
import com.tc.utils.TokenUtil;

public class TokenManage {
	private static TokenManage me = new TokenManage();
	private Map<String, User> tokens;

	/**
	 * @return
	 */
	public static TokenManage getInstance() {
		return me;
	}

	private TokenManage() {
		tokens = new ConcurrentHashMap<String, User>();
	}

	/**
	 * 校验token
	 * 
	 * @param token
	 * @return
	 */
	public User validToken(String token) {
		return tokens.get(token);
	}

	public String createToken(User user) {

		String token = TokenUtil.generateToken();
		tokens.put(token, user);

		return token;
	}

	public void saveToken(String userId, String token) {

	}

}
