package com.back.mymontz.service;

import org.springframework.stereotype.Service;

import com.back.mymontz.dto.AuthenticationRequest;
import com.back.mymontz.dto.AuthenticationResponse;
import com.back.mymontz.dto.RegisterRequest;
import com.back.mymontz.model.User;

@Service
public interface UserService {
	
	AuthenticationResponse login(AuthenticationRequest request);

	AuthenticationResponse register(RegisterRequest request);
	
	User getUserById(Long id);
	
	User getUserByUsername(String username);
	
	User updateUser(Long id, User user);
	
	void deleteUserById(Long id);
}
