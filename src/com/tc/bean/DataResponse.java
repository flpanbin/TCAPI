package com.tc.bean;

public class DataResponse<T> extends BaseResponse {

	private T data;

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public DataResponse(int code, String message, T data) {
		super(code, message);
		this.data = data;
	}

	public DataResponse(int code, T data) {
		super(code, "");
		this.data = data;
	}

	public DataResponse(int code, String message) {
		super(code, message);
	}

	public DataResponse() {
		super();
	}
}
