package com.sergeypetrunin.arkinvest.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

public record FundTransaction(
        UUID id,
        @JsonProperty("fund_id") UUID fundId,
        @JsonProperty("investor_id") UUID investorId,
        @JsonProperty("transaction_type") TransactionType transactionType,
        @JsonProperty("transaction_effect") TransactionEffect transactionEffect,
        BigDecimal amount,
        @JsonProperty("transaction_date") String transactionDate,
        String description
) { }
