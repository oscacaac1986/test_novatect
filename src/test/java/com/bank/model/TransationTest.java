package com.bank.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class TransationTest {
    @Test
void transaction_EqualsAndHashCode() {
    Transaction t1 = new Transaction();
    t1.setId("123");
    Transaction t2 = new Transaction();
    t2.setId("123");
    Transaction t3 = new Transaction();
    t3.setId("456");
    
    assertEquals(t1, t2);
    assertNotEquals(t1, t3);
    assertEquals(t1.hashCode(), t2.hashCode());
}
    
}
