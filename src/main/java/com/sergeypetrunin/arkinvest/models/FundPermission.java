package com.sergeypetrunin.arkinvest.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record FundPermission(
        @JsonProperty("fund_id") UUID fundId,
        @JsonProperty("investor_id") UUID investorId
) { }
