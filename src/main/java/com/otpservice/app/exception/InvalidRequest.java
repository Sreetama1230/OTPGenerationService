package com.otpservice.app.exception;

public class InvalidRequest extends RuntimeException{
	
	public InvalidRequest(String errorMessage){
		super(errorMessage);
	}

}
