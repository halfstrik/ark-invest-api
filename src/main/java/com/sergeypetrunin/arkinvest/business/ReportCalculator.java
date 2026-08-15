package com.sergeypetrunin.arkinvest.business;

import com.sergeypetrunin.arkinvest.models.FundTransaction;
import com.sergeypetrunin.arkinvest.models.TransactionEffect;

import java.math.BigDecimal;
import java.util.List;

public class ReportCalculator {

    public BigDecimal calculateTotalBalance(List<FundTransaction> transactions) {
        return transactions.stream()
                .map(t -> t.transactionEffect() == TransactionEffect.CREDIT ? t.amount() : t.amount().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
