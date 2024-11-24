package com.bank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
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

import com.bank.dto.CardBalanceDTO;
import com.bank.exception.CardNotFoundException;
import com.bank.exception.InvalidOperationException;
import com.bank.model.Card;
import com.bank.repository.CardRepository;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardService cardService;

    private Card testCard;
    private String validCardId = "123456789012345";
    private String validProductId = "123456";
    private CardBalanceDTO balanceDTO;

    @BeforeEach
    void setUp() {
        testCard = new Card();
        testCard.setCardId(validCardId);
        testCard.setProductId(validProductId);
        testCard.setHolderName("John Doe");
        testCard.setBalance(1000.0);
        testCard.setIsActive(true);
        testCard.setIsBlocked(false);
        testCard.setExpirationDate(LocalDateTime.now().plusYears(3));

        balanceDTO = new CardBalanceDTO();
        balanceDTO.setCardId(validCardId);
        balanceDTO.setBalance(500.0);
    }

    @Test
    void generateCardNumber_Success() {
        when(cardRepository.save(any(Card.class))).thenReturn(testCard);
        String cardNumber = cardService.generateCardNumber(validProductId);
        
        assertNotNull(cardNumber);
        assertEquals(16, cardNumber.length());
        assertTrue(cardNumber.startsWith(validProductId));
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void activateCard_Success() {
        testCard.setIsActive(false);
        when(cardRepository.findById(validCardId)).thenReturn(Optional.of(testCard));
        when(cardRepository.save(any(Card.class))).thenReturn(testCard);

        cardService.activateCard(validCardId);

        assertTrue(testCard.getIsActive());
        verify(cardRepository).save(testCard);
    }

    @Test
    void activateCard_AlreadyActive() {
        when(cardRepository.findById(validCardId)).thenReturn(Optional.of(testCard));

        assertThrows(InvalidOperationException.class, () -> 
            cardService.activateCard(validCardId)
        );
    }

    @Test
    void activateCard_NotFound() {
        when(cardRepository.findById(validCardId)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> 
            cardService.activateCard(validCardId)
        );
    }

    @Test
    void blockCard_Success() {
        when(cardRepository.findById(validCardId)).thenReturn(Optional.of(testCard));
        when(cardRepository.save(any(Card.class))).thenReturn(testCard);

        cardService.blockCard(validCardId);

        assertTrue(testCard.getIsBlocked());
        verify(cardRepository).save(testCard);
    }

    @Test
    void blockCard_NotFound() {
        when(cardRepository.findById(validCardId)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> 
            cardService.blockCard(validCardId)
        );
    }

    @Test
    void rechargeBalance_Success() {
        when(cardRepository.findById(validCardId)).thenReturn(Optional.of(testCard));
        when(cardRepository.save(any(Card.class))).thenReturn(testCard);

        cardService.rechargeBalance(balanceDTO);

        assertEquals(1500.0, testCard.getBalance());
        verify(cardRepository).findById(validCardId);
        verify(cardRepository).save(testCard);
    }

    @Test
    void rechargeBalance_CardBlocked() {
        testCard.setIsBlocked(true);
        CardBalanceDTO balanceDTO = new CardBalanceDTO();
        balanceDTO.setCardId(validCardId);
        balanceDTO.setBalance(500.0);

        when(cardRepository.findById(validCardId)).thenReturn(Optional.of(testCard));

        assertThrows(InvalidOperationException.class, () -> 
            cardService.rechargeBalance(balanceDTO)
        );
    }

    @Test
    void checkBalance_Success() {
        when(cardRepository.findById(validCardId)).thenReturn(Optional.of(testCard));

        Double balance = cardService.checkBalance(validCardId);

        assertEquals(1000.0, balance);
        verify(cardRepository).findById(validCardId);
    }

    @Test
    void isValidForTransaction_AllConditionsMet() {
        assertTrue(cardService.isValidForTransaction(testCard, 500.0));
    }

    @Test
    void isValidForTransaction_InsufficientBalance() {
        assertFalse(cardService.isValidForTransaction(testCard, 2000.0));
    }

    @Test
    void isValidForTransaction_CardInactive() {
        testCard.setIsActive(false);
        assertFalse(cardService.isValidForTransaction(testCard, 500.0));
    }

    @Test
    void isValidForTransaction_CardBlocked() {
        testCard.setIsBlocked(true);
        assertFalse(cardService.isValidForTransaction(testCard, 500.0));
    }

    @Test
    void isValidForTransaction_CardExpired() {
        testCard.setExpirationDate(LocalDateTime.now().minusDays(1));
        assertFalse(cardService.isValidForTransaction(testCard, 500.0));
    }

    @Test
void generateCardNumber_ValidatesLength() {
    String productId = "123456";
    String cardNumber = cardService.generateCardNumber(productId);
    
    assertEquals(16, cardNumber.length());
    assertTrue(cardNumber.startsWith(productId));
    verify(cardRepository).save(any(Card.class));
}

@Test
void generateCardNumber_SetsInitialValues() {
    String productId = "123456";
    when(cardRepository.save(any(Card.class))).thenAnswer(i -> i.getArguments()[0]);
    
    cardService.generateCardNumber(productId);
    
    verify(cardRepository).save(argThat(card -> {
        Card c = (Card) card;
        return !c.getIsActive() &&
               !c.getIsBlocked() &&
               c.getBalance() == 0.0 &&
               c.getExpirationDate().isAfter(LocalDateTime.now().plusYears(2));
    }));
}


@Test
    void rechargeBalance_ValidatesPositiveAmount() {
        balanceDTO.setBalance(0.0);
        
        assertThrows(InvalidOperationException.class, () ->
            cardService.rechargeBalance(balanceDTO)
        );

        balanceDTO.setBalance(-100.0);
        
        assertThrows(InvalidOperationException.class, () ->
            cardService.rechargeBalance(balanceDTO)
        );

        verify(cardRepository, never()).findById(anyString());
        verify(cardRepository, never()).save(any());
    }

    @Test
    void rechargeBalance_ValidatesCardActive() {
        testCard.setIsActive(false);
        when(cardRepository.findById(validCardId)).thenReturn(Optional.of(testCard));

        assertThrows(InvalidOperationException.class, () ->
            cardService.rechargeBalance(balanceDTO)
        );

        verify(cardRepository).findById(validCardId);
        verify(cardRepository, never()).save(any());
    }

    @Test
    void rechargeBalance_ValidatesCardNotBlocked() {
        testCard.setIsBlocked(true);
        when(cardRepository.findById(validCardId)).thenReturn(Optional.of(testCard));

        assertThrows(InvalidOperationException.class, () ->
            cardService.rechargeBalance(balanceDTO)
        );

        verify(cardRepository).findById(validCardId);
        verify(cardRepository, never()).save(any());
    }

    @Test
    void rechargeBalance_CardNotFound() {
        when(cardRepository.findById(validCardId)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () ->
            cardService.rechargeBalance(balanceDTO)
        );

        verify(cardRepository).findById(validCardId);
        verify(cardRepository, never()).save(any());
    }
}
