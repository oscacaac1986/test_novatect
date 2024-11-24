package com.bank.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.dto.TransactionCancellationDTO;
import com.bank.dto.TransactionRequestDTO;
import com.bank.dto.TransactionResponseDTO;
import com.bank.enums.TransactionStatus;
import com.bank.exception.InvalidOperationException;
import com.bank.exception.TransactionNotFoundException;
import com.bank.mapper.TransactionMapper;
import com.bank.model.Card;
import com.bank.model.Transaction;
import com.bank.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final CardService cardService;
    private final TransactionMapper transactionMapper;

    @Transactional
    public TransactionResponseDTO processPurchase(TransactionRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new InvalidOperationException("Transaction request cannot be null");
        }

        Card card = cardService.findCard(requestDTO.getCardId());
        
        if (!cardService.isValidForTransaction(card, requestDTO.getPrice())) {
            throw new InvalidOperationException("Invalid transaction: Check card status and balance");
        }

        Transaction transaction = new Transaction();
        transaction.setCardId(requestDTO.getCardId());
        transaction.setPrice(requestDTO.getPrice());
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setStatus(TransactionStatus.COMPLETED);

        card.setBalance(card.getBalance() - requestDTO.getPrice());
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toDTO(savedTransaction);
    }

    public TransactionResponseDTO getTransaction(String transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));
        return transactionMapper.toDTO(transaction);
    }

    @Transactional
    public void cancelTransaction(TransactionCancellationDTO cancellationDTO) {
        Transaction transaction = transactionRepository.findById(cancellationDTO.getTransactionId())
            .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));

        // Validar que la tarjeta sea la misma de la transacción
        if (!transaction.getCardId().equals(cancellationDTO.getCardId())) {
            throw new InvalidOperationException("Card ID does not match transaction");
        }

        validateCancellation(transaction);

        Card card = cardService.findCard(cancellationDTO.getCardId());
        card.setBalance(card.getBalance() + transaction.getPrice());
        
        transaction.setStatus(TransactionStatus.CANCELLED);
        transaction.setLastModifiedDate(LocalDateTime.now());
        
        transactionRepository.save(transaction);
    }

    private void validateCancellation(Transaction transaction) {
        if (transaction.getStatus() == TransactionStatus.CANCELLED) {
            throw new InvalidOperationException("Transaction is already cancelled");
        }

        LocalDateTime cancellationDeadline = transaction.getTransactionDate().plusHours(24);
        if (LocalDateTime.now().isAfter(cancellationDeadline)) {
            throw new InvalidOperationException("Transaction cannot be cancelled after 24 hours");
        }
    }
}