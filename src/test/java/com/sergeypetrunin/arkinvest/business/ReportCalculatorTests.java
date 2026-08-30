package com.sergeypetrunin.arkinvest.business;

import com.sergeypetrunin.arkinvest.models.FundTransaction;
import com.sergeypetrunin.arkinvest.models.TransactionEffect;
import com.sergeypetrunin.arkinvest.models.TransactionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ReportCalculatorTests {

    @Test
    void shouldCalculateTotalBalanceFromCreditsAndDebits() {
        UUID fundId = UUID.randomUUID();
        UUID investorId = UUID.randomUUID();

        List<FundTransaction> transactions = List.of(
                new FundTransaction(
                        UUID.randomUUID(),
                        fundId,
                        investorId,
                        TransactionType.CONTRIBUTION,
                        TransactionEffect.CREDIT,
                        new BigDecimal("100.00"),
                        Instant.parse("2026-08-15T12:00:00Z"),
                        "Contribution"
                ),
                new FundTransaction(
                        UUID.randomUUID(),
                        fundId,
                        investorId,
                        TransactionType.DISTRIBUTION,
                        TransactionEffect.DEBIT,
                        new BigDecimal("30.00"),
                        Instant.parse("2026-08-15T12:00:00Z"),
                        "Distribution"
                ),
                new FundTransaction(
                        UUID.randomUUID(),
                        fundId,
                        investorId,
                        TransactionType.INTEREST_INCOME,
                        TransactionEffect.CREDIT,
                        new BigDecimal("10.00"),
                        Instant.parse("2026-08-15T12:00:00Z"),
                        "Interest"
                )
        );

        ReportCalculator calculator = new ReportCalculator();

        assertThat(calculator.calculateTotalBalance(transactions))
                .isEqualByComparingTo(new BigDecimal("80.00"));
    }

    @Test
    void shouldReturnZeroForEmptyTransactions() {
        ReportCalculator calculator = new ReportCalculator();

        assertThat(calculator.calculateTotalBalance(List.of()))
                .isEqualByComparingTo(BigDecimal.ZERO);
    }
}
