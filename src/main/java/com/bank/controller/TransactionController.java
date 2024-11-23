package com.bank.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.dto.TransactionCancellationDTO;
import com.bank.dto.TransactionRequestDTO;
import com.bank.dto.TransactionResponseDTO;
import com.bank.service.TransactionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
@Tag(name = "Transaction Management", description = "Endpoints for managing transactions")
public class TransactionController {
    private final TransactionService transactionService;

    @Operation(summary = "Process a purchase transaction")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transaction processed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid transaction data"),
        @ApiResponse(responseCode = "404", description = "Card not found")
    })
    @PostMapping("/purchase")
    public ResponseEntity<TransactionResponseDTO> processPurchase(@RequestBody TransactionRequestDTO request) {
        return ResponseEntity.ok(transactionService.processPurchase(request));
    }

    @Operation(summary = "Get transaction details")
    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponseDTO> getTransaction(@PathVariable String transactionId) {
        return ResponseEntity.ok(transactionService.getTransaction(transactionId));
    }

    @Operation(summary = "Cancel a transaction", 
               description = "Cancels a transaction within 24 hours of creation")
    @PostMapping("/anulation")
    public ResponseEntity<Void> cancelTransaction(@RequestBody TransactionCancellationDTO request) {
        transactionService.cancelTransaction(request);
        return ResponseEntity.ok().build();
    }
}
