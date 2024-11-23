// src/main/java/com/bank/repository/CardRepository.java
package com.bank.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bank.model.Card;

@Repository
public interface CardRepository extends JpaRepository<Card, String> {
    
    // Buscar tarjetas activas
    List<Card> findByIsActiveTrue();
    
    // Buscar tarjetas por productId
    List<Card> findByProductId(String productId);
    
    // Buscar tarjetas no bloqueadas
    List<Card> findByIsBlockedFalse();
    
    // Buscar por cardId y estado activo
    Optional<Card> findByCardIdAndIsActiveTrue(String cardId);
    
    // Buscar tarjetas con saldo mayor a un valor
    @Query("SELECT c FROM Card c WHERE c.balance >= :minBalance")
    List<Card> findCardsWithMinBalance(@Param("minBalance") Double minBalance);
    
    // Verificar si existe una tarjeta activa
    boolean existsByCardIdAndIsActiveTrue(String cardId);
}
