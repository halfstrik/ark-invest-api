package com.sergeypetrunin.arkinvest.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sergeypetrunin.arkinvest.models.FundTransaction;
import com.sergeypetrunin.arkinvest.models.TransactionEffect;
import com.sergeypetrunin.arkinvest.models.TransactionType;
import com.sergeypetrunin.arkinvest.repositories.FundTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
public class FundTransactionController {

    @Autowired
    FundTransactionRepository fundTransactionRepository;

    @GetMapping
    public ResponseEntity<List<FundTransaction>> getTransactions() {
        List<FundTransaction> transactions = fundTransactionRepository.findAll();
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public void handleIllegalArgument(IllegalArgumentException e) {
    }

    public record CreateFundTransactionRequest(
            @JsonProperty("fund_id") UUID fundId,
            @JsonProperty("investor_id") UUID investorId,
            @JsonProperty("transaction_type") TransactionType transactionType,
            @JsonProperty("transaction_effect") TransactionEffect transactionEffect,
            BigDecimal amount,
            String description
    ) {}

    @PostMapping
    public ResponseEntity<FundTransaction> createTransaction(@RequestBody CreateFundTransactionRequest request) {
        if (request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amount must be positive");
        }

        UUID id = fundTransactionRepository.create(
                request.fundId(),
                request.investorId(),
                request.transactionType(),
                request.transactionEffect(),
                request.amount(),
                request.description()
        );

        FundTransaction transaction = new FundTransaction(
                id,
                request.fundId(),
                request.investorId(),
                request.transactionType(),
                request.transactionEffect(),
                request.amount(),
                OffsetDateTime.now(),
                request.description()
        );

        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(location).body(transaction);
    }
}
