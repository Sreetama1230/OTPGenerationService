# OTPGenerationService

This is a Spring Boot project to implement an OTP generation system with Redis.

## How does the flow go?

We have a total of 3 APIs:

1. `/send`
2. `/resend`
3. `/verify`

For security purposes, we have stored the OTPs as hashed values. Used `sha256` algorithm for hashing and it is one way hashing technique. And already verifying the the provided email and mobile number. We need to send either mobile number or email id to generate the OTP. Here OTP is the combination of `6` digit numbers.

## 1. `/send` Endpoint

- When a username does not exist, one session will be created with the username `username_<username>`.
- After that, whether the username exists or does not exist, another session will be created with the `session_<sessionid>_username_<username>` key to store the session details.
- After generating the OTP, another session will be created with the `otp_sessionid_<sessionid>_username<username>` key to basically store the OTP details.
- If an existing user requests for OTP flow will be same just will not save the user details.

## 2. `/resend` Endpoint

The `/resend` endpoint expects an existing user who is requesting an OTP. If that does not matter much if the particular user does not have an active session will resend an OTP.

- The maximum number of attempts possible is **5**. After that, the user needs to wait for another active session that could be activated after **20–30 minutes**.
- Each OTP will be valid for **6 minutes**.
- The session will be valid for **30 minutes**.
- Whenever a new OTP is requested, a new session will be created for the OTP. (Override the old one)
- The remaining 2 sessions will be updated with the remaining TTL value, if required.
- After **20–30 minutes**, the user can again hit the same endpoint, and the new sessions will be created.
- Now, suppose the user is doing some malfunctioning and intentionally providing a wrong session ID and there is an active session present with their username. In that case, the request will be rejected with a **400 error**.

## 3. `/verify` Endpoint

- The `/verify` endpoint will fetch the data from the cache and check it against the provided OTP.
- The total maximum verification count is **5**, including both success and failure.
- If the OTP is validated successfully, just return a success response and remove the related sessions.
- Otherwise, will update the otp cache record with updated verification count and remaining TTL value of the valid current otp. 
- If the verification count exceeds the maximum verification attempts, we will stop the request for that session and let the user know to wait.

## Technology Stack
- Java
- SpringBoot
- Redis
- MySQL
