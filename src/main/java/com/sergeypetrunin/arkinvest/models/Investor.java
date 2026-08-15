package com.sergeypetrunin.arkinvest.models;

import java.util.UUID;

public record Investor(
        UUID id,
        String name,
        String email
) { }
