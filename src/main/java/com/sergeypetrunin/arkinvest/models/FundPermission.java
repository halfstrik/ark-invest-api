package com.sergeypetrunin.arkinvest.models;

import java.util.UUID;

public record FundPermission(
        UUID fundId,
        UUID investorId
) { }
