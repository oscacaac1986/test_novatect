package com.bank.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.dto.CardBalanceDTO;
import com.bank.exception.CardNotFoundException;
import com.bank.exception.InvalidOperationException;
import com.bank.mapper.CardMapper;
import com.bank.model.Card;
import com.bank.repository.CardRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final CardMapper cardMapper;

    public String generateCardNumber(String productId) {
        StringBuilder cardNumber = new StringBuilder(productId);
        while (cardNumber.length() < 16) {
            cardNumber.append((int) (Math.random() * 10));
        }
        String generatedNumber = cardNumber.toString();
        
        // Crear nueva tarjeta
        Card card = new Card();
        card.setCardId(generatedNumber);
        card.setProductId(productId);
        card.setExpirationDate(LocalDateTime.now().plusYears(3));
        card.setIsActive(false);
        card.setIsBlocked(false);
        card.setBalance(0.0);
        
        cardRepository.save(card);
        return generatedNumber;
    }

    @Transactional
    public void activateCard(String cardId) {
        Card card = findCard(cardId);
        if (card.getIsActive()) {
            throw new InvalidOperationException("Card is already active");
        }
        card.setIsActive(true);
        cardRepository.save(card);
    }

    @Transactional
    public void blockCard(String cardId) {
        Card card = findCard(cardId);
        card.setIsBlocked(true);
        cardRepository.save(card);
    }

    @Transactional
    public void rechargeBalance(CardBalanceDTO balanceDTO) {
        Card card = findCard(balanceDTO.getCardId());
        if (card.getIsBlocked()) {
            throw new InvalidOperationException("Card is blocked");
        }
        card.setBalance(card.getBalance() + balanceDTO.getBalance());
        cardRepository.save(card);
    }

    public Double checkBalance(String cardId) {
        return findCard(cardId).getBalance();
    }

    public Card findCard(String cardId) {
        return cardRepository.findById(cardId)
            .orElseThrow(() -> new CardNotFoundException("Card not found: " + cardId));
    }

    public boolean isValidForTransaction(Card card, Double amount) {
        return card.getIsActive() &&
               !card.getIsBlocked() &&
               card.getBalance() >= amount &&
               card.getExpirationDate().isAfter(LocalDateTime.now());
    }
}