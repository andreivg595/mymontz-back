package com.back.mymontz.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseRequest {

    private Double amount;
    private Long userId;
    private Long categoryId;
    private String date;
    private String note;
    private MultipartFile imageFile;
}
