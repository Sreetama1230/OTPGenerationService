package com.otpservice.app.dto;

public class OTPResponse {
	private String otp;
	private String sessionId;
	private String username;

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public OTPResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	public OTPResponse(String otp, String sessionId, String username) {
		super();
		this.otp = otp;
		this.sessionId = sessionId;
		this.username = username;
	}

	public static OTPResponse getOTPResponse(CustomerOTP customerOTP) {
		return new OTPResponse(customerOTP.getOtp(), customerOTP.getSessionId(), customerOTP.getUsername());
	}

}
