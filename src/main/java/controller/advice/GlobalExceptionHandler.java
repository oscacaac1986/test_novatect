package com.bank.controller.advice;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.bank.dto.ErrorResponseDTO;
import com.bank.exception.CardNotFoundException;
import com.bank.exception.InvalidOperationException;
import com.bank.exception.TransactionNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleCardNotFoundException(CardNotFoundException ex) {
        return new ResponseEntity<>(
            new ErrorResponseDTO(
                LocalDateTime.now(),
                "Card not found",
                ex.getMessage()
            ),
            HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidOperationException(InvalidOperationException ex) {
        return new ResponseEntity<>(
            new ErrorResponseDTO(
                LocalDateTime.now(),
                "Invalid operation",
                ex.getMessage()
            ),
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleTransactionNotFoundException(TransactionNotFoundException ex) {
        return new ResponseEntity<>(
            new ErrorResponseDTO(
                LocalDateTime.now(),
                "Transaction not found",
                ex.getMessage()
            ),
            HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGlobalException(Exception ex) {
        return new ResponseEntity<>(
            new ErrorResponseDTO(
                LocalDateTime.now(),
                "Internal server error",
                ex.getMessage()
            ),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}