package com.otpservice.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Form {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long clientid;
	@Column(unique = true)
	private String username;
	@Column(nullable = false)
	private String password;
	@Column(nullable = true , unique = true)
	private String mobileNumber;
	@Column(nullable = true , unique = true)
	private String email;

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Long getClientid() {
		return clientid;
	}
	public Form() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Form(Long clientid, String username, String password, String mobileNumber, String email) {
		super();
		this.clientid = clientid;
		this.username = username;
		this.password = password;
		this.mobileNumber = mobileNumber;
		this.email = email;
	}


	
}
