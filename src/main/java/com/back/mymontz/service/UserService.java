package com.back.mymontz.service;

import org.springframework.stereotype.Service;

import com.back.mymontz.model.User;

@Service
public interface UserService {

	User createUser(User customer);
	
	User getUserById(Long id);
	
	User getUserByUsername(String username);
	
	User updateUser(Long id, User user);
	
	void deleteUserById(Long id);
}
