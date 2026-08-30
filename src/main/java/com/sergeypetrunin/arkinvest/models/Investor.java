package com.sergeypetrunin.arkinvest.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record Investor(
        UUID id,
        String name,
        String email,
        @JsonProperty("is_deleted") boolean isDeleted
) { }
