package com.otpservice.app.dto;

public class Error {

	private String description;
	private String statusCode;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public Error() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Error(String description, String statusCode) {
		super();
		this.description = description;
		this.statusCode = statusCode;
	}
	
	
}
