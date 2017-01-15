package com.newland.cloudtest.exception;

public class SmsException extends Exception {

	private static final long serialVersionUID = 1L;

	public SmsException() {

	}

	public SmsException(String message, Throwable cause) {
		super(message, cause);
	}

	public SmsException(String message) {
		super(message);
	}

	public SmsException(Throwable cause) {
		super(cause);
	}

}
