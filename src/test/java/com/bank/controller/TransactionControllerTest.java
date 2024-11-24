package com.bank.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.bank.dto.TransactionCancellationDTO;
import com.bank.dto.TransactionRequestDTO;
import com.bank.dto.TransactionResponseDTO;
import com.bank.enums.TransactionStatus;
import com.bank.exception.InvalidOperationException;
import com.bank.exception.TransactionNotFoundException;
import com.bank.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private TransactionRequestDTO purchaseRequest;
    private TransactionResponseDTO transactionResponse;
    private TransactionCancellationDTO cancellationRequest;

    @BeforeEach
    void setUp() {
        purchaseRequest = new TransactionRequestDTO();
        purchaseRequest.setCardId("1234567890123456");
        purchaseRequest.setPrice(100.0);

        transactionResponse = new TransactionResponseDTO();
        transactionResponse.setId("trans123");
        transactionResponse.setCardId("1234567890123456");
        transactionResponse.setPrice(100.0);
        transactionResponse.setStatus(TransactionStatus.COMPLETED);

        cancellationRequest = new TransactionCancellationDTO();
        cancellationRequest.setCardId("1234567890123456");
        cancellationRequest.setTransactionId("trans123");
    }

    @Test
    void processPurchase_Success() throws Exception {
        when(transactionService.processPurchase(any(TransactionRequestDTO.class)))
            .thenReturn(transactionResponse);

        mockMvc.perform(post("/transaction/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(purchaseRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("trans123"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void processPurchase_InvalidOperation() throws Exception {
        when(transactionService.processPurchase(any(TransactionRequestDTO.class)))
            .thenThrow(new InvalidOperationException("Invalid transaction"));

        mockMvc.perform(post("/transaction/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(purchaseRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTransaction_Success() throws Exception {
        when(transactionService.getTransaction("trans123"))
            .thenReturn(transactionResponse);

        mockMvc.perform(get("/transaction/trans123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("trans123"));
    }

    @Test
    void getTransaction_NotFound() throws Exception {
        when(transactionService.getTransaction("trans123"))
            .thenThrow(new TransactionNotFoundException("Transaction not found"));

        mockMvc.perform(get("/transaction/trans123"))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancelTransaction_Success() throws Exception {
        mockMvc.perform(post("/transaction/anulation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancellationRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void cancelTransaction_InvalidOperation() throws Exception {
        doThrow(new InvalidOperationException("Cannot cancel transaction"))
            .when(transactionService).cancelTransaction(any(TransactionCancellationDTO.class));

        mockMvc.perform(post("/transaction/anulation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancellationRequest)))
                .andExpect(status().isBadRequest());
    }
}
