package com.bank.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.bank.dto.CardRequestDTO;
import com.bank.dto.CardResponseDTO;
import com.bank.model.Card;

@Component
public class CardMapper {

    public CardResponseDTO toDTO(Card card) {
        if (card == null) return null;
        
        CardResponseDTO dto = new CardResponseDTO();
        dto.setCardId(card.getCardId());
        dto.setHolderName(card.getHolderName());
        dto.setBalance(card.getBalance());
        dto.setIsActive(card.getIsActive());
        dto.setIsBlocked(card.getIsBlocked());
        dto.setExpirationDate(card.getExpirationDate());
        return dto;
    }

    public Card toEntity(CardRequestDTO dto) {
        if (dto == null) return null;
        
        Card card = new Card();
        card.setCardId(dto.getCardId());
        card.setHolderName(dto.getHolderName());
        card.setProductId(dto.getProductId());
        card.setBalance(0.0);
        card.setIsActive(false);
        card.setIsBlocked(false);
        card.setExpirationDate(LocalDateTime.now().plusYears(3));
        return card;
    }
}
