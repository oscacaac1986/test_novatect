package com.bank.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bank.enums.TransactionStatus;
import com.bank.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    
    // Buscar transacciones por cardId
    List<Transaction> findByCardId(String cardId);
    
    // Buscar transacciones por estado
    List<Transaction> findByStatus(TransactionStatus status);
    
    // Buscar transacciones dentro de un rango de fechas
    List<Transaction> findByTransactionDateBetween(
        LocalDateTime startDate, 
        LocalDateTime endDate
    );
    
    // Buscar transacciones por cardId y estado
    List<Transaction> findByCardIdAndStatus(
        String cardId, 
        TransactionStatus status
    );
    
    // Buscar transacciones para anulación (menos de 24 horas)
    @Query("SELECT t FROM Transaction t WHERE t.cardId = :cardId " +
           "AND t.transactionDate >= :date " +
           "AND t.status = :status")
    List<Transaction> findTransactionsForCancellation(
        @Param("cardId") String cardId,
        @Param("date") LocalDateTime date,
        @Param("status") TransactionStatus status
    );
    
    // Contar transacciones por cardId y estado
    long countByCardIdAndStatus(String cardId, TransactionStatus status);
    
    // Buscar última transacción de una tarjeta
    Optional<Transaction> findFirstByCardIdOrderByTransactionDateDesc(String cardId);
    
    // Buscar transacciones por monto mayor a
    @Query("SELECT t FROM Transaction t WHERE t.price >= :amount")
    List<Transaction> findTransactionsWithMinAmount(@Param("amount") Double amount);
    
    // Validar si existe una transacción cancelable
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Transaction t " +
           "WHERE t.id = :transactionId " +
           "AND t.transactionDate >= :date " +
           "AND t.status = :status")
    boolean existsCancelableTransaction(
        @Param("transactionId") String transactionId,
        @Param("date") LocalDateTime date,
        @Param("status") TransactionStatus status
    );
}
