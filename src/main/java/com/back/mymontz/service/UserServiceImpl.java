package com.back.mymontz.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.back.mymontz.config.JwtService;
import com.back.mymontz.dto.AuthenticationRequest;
import com.back.mymontz.dto.AuthenticationResponse;
import com.back.mymontz.dto.RegisterRequest;
import com.back.mymontz.exception.ResourceNotFoundException;
import com.back.mymontz.exception.UserDuplicateException;
import com.back.mymontz.exception.UserNotFoundException;
import com.back.mymontz.model.User;
import com.back.mymontz.repository.UserRepository;
import com.back.mymontz.util.Role;

@Component
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Override
	public AuthenticationResponse login(AuthenticationRequest request) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						request.getUsername(),
						request.getPassword()));
		var user = userRepository.findByUsername(request.getUsername())
				.orElseThrow();
		var jwtToken = jwtService.generateToken(user);
		return AuthenticationResponse.builder().token(jwtToken).build();
	}
	
	@Override
	public AuthenticationResponse register(RegisterRequest request) {
		User user = User.builder()
				.username(request.getUsername())
				.password(passwordEncoder.encode(request.getPassword()))
				.email(request.getEmail())
				.role(Role.USER)
				.build();
		
		userRepository.save(user);
		
		var jwtToken = jwtService.generateToken(user);
		
		return AuthenticationResponse.builder()
				.token(jwtToken)
				.build();
	}

	@Override
	public User getUserById(Long id) {
		Optional<User> usuarioOptional = userRepository.findById(id);
		if (usuarioOptional.isPresent()) {
			return usuarioOptional.get();
		} else {
			throw new UserNotFoundException(id);
		}
	}

	@Override
	public Optional<User> getUserByUsername(String username) {
		Optional<User> user = userRepository.findByUsername(username);
		if (user != null) {
			return user;
		} else {
			throw new ResourceNotFoundException("User not exist with username: " + username);
		}
	}

	@Override
	public User updateUser(Long id, User user) {
		User existingUser = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));

		if (!existingUser.getUsername().equals(user.getUsername())
				&& userRepository.existsByUsername(user.getUsername())) {
			throw new UserDuplicateException(user.getUsername());
		}

		existingUser.setUsername(user.getUsername());
		existingUser.setEmail(user.getEmail());

		if (user.getPassword() != null && !user.getPassword().isEmpty()) {
			String newEncryptedPwd = passwordEncoder.encode(user.getPassword());
			existingUser.setPassword(newEncryptedPwd);
		} 

		return userRepository.save(existingUser);
	}

	@Override
	public void deleteUserById(Long id) {
		if (!userRepository.existsById(id)) {
			throw new UserNotFoundException(id);
		}
		userRepository.deleteById(id);
	}
}
