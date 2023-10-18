package com.back.mymontz.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.back.mymontz.exception.ResourceNotFoundException;
import com.back.mymontz.exception.UserDuplicateException;
import com.back.mymontz.exception.UserNotFoundException;
import com.back.mymontz.model.User;
import com.back.mymontz.repository.UserRepository;

@Component
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public User createUser(User user) {
		if (userRepository.existsByUsername(user.getUsername())) {
			throw new UserDuplicateException(user.getUsername());
		}

		user.setPassword(passwordEncoder.encode(user.getPassword()));

		return userRepository.save(user);
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
	public User getUserByUsername(String username) {
		User user = userRepository.findByUsername(username);
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
