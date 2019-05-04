package com.yuriylisovskiy.er.Services.ClientService.Exceptions;

public class RequestError extends Exception {

	private String _err;
	private int _status;

	public RequestError(String err, int status) {
		this._err = err;
		this._status = status;
	}

	public String getErr() {
		return this._err;
	}

	public int getStatus() {
		return this._status;
	}

}
