package com.yuriylisovskiy.er.client.exceptions;

public class RequestError extends Exception {

	private String err;
	private int status;

	public RequestError(String err, int status) {
		this.err = err;
		this.status = status;
	}

	public String getErr() {
		return this.err;
	}

	public int getStatus() {
		return this.status;
	}

}
