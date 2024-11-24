package com.bank.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.bank.controller.advice.GlobalExceptionHandler;
import com.bank.dto.ErrorResponseDTO;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleCardNotFoundException() {
        String errorMessage = "Card not found";
        CardNotFoundException exception = new CardNotFoundException(errorMessage);
        
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleCardNotFoundException(exception);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(errorMessage, response.getBody().getMessage());
    }

    @Test
    void handleInvalidOperationException() {
        String errorMessage = "Invalid operation";
        InvalidOperationException exception = new InvalidOperationException(errorMessage);
        
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleInvalidOperationException(exception);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(errorMessage, response.getBody().getMessage());
    }

    @Test
    void handleTransactionNotFoundException() {
        String errorMessage = "Transaction not found";
        TransactionNotFoundException exception = new TransactionNotFoundException(errorMessage);
        
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleTransactionNotFoundException(exception);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(errorMessage, response.getBody().getMessage());
    }

    @Test
    void handleGenericException() {
        String errorMessage = "Unexpected error";
        Exception exception = new Exception(errorMessage);
        
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleGenericException(exception);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Internal server error"));
    }
}