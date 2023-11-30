package com.back.mymontz.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.back.mymontz.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	boolean existsByUsername(String username);
	
	Optional<User> findByUsername(String username);
	
	Optional<User> findByEmail(String email);
}
