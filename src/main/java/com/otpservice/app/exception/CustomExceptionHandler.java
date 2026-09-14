package com.otpservice.app.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.otpservice.app.dto.Error;

@ControllerAdvice
public class CustomExceptionHandler {

	@ExceptionHandler
	public ResponseEntity<Error> handleInvalidRequest(InvalidRequest request){

		return new ResponseEntity<Error>(new Error(request.getMessage(), HttpStatus.BAD_REQUEST.toString()) , HttpStatus.BAD_REQUEST);
	}
}
