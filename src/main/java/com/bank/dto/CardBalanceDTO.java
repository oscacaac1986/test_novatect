package com.bank.dto;

import lombok.Data;

@Data
public class CardBalanceDTO {
    private String cardId;
    private Double balance;
}
