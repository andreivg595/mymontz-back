package com.back.mymontz.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.back.mymontz.dto.AuthenticationRequest;
import com.back.mymontz.dto.AuthenticationResponse;
import com.back.mymontz.dto.RegisterRequest;
import com.back.mymontz.model.User;
import com.back.mymontz.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class UserController {

	@Autowired
	private final UserService userService;
	
	@PostMapping("/auth/login")
	public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
		return ResponseEntity.ok(userService.login(request));
	}
	
	@PostMapping("/auth/register")
	public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
		return ResponseEntity.ok(userService.register(request));
	}
	
	@GetMapping("/user/{id}")
	public User getUserById(@PathVariable Long id) {
		return userService.getUserById(id);
	}
	
	@GetMapping("/user/username/{username}")
	public Optional<User> getUserById(@PathVariable String username) {
		return userService.getUserByUsername(username);
	}
	
	@PutMapping("/user/update/{id}")
	public User updateUser(@PathVariable Long id, @RequestBody User user) {
		return userService.updateUser(id, user);
	}

	@DeleteMapping("/user/{id}")
	public void deleteUser(@PathVariable Long id) {
		userService.deleteUserById(id);
	}
}
