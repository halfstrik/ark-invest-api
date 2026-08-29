package com.sergeypetrunin.arkinvest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record Fund(
        UUID id,
        String name,
        String description,
        @JsonProperty("is_deleted") boolean isDeleted
) {
    public Fund(UUID id, String name, String description) {
        this(id, name, description, false);
    }
}
