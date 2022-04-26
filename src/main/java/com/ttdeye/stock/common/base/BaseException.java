
package com.ttdeye.stock.common.base;

public class BaseException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	protected Object code;
	protected String message;
	protected Object data;

	public BaseException() {

	}

	public BaseException(Object code, String message) {
		super(message);
		this.code = code;
		this.message = message;
	}


	public BaseException(Object code, String message, Object data) {
		super(message);
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public Object getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
    @Override
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}