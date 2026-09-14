package com.otpservice.app.controller;

import org.apache.tomcat.util.http.parser.HttpParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.otpservice.app.dto.OTPResponse;
import com.otpservice.app.model.Form;
import com.otpservice.app.service.FormService;

@RestController
@RequestMapping("/otp")
public class FormController {

	@Autowired
	private FormService formService;

	@PostMapping("/send")
	public ResponseEntity<OTPResponse> generateOTP(@RequestBody Form form) {
		return new ResponseEntity<OTPResponse>(formService.generateOTP(form), HttpStatus.CREATED);
	}

	@PostMapping("/resend/username/{username}")
	public ResponseEntity<OTPResponse> resendOTP(@PathVariable String username,
			@RequestHeader("session-id") String sessionid) {

		return new ResponseEntity<OTPResponse>(formService.resendOTP(username, sessionid), HttpStatus.CREATED);
	}

	@PostMapping("username/{username}/verify")
	public ResponseEntity<String> resendOTP(@RequestBody String otp, @PathVariable String username,
			@RequestHeader("session-id") String sessionid) {

		return new ResponseEntity<String>(formService.verifyOTP(username, sessionid, otp), HttpStatus.OK);
	}
}
