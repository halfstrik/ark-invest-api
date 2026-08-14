package com.sergeypetrunin.arkinvest.models;

import java.util.UUID;

public record Fund(
        UUID id,
        String name,
        String description
) { }
