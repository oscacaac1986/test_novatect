package com.bank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CardService cardService;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionService transactionService;

    private Transaction testTransaction;
    private Card testCard;
    private TransactionRequestDTO testRequest;
    private TransactionResponseDTO testResponse;
    private String validCardId = "123456789012345";
    private String validTransactionId = "trans123";

    @BeforeEach
    void setUp() {
        // Configurar Card
        testCard = new Card();
        testCard.setCardId(validCardId);
        testCard.setBalance(1000.0);
        testCard.setIsActive(true);
        testCard.setIsBlocked(false);
        testCard.setExpirationDate(LocalDateTime.now().plusYears(3));

        // Configurar Transaction
        testTransaction = new Transaction();
        testTransaction.setId(validTransactionId);
        testTransaction.setCardId(validCardId);
        testTransaction.setPrice(100.0);
        testTransaction.setTransactionDate(LocalDateTime.now());
        testTransaction.setStatus(TransactionStatus.COMPLETED);

        // Configurar TransactionRequestDTO
        testRequest = new TransactionRequestDTO();
        testRequest.setCardId(validCardId);
        testRequest.setPrice(100.0);

        // Configurar TransactionResponseDTO
        testResponse = new TransactionResponseDTO();
        testResponse.setId(validTransactionId);
        testResponse.setCardId(validCardId);
        testResponse.setPrice(100.0);
        testResponse.setStatus(TransactionStatus.COMPLETED);
        testResponse.setTransactionDate(testTransaction.getTransactionDate());
    }

    @Test
    void processPurchase_Success() {
        when(cardService.findCard(validCardId)).thenReturn(testCard);
        when(cardService.isValidForTransaction(any(Card.class), anyDouble())).thenReturn(true);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);
        when(transactionMapper.toDTO(any(Transaction.class))).thenReturn(testResponse);

        TransactionResponseDTO response = transactionService.processPurchase(testRequest);

        assertNotNull(response);
        assertEquals(TransactionStatus.COMPLETED, response.getStatus());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void processPurchase_InvalidTransaction() {
        when(cardService.findCard(validCardId)).thenReturn(testCard);
        when(cardService.isValidForTransaction(any(Card.class), anyDouble())).thenReturn(false);

        assertThrows(InvalidOperationException.class, () -> 
            transactionService.processPurchase(testRequest)
        );
    }

    @Test
    void getTransaction_Success() {
        when(transactionRepository.findById(validTransactionId))
            .thenReturn(Optional.of(testTransaction));
        when(transactionMapper.toDTO(testTransaction)).thenReturn(testResponse);

        TransactionResponseDTO response = transactionService.getTransaction(validTransactionId);

        assertNotNull(response);
        assertEquals(validTransactionId, response.getId());
    }

    @Test
    void getTransaction_NotFound() {
        when(transactionRepository.findById(validTransactionId))
            .thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () -> 
            transactionService.getTransaction(validTransactionId)
        );
    }

    @Test
void processPurchase_ValidatesAmount() {
    testRequest.setPrice(-100.0);
    
    assertThrows(InvalidOperationException.class, () ->
        transactionService.processPurchase(testRequest)
    );
}

@Test
void processPurchase_ValidatesCard() {
    when(cardService.findCard(validCardId)).thenReturn(testCard);
    testCard.setIsActive(false);
    
    assertThrows(InvalidOperationException.class, () ->
        transactionService.processPurchase(testRequest)
    );
}

@Test
void cancelTransaction_ValidatesCardOwnership() {
    Transaction transaction = new Transaction();
    transaction.setCardId("differentCardId");
    transaction.setTransactionDate(LocalDateTime.now());
    transaction.setStatus(TransactionStatus.COMPLETED);
    
    when(transactionRepository.findById(validTransactionId))
        .thenReturn(Optional.of(transaction));
    
    assertThrows(InvalidOperationException.class, () ->
        transactionService.cancelTransaction(
            new TransactionCancellationDTO(validCardId, validTransactionId)
        )
    );
}



}