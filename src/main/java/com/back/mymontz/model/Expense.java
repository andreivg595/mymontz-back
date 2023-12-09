package com.back.mymontz.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "expense")
public class Expense {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
    @ManyToOne
    @jakarta.persistence.JoinColumn(name = "FK_USER", referencedColumnName = "id", nullable = false)
    private User user;
    
    @ManyToOne
    @jakarta.persistence.JoinColumn(name = "FK_CATEGORY", referencedColumnName = "id", nullable = false)
    private ExpenseCategory category;
    
    @Column(name = "date", nullable = false)
    private LocalDate date;
    
    @Column(name = "amount", nullable = false)
    private double amount;
    
    @Column(name = "note")
    private String note;
    
    @Lob
    @Column(name = "image", columnDefinition = "LONGBLOB")
	private byte[] image;
}
