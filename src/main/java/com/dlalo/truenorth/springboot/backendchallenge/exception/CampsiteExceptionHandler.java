package com.dlalo.truenorth.springboot.backendchallenge.exception;

public class CampsiteExceptionHandler extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public CampsiteExceptionHandler(String exception) {
		super(exception);
	}

}
