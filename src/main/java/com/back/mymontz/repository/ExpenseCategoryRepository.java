package com.back.mymontz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.back.mymontz.model.ExpenseCategory;
import com.back.mymontz.util.Type;

@Repository
public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Long> {
	boolean existsByType(Type type);
}
