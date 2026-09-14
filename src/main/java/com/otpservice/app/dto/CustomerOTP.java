package com.otpservice.app.dto;

import java.time.LocalDateTime;

public class CustomerOTP {

	private String otp;
	private Long attemptsLeft;
	private String sessionId;
	private String username;
	private LocalDateTime createdTime;
	private LocalDateTime expiryTime;
	private Long verifyCount;

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public Long getAttemptsLeft() {
		return attemptsLeft;
	}

	public void setAttemptsLeft(Long attemptsLeft) {
		this.attemptsLeft = attemptsLeft;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Long getVerifyCount() {
		return verifyCount;
	}

	public void setVerifyCount(Long verifyCount) {
		this.verifyCount = verifyCount;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public LocalDateTime getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(LocalDateTime createdTime) {
		this.createdTime = createdTime;
	}

	public LocalDateTime getExpiryTime() {
		return expiryTime;
	}

	public void setExpiryTime(LocalDateTime expiryTime) {
		this.expiryTime = expiryTime;
	}

	public CustomerOTP() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CustomerOTP(String otp, Long attemptsLeft, String sessionId, String username, LocalDateTime createdTime,
			LocalDateTime expiryTime) {
		super();
		this.otp = otp;
		this.attemptsLeft = attemptsLeft;
		this.sessionId = sessionId;
		this.username = username;
		this.createdTime = createdTime;
		this.expiryTime = expiryTime;
	}

}
