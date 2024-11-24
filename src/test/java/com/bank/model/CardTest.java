package com.bank.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class CardTest {
    @Test
void card_EqualsAndHashCode() {
    Card card1 = new Card();
    card1.setCardId("123");
    Card card2 = new Card();
    card2.setCardId("123");
    Card card3 = new Card();
    card3.setCardId("456");
    
    assertEquals(card1, card2);
    assertNotEquals(card1, card3);
    assertEquals(card1.hashCode(), card2.hashCode());
}
    
}
