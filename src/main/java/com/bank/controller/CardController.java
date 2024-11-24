package com.bank.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.dto.CardBalanceDTO;
import com.bank.dto.CardEnrollmentDTO;
import com.bank.service.CardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/card")
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;
    @GetMapping("/{productId}/number")
    public ResponseEntity<String> generateCardNumber(@PathVariable String productId) {
        return ResponseEntity.ok(cardService.generateCardNumber(productId));
    }

    @PostMapping("/enroll")
    public ResponseEntity<Void> activateCard(@RequestBody CardEnrollmentDTO request) {
        cardService.activateCard(request.getCardId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> blockCard(@PathVariable String cardId) {
        cardService.blockCard(cardId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/balance")
    public ResponseEntity<Void> rechargeBalance(@RequestBody CardBalanceDTO request) {
        cardService.rechargeBalance(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/balance/{cardId}")
    public ResponseEntity<Double> checkBalance(@PathVariable String cardId) {
        return ResponseEntity.ok(cardService.checkBalance(cardId));
    }
}