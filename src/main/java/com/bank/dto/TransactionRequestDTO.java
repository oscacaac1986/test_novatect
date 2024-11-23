package com.bank.dto;

import lombok.Data;

@Data
public class TransactionRequestDTO {
    private String cardId;
    private Double price;
}
