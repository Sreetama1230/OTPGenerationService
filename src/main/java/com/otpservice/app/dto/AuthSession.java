package com.otpservice.app.dto;

import java.time.LocalDateTime;

public class AuthSession {

	private String sessionId;

	private  Long currentCount;
	private LocalDateTime startTime;
	private LocalDateTime endTime; // startTime + 30min



	public AuthSession() {
		super();
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}


	public Long getCurrentCount() {
		return currentCount;
	}

	public void setCurrentCount(Long currentCount) {
		this.currentCount = currentCount;
	}

	public AuthSession(String sessionId, LocalDateTime startTime, LocalDateTime endTime) {
		super();
		this.sessionId = sessionId;
		this.startTime = startTime;
		this.endTime = endTime;
		
	}

	
}
