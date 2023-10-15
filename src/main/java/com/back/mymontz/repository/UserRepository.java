package com.back.mymontz.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.back.mymontz.model.Expense;
import com.back.mymontz.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	List<Expense> findByUserId(Long userId);
    List<Expense> findByDateBetweenAndUserId(LocalDate startDate, LocalDate endDate, Long usuarioId);
}
