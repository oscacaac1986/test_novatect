package com.bank.dto;

import lombok.Data;

@Data
public class CardRequestDTO {
    private String cardId;
    private String holderName;
    private String productId;
}
