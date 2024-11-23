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

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/purchase")
    public ResponseEntity<TransactionResponseDTO> processPurchase(@RequestBody TransactionRequestDTO request) {
        return ResponseEntity.ok(transactionService.processPurchase(request));
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponseDTO> getTransaction(@PathVariable String transactionId) {
        return ResponseEntity.ok(transactionService.getTransaction(transactionId));
    }

    @PostMapping("/anulation")
    public ResponseEntity<Void> cancelTransaction(@RequestBody TransactionCancellationDTO request) {
        transactionService.cancelTransaction(request);
        return ResponseEntity.ok().build();
    }
}
