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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/card")
@RequiredArgsConstructor
@Tag(name = "Card Management", description = "Endpoints for managing bank cards")
public class CardController {
    private final CardService cardService;
    @Operation(summary = "Generate new card number", 
               description = "Generates a new card number based on product ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Card number generated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid product ID supplied")
    })
    @GetMapping("/{productId}/number")
    public ResponseEntity<String> generateCardNumber(@PathVariable String productId) {
        return ResponseEntity.ok(cardService.generateCardNumber(productId));
    }

    @Operation(summary = "Activate card", description = "Activates an existing card")
    @PostMapping("/enroll")
    public ResponseEntity<Void> activateCard(@RequestBody CardEnrollmentDTO request) {
        cardService.activateCard(request.getCardId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Block card", description = "Blocks an existing card")
    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> blockCard(@PathVariable String cardId) {
        cardService.blockCard(cardId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Recharge card balance")
    @PostMapping("/balance")
    public ResponseEntity<Void> rechargeBalance(@RequestBody CardBalanceDTO request) {
        cardService.rechargeBalance(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Check card balance")
    @GetMapping("/balance/{cardId}")
    public ResponseEntity<Double> checkBalance(@PathVariable String cardId) {
        return ResponseEntity.ok(cardService.checkBalance(cardId));
    }
}