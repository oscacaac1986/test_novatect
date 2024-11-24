package com.bank.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.bank.dto.CardBalanceDTO;
import com.bank.dto.CardEnrollmentDTO;
import com.bank.exception.CardNotFoundException;
import com.bank.exception.InvalidOperationException;
import com.bank.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CardController.class)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @Autowired
    private ObjectMapper objectMapper;

    private String validCardId;
    private CardEnrollmentDTO enrollmentDTO;
    private CardBalanceDTO balanceDTO;

    @BeforeEach
    void setUp() {
        validCardId = "1234567890123456";
        
        enrollmentDTO = new CardEnrollmentDTO();
        enrollmentDTO.setCardId(validCardId);
        
        balanceDTO = new CardBalanceDTO();
        balanceDTO.setCardId(validCardId);
        balanceDTO.setBalance(1000.0);
    }

    @Test
    void generateCardNumber_Success() throws Exception {
        String productId = "123456";
        when(cardService.generateCardNumber(productId)).thenReturn(validCardId);

        mockMvc.perform(get("/card/{productId}/number", productId))
                .andExpect(status().isOk())
                .andExpect(content().string(validCardId));
    }

    @Test
    void generateCardNumber_InvalidProductId() throws Exception {
        String invalidProductId = "12345"; // menos de 6 dígitos
        when(cardService.generateCardNumber(invalidProductId))
                .thenThrow(new InvalidOperationException("Invalid product ID"));

        mockMvc.perform(get("/card/{productId}/number", invalidProductId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void activateCard_Success() throws Exception {
        doNothing().when(cardService).activateCard(validCardId);

        mockMvc.perform(post("/card/enroll")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(enrollmentDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void activateCard_NotFound() throws Exception {
        doThrow(new CardNotFoundException("Card not found"))
                .when(cardService).activateCard(validCardId);

        mockMvc.perform(post("/card/enroll")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(enrollmentDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void activateCard_AlreadyActive() throws Exception {
        doThrow(new InvalidOperationException("Card already active"))
                .when(cardService).activateCard(validCardId);

        mockMvc.perform(post("/card/enroll")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(enrollmentDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void blockCard_Success() throws Exception {
        doNothing().when(cardService).blockCard(validCardId);

        mockMvc.perform(delete("/card/{cardId}", validCardId))
                .andExpect(status().isOk());
    }

    @Test
    void blockCard_NotFound() throws Exception {
        doThrow(new CardNotFoundException("Card not found"))
                .when(cardService).blockCard(validCardId);

        mockMvc.perform(delete("/card/{cardId}", validCardId))
                .andExpect(status().isNotFound());
    }

    @Test
    void rechargeBalance_Success() throws Exception {
        doNothing().when(cardService).rechargeBalance(any(CardBalanceDTO.class));

        mockMvc.perform(post("/card/balance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(balanceDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void rechargeBalance_CardBlocked() throws Exception {
        doThrow(new InvalidOperationException("Card is blocked"))
                .when(cardService).rechargeBalance(any(CardBalanceDTO.class));

        mockMvc.perform(post("/card/balance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(balanceDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void checkBalance_Success() throws Exception {
        when(cardService.checkBalance(validCardId)).thenReturn(1000.0);

        mockMvc.perform(get("/card/balance/{cardId}", validCardId))
                .andExpect(status().isOk())
                .andExpect(content().string("1000.0"));
    }

    @Test
    void checkBalance_NotFound() throws Exception {
        when(cardService.checkBalance(validCardId))
                .thenThrow(new CardNotFoundException("Card not found"));

        mockMvc.perform(get("/card/balance/{cardId}", validCardId))
                .andExpect(status().isNotFound());
    }
}
