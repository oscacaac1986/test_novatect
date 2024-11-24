package com.bank.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.bank.dto.TransactionRequestDTO;
import com.bank.dto.TransactionResponseDTO;
import com.bank.enums.TransactionStatus;
import com.bank.model.Transaction;

class TransactionMapperTest {

    private final TransactionMapper mapper = new TransactionMapper();

    @Test
    void toDTO_Success() {
        Transaction transaction = new Transaction();
        transaction.setId("trans123");
        transaction.setCardId("1234567890123456");
        transaction.setPrice(100.0);
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setTransactionDate(LocalDateTime.now());

        TransactionResponseDTO dto = mapper.toDTO(transaction);

        assertNotNull(dto);
        assertEquals(transaction.getId(), dto.getId());
        assertEquals(transaction.getCardId(), dto.getCardId());
        assertEquals(transaction.getPrice(), dto.getPrice());
        assertEquals(transaction.getStatus(), dto.getStatus());
    }

    @Test
    void toDTO_NullTransaction() {
        assertNull(mapper.toDTO(null));
    }

    @Test
    void toEntity_Success() {
        TransactionRequestDTO dto = new TransactionRequestDTO();
        dto.setCardId("1234567890123456");
        dto.setPrice(100.0);

        Transaction transaction = mapper.toEntity(dto);

        assertNotNull(transaction);
        assertEquals(dto.getCardId(), transaction.getCardId());
        assertEquals(dto.getPrice(), transaction.getPrice());
        assertEquals(TransactionStatus.COMPLETED, transaction.getStatus());
        assertNotNull(transaction.getTransactionDate());
    }

    @Test
    void toEntity_NullDTO() {
        assertNull(mapper.toEntity(null));
    }
}
