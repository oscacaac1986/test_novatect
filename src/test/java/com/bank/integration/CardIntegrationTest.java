package com.bank.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.bank.dto.CardBalanceDTO;
import com.bank.dto.CardEnrollmentDTO;
import com.bank.model.Card;
import com.bank.repository.CardRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CardIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void fullCardLifecycle() throws Exception {
        // 1. Generate card
        String productId = "123456";
        String cardId = mockMvc.perform(get("/card/{productId}/number", productId))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        // 2. Activate card
        CardEnrollmentDTO enrollmentDTO = new CardEnrollmentDTO();
        enrollmentDTO.setCardId(cardId);

        mockMvc.perform(post("/card/enroll")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(enrollmentDTO)))
            .andExpect(status().isOk());

        // 3. Recharge balance
        CardBalanceDTO balanceDTO = new CardBalanceDTO();
        balanceDTO.setCardId(cardId);
        balanceDTO.setBalance(1000.0);

        mockMvc.perform(post("/card/balance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(balanceDTO)))
            .andExpect(status().isOk());

        // 4. Check balance
        mockMvc.perform(get("/card/balance/{cardId}", cardId))
            .andExpect(status().isOk())
            .andExpect(content().string("1000.0"));

        // 5. Block card
        mockMvc.perform(delete("/card/{cardId}", cardId))
            .andExpect(status().isOk());

        // 6. Verify card is blocked
        Card card = cardRepository.findById(cardId).orElseThrow();
        assert card.getIsBlocked();
    }
}
