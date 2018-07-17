package com.tc.bean;

public class BaseResponse {

	private int code;
	private String message;

	public int getCode() {
		return code;
	}

	public BaseResponse(int code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	public BaseResponse setCode(int code) {
		this.code = code;
		return this;
	}

	public BaseResponse() {
		super();
	}

	public String getMessage() {
		return message;
	}

	public BaseResponse(int code) {
		super();
		this.code = code;
	}

	public BaseResponse setMessage(String message) {
		this.message = message;
		return this;
	}
}
