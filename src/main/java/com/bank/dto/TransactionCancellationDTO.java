package com.bank.dto;

import lombok.Data;

@Data
public class TransactionCancellationDTO {
    private String cardId;
    private String transactionId;
    
}
