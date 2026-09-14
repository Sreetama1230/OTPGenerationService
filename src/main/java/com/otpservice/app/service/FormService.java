package com.otpservice.app.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.otpservice.app.conts.AppConstants;
import com.otpservice.app.dto.AuthSession;
import com.otpservice.app.dto.CustomerOTP;
import com.otpservice.app.dto.OTPResponse;
import com.otpservice.app.exception.InvalidRequest;
import com.otpservice.app.model.Form;
import com.otpservice.app.repo.FormRepository;

@Service
public class FormService {

	@Autowired
	private FormRepository repository;

	@Autowired
	private RedisService redisService;

	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

	public OTPResponse generateOTP(Form form) {

		verifyRequest(form);

		AuthSession isAlreadyExistedSession = redisService.get("username_" + form.getUsername(), AuthSession.class);

		if (isAlreadyExistedSession != null) {
			throw new InvalidRequest("There is already an active session: " + isAlreadyExistedSession.getSessionId());
		}

		String sessionid = UUID.randomUUID().toString();

		String newOpt = generateOTP();
		OTPResponse otpResponse = new OTPResponse();
		// store the session details
		AuthSession authSession = new AuthSession();
		authSession.setStartTime(LocalDateTime.now());
		authSession.setEndTime(authSession.getStartTime().plusMinutes(30));
		authSession.setSessionId(sessionid);
		authSession.setCurrentCount(1L);
		redisService.set("session_" + sessionid + "_username_" + form.getUsername(), authSession, 1800L);

		// want when we generate an OTP for the first time we will create a redis record
		// why? - a user can create the otp any number of times with /send end point
		// can hamper our service
		redisService.set("username_" + form.getUsername(), authSession, 1800L);
		// store the otp details

		if (repository.findByUsername(form.getUsername()).isEmpty()) {
			repository.save(form);
		}

		CustomerOTP customerOTP = new CustomerOTP(newOpt, AppConstants.MAXATTEMPTS - authSession.getCurrentCount(),
				sessionid, form.getUsername(), LocalDateTime.now(), LocalDateTime.now().plusMinutes(6));

		customerOTP.setVerifyCount(0L);
		otpResponse = OTPResponse.getOTPResponse(customerOTP);

		customerOTP.setOtp(hashOTP(newOpt));

		redisService.set("otp_sessionid_" + sessionid + "_username_" + form.getUsername(), customerOTP, 360L);

		return otpResponse;

	}

	// somehow we forgot the otp, requesting for another otp
	// already have an active otp but still asking for another one
	// will remove the existing otp record from the cache and add the new one
	public OTPResponse resendOTP(String username, String sessionid) { // sessionid will be provided in the request
																		// header

		Form form = repository.findByUsername(username).get();
		String newOpt = "";
		long operationsLeft = 0;

		CustomerOTP customerOTP = new CustomerOTP();
		OTPResponse otpResponse = new OTPResponse();

		AuthSession authSession = redisService.get("session_" + sessionid + "_username_" + form.getUsername(),
				AuthSession.class);

		if (authSession != null && authSession.getCurrentCount() >= AppConstants.MAXATTEMPTS) {

			throw new InvalidRequest("Maximum OTP requests exceeded");
		}

		if (authSession != null) {
//			if (authSession.getEndTime().isAfter(LocalDateTime.now())) { // session is active
			// authSession.getCurrentCount() < AppConstants.MAXATTEMPTS)
			newOpt = generateOTP();
			authSession.setCurrentCount(authSession.getCurrentCount() + 1);
			// again adding the updated session details
			Long remainingTTL = redisService.getRemainingTTL("session_" + sessionid + "_username_" + username);
			redisService.set("session_" + sessionid + "_username_" + form.getUsername(), authSession, remainingTTL);
			operationsLeft = AppConstants.MAXATTEMPTS - authSession.getCurrentCount();
			customerOTP = new CustomerOTP(newOpt, operationsLeft, sessionid, form.getUsername(), LocalDateTime.now(),
					LocalDateTime.now().plusMinutes(6));
			customerOTP.setVerifyCount(0L);
			// per 30 min 5 otps are allowed so per otp will be valid for 6min
			otpResponse = OTPResponse.getOTPResponse(customerOTP);
			customerOTP.setOtp(hashOTP(newOpt));
			// it should remove the existing one and add a new cache
			redisService.set("otp_sessionid_" + sessionid + "_username_" + form.getUsername(), customerOTP, 360L);

		} else {
			// because of TTL the record might be deleted, so creating a new session
			// before that we will check if there any active session with same same username
			// maybe the user intentionally providing invalid session id to create more otps
			AuthSession isAlreadyExistedSession = redisService.get("username_" + username, AuthSession.class);

			if (isAlreadyExistedSession != null) {
				throw new InvalidRequest("Please use active session id");
			}
			newOpt = generateOTP();
			authSession = new AuthSession();
			authSession.setStartTime(LocalDateTime.now());
			authSession.setEndTime(authSession.getStartTime().plusMinutes(30));
			// create new session id
			String newSessionid = UUID.randomUUID().toString();
			authSession.setSessionId(newSessionid);
			authSession.setCurrentCount(1L);
			operationsLeft = AppConstants.MAXATTEMPTS - authSession.getCurrentCount();
			redisService.set("session_" + newSessionid + "_username_" + form.getUsername(), authSession, 1800L);

			customerOTP = new CustomerOTP(newOpt, operationsLeft, newSessionid, form.getUsername(), LocalDateTime.now(),
					LocalDateTime.now().plusMinutes(6));
			customerOTP.setVerifyCount(0L);
			// per 30 min 5 otps are allowed so per otp will be valid for 6min
			otpResponse = OTPResponse.getOTPResponse(customerOTP);
			customerOTP.setOtp(hashOTP(newOpt));
			redisService.set("otp_sessionid_" + newSessionid + "_username_" + form.getUsername(), customerOTP, 360L);
			// creating new session with the username
			redisService.set("username_" + form.getUsername(), authSession, 1800L);
		}

		return otpResponse;

	}

	public String verifyOTP(String username, String sessionid, String providedOTP) {

		CustomerOTP customerOTP = redisService.get("otp_sessionid_" + sessionid + "_username_" + username,
				CustomerOTP.class);
		if (customerOTP == null) {
			throw new InvalidRequest("OTP Expired");
		}

		if (customerOTP.getVerifyCount() >= AppConstants.MAXVERIFICATIONCOUNTS) {
			redisService.delete("otp_sessionid_" + sessionid + "_username_" + username);
			throw new InvalidRequest("Maximum verification attempts exceeded");
		}

		customerOTP.setVerifyCount(customerOTP.getVerifyCount() + 1);

		if (hashOTP(providedOTP).equals(customerOTP.getOtp())) {
			// if the otp has been validated we can jsut remove all the cache records
			// no point of storing them
			redisService.delete("otp_sessionid_" + sessionid + "_username_" + username);
			redisService.delete("username_" + username);
			redisService.delete("session_" + sessionid + "_username_" + username);
			return "Valid OTP";
		}

		Long remainingTTL = redisService.getRemainingTTL("otp_sessionid_" + sessionid + "_username_" + username);
		redisService.set("otp_sessionid_" + sessionid + "_username_" + username, customerOTP, remainingTTL);

		throw new InvalidRequest("Invalid OTP");

	}

	public boolean verifyRequest(Form form) {

		if (form.getEmail() == null && form.getMobileNumber() == null) {
			throw new InvalidRequest("Please provide either email or mobilenumber");
		}

		if (form.getEmail() != null && !form.getEmail().contains("@")) {
			throw new InvalidRequest("Please provide valid email id");
		}

		if (form.getMobileNumber() != null && form.getMobileNumber().length() != 10) {
			throw new InvalidRequest("Please provide valid phone number");
		}

		return true;
	}

	public static String generateOTP() {
		// 6 digits
		int otp = 100000 + SECURE_RANDOM.nextInt(900000);
		return String.valueOf(otp);
	}

	public static String hashOTP(String otp) {
		return DigestUtils.sha256Hex(otp);
	}

}
