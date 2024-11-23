package com.bank.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CardResponseDTO {
    private String cardId;
    private String holderName;
    private Double balance;
    private Boolean isActive;
    private Boolean isBlocked;
    private LocalDateTime expirationDate;
}
