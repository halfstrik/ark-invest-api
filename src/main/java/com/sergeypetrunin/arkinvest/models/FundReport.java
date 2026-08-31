package com.sergeypetrunin.arkinvest.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record FundReport(
        @JsonProperty("total_balance") BigDecimal totalBalance,
        @JsonProperty("from_date") Instant fromDate,
        @JsonProperty("to_date") Instant toDate,
        List<FundTransaction> transactions
) { }
