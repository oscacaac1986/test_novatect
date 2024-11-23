package com.bank.dto;

import java.time.LocalDateTime;

import com.bank.enums.TransactionStatus;

import lombok.Data;

@Data
public class TransactionResponseDTO {
    private String id;
    private String cardId;
    private Double price;
    private TransactionStatus status;
    private LocalDateTime transactionDate;
}
