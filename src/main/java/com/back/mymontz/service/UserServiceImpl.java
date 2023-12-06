package com.back.mymontz.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.back.mymontz.config.JwtService;
import com.back.mymontz.dto.AuthenticationRequest;
import com.back.mymontz.dto.AuthenticationResponse;
import com.back.mymontz.dto.RegisterRequest;
import com.back.mymontz.exception.ConstraintException;
import com.back.mymontz.exception.CustomException;
import com.back.mymontz.exception.DuplicateEntryException;
import com.back.mymontz.exception.ResourceNotFoundException;
import com.back.mymontz.exception.UnauthorizedException;
import com.back.mymontz.model.User;
import com.back.mymontz.repository.UserRepository;
import com.back.mymontz.util.Role;

import jakarta.validation.ConstraintViolationException;

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
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							request.getUsername(),
							request.getPassword()));
			var user = userRepository.findByUsername(request.getUsername())
					.orElseThrow();
			var jwtToken = jwtService.generateToken(user);
			return AuthenticationResponse.builder().token(jwtToken).build();
		} catch (Exception e) {
			throw new CustomException(e.getMessage(), e, HttpStatus.UNAUTHORIZED);
		}
	}

	@Override
	public AuthenticationResponse register(RegisterRequest request) {
		try {
			User user = User.builder().username(request.getUsername())
					.password(passwordEncoder.encode(request.getPassword())).email(request.getEmail()).role(Role.USER)
					.build();

			userRepository.save(user);

			var jwtToken = jwtService.generateToken(user);

			return AuthenticationResponse.builder().token(jwtToken).build();
		} catch (DataIntegrityViolationException e) {
			throw new DuplicateEntryException("Duplicate entry email or username", e);
		} catch (ConstraintViolationException e) {
			throw new ConstraintException(e.getMessage(), e);
		}
	}

	@Override
	public User getUserById(Long id) {
		Optional<User> user = userRepository.findById(id);
		if (user.isPresent()) {
			return user.get();
		} else {
			throw new ResourceNotFoundException("User not exist with id: " + id);
		}
	}

	@Override
	public User getUserByUsername(String username) {
		Optional<User> user = userRepository.findByUsername(username);
		if (user.isPresent()) {
			return user.get();
		} else {
			throw new ResourceNotFoundException("User not exist with username: " + username);
		}
	}

	@Override
	public User updateUser(Long id, User user) {
		Optional<User> existingUser = userRepository.findById(id);

		try {
			this.checkAuthorization(existingUser.get().getId());
			existingUser.get().setUsername(user.getUsername());
			existingUser.get().setEmail(user.getEmail());

			if (user.getPassword() != null && !user.getPassword().isEmpty()) {
				String newEncryptedPwd = passwordEncoder.encode(user.getPassword());
				existingUser.get().setPassword(newEncryptedPwd);
			}

			User u = existingUser.get();
			return userRepository.save(u);
		} catch (DataIntegrityViolationException e) {
			String msg = e.getMessage().substring(0, e.getMessage().indexOf("]") + 1);
			throw new ConstraintException(msg, e);
		}
	}

	@Override
	public void deleteUserById(Long id) {
		if (!userRepository.existsById(id)) {
			throw new ResourceNotFoundException("User not exist with id: " + id);
		}
		userRepository.deleteById(id);
	}
	
	public void checkAuthorization(Long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Long authenticatedUserId = ((User) authentication.getPrincipal()).getId();
		
		if (!id.equals(authenticatedUserId)) {
			throw new UnauthorizedException("You are not authorized to create, access or modify other users data");
		}
	}
}
