package com.back.mymontz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.back.mymontz.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	boolean existsByUsername(String username);

	User findByUsername(String username);
}
