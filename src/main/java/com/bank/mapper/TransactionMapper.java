package com.bank.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.bank.dto.TransactionRequestDTO;
import com.bank.dto.TransactionResponseDTO;
import com.bank.enums.TransactionStatus;
import com.bank.model.Transaction;

@Component
public class TransactionMapper {
    
    public TransactionResponseDTO toDTO(Transaction transaction) {
        if (transaction == null) return null;
        
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setId(transaction.getId());
        dto.setCardId(transaction.getCardId());
        dto.setPrice(transaction.getPrice());
        dto.setStatus(transaction.getStatus());
        dto.setTransactionDate(transaction.getTransactionDate());
        return dto;
    }
    
    public Transaction toEntity(TransactionRequestDTO dto) {
        if (dto == null) return null;
        
        Transaction transaction = new Transaction();
        transaction.setCardId(dto.getCardId());
        transaction.setPrice(dto.getPrice());
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setTransactionDate(LocalDateTime.now());
        return transaction;
    }
}