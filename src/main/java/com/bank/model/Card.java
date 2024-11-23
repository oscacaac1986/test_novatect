package com.bank.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Card {
    @Id
    private String cardId;
    private String holderName;
    private LocalDateTime expirationDate;
    private Double balance;
    private Boolean isActive;
    private Boolean isBlocked;
    private String productId;
}
