package com.otpservice.app.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Service;

import tools.jackson.databind.ObjectMapper;

@Service
public class RedisService {

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	public <T> T get(String key, Class<T> customClass) {
		Object object = redisTemplate.opsForValue().get(key);
		if (object == null) {
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(object.toString(), customClass);
	}

	public void set(String key, Object o, Long ttl) {
		ObjectMapper mapper = new ObjectMapper();
		String jsonValue = mapper.writeValueAsString(o);
		redisTemplate.opsForValue().set(key, jsonValue, Expiration.from(ttl, TimeUnit.SECONDS));

	}
	
	public void delete(String key) {
		redisTemplate.opsForValue().getAndDelete(key);

	}
	
	public Long getRemainingTTL(String key) {
		return redisTemplate.getExpire(key, TimeUnit.SECONDS);
	}
	
	
}
