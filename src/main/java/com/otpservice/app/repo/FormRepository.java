package com.otpservice.app.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.otpservice.app.model.Form;

@Repository
public interface FormRepository extends JpaRepository<Form , Long> {

	
	public Optional<Form> findByUsername(String username);
}
