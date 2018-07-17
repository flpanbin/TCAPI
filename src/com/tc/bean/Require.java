package com.tc.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 参数验证
 * 
 * @author PB
 *
 */
public class Require {

	/**
	 * 验证条件
	 */
	private List<Object> conditions = new ArrayList<Object>();
	/**
	 * 错误信息
	 */
	private List<String> messages = new ArrayList<String>();

	public static Require getMe() {
		return new Require();
	}

	public Object getCondition(int index) {
		return conditions.get(index);
	}

	public Require put(Object condition, String message) {
		conditions.add(condition);
		messages.add(message);
		return this;
	}

	public String getMessage(int index) {
		return messages.get(index);
	}

	public int getLength() {
		return conditions.size();
	}

}
