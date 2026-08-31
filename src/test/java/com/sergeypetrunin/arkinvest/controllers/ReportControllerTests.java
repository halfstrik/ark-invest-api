package com.sergeypetrunin.arkinvest.controllers;

import com.sergeypetrunin.arkinvest.models.FundTransaction;
import com.sergeypetrunin.arkinvest.models.TransactionEffect;
import com.sergeypetrunin.arkinvest.models.TransactionType;
import com.sergeypetrunin.arkinvest.repositories.FundTransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
public class ReportControllerTests {

    @MockitoBean
    FundTransactionRepository fundTransactionRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCalculateTotalBalanceForFund() throws Exception {
        UUID fundId = UUID.randomUUID();
        UUID investorId = UUID.randomUUID();

        FundTransaction contribution = new FundTransaction(
                UUID.randomUUID(),
                fundId,
                investorId,
                TransactionType.CONTRIBUTION,
                TransactionEffect.CREDIT,
                new BigDecimal("100.00"),
                Instant.parse("2026-08-15T12:00:00Z"),
                "Initial contribution"
        );

        FundTransaction interest = new FundTransaction(
                UUID.randomUUID(),
                fundId,
                investorId,
                TransactionType.INTEREST_INCOME,
                TransactionEffect.CREDIT,
                new BigDecimal("50.00"),
                Instant.parse("2026-08-15T12:00:00Z"),
                "Interest income"
        );

        FundTransaction distribution = new FundTransaction(
                UUID.randomUUID(),
                fundId,
                investorId,
                TransactionType.DISTRIBUTION,
                TransactionEffect.DEBIT,
                new BigDecimal("25.00"),
                Instant.parse("2026-08-15T12:00:00Z"),
                "Distribution"
        );

        when(fundTransactionRepository.findByFundId(eq(fundId), nullable(Instant.class), nullable(Instant.class), nullable(TransactionEffect.class)))
                .thenReturn(List.of(contribution, interest, distribution));

        mockMvc.perform(get("/reports/fund/" + fundId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total_balance").value(125.00))
                .andExpect(jsonPath("$.from_date").isEmpty())
                .andExpect(jsonPath("$.to_date").isEmpty())
                .andExpect(jsonPath("$.transactions.length()").value(3))
                .andExpect(jsonPath("$.transactions[0].amount").value(100.00))
                .andExpect(jsonPath("$.transactions[1].amount").value(50.00))
                .andExpect(jsonPath("$.transactions[2].amount").value(25.00));
    }

    @Test
    void shouldFilterReportByDateRange() throws Exception {
        UUID fundId = UUID.randomUUID();
        UUID investorId = UUID.randomUUID();

        FundTransaction earlyContribution = new FundTransaction(
                UUID.randomUUID(),
                fundId,
                investorId,
                TransactionType.CONTRIBUTION,
                TransactionEffect.CREDIT,
                new BigDecimal("100.00"),
                Instant.parse("2026-08-14T12:00:00Z"),
                "Early contribution"
        );

        FundTransaction inRangeDistribution = new FundTransaction(
                UUID.randomUUID(),
                fundId,
                investorId,
                TransactionType.DISTRIBUTION,
                TransactionEffect.DEBIT,
                new BigDecimal("25.00"),
                Instant.parse("2026-08-15T12:00:00Z"),
                "Distribution"
        );

        FundTransaction lateInterest = new FundTransaction(
                UUID.randomUUID(),
                fundId,
                investorId,
                TransactionType.INTEREST_INCOME,
                TransactionEffect.CREDIT,
                new BigDecimal("50.00"),
                Instant.parse("2026-08-16T12:00:00Z"),
                "Late interest"
        );

        when(fundTransactionRepository.findByFundId(eq(fundId), nullable(Instant.class), nullable(Instant.class), nullable(TransactionEffect.class)))
                .thenReturn(List.of(inRangeDistribution));

        mockMvc.perform(get("/reports/fund/" + fundId)
                        .param("from_date", "2026-08-15T00:00:00Z")
                        .param("to_date", "2026-08-15T23:59:59Z"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total_balance").value(-25.00))
                .andExpect(jsonPath("$.from_date").value("2026-08-15T00:00:00Z"))
                .andExpect(jsonPath("$.to_date").value("2026-08-15T23:59:59Z"))
                .andExpect(jsonPath("$.transactions.length()").value(1))
                .andExpect(jsonPath("$.transactions[0].amount").value(25.00));
    }

    @Test
    void shouldFilterReportByTransactionEffect() throws Exception {
        UUID fundId = UUID.randomUUID();
        UUID investorId = UUID.randomUUID();

        FundTransaction contribution = new FundTransaction(
                UUID.randomUUID(),
                fundId,
                investorId,
                TransactionType.CONTRIBUTION,
                TransactionEffect.CREDIT,
                new BigDecimal("100.00"),
                Instant.parse("2026-08-15T12:00:00Z"),
                "Contribution"
        );

        FundTransaction distribution = new FundTransaction(
                UUID.randomUUID(),
                fundId,
                investorId,
                TransactionType.DISTRIBUTION,
                TransactionEffect.DEBIT,
                new BigDecimal("25.00"),
                Instant.parse("2026-08-15T12:00:00Z"),
                "Distribution"
        );

        when(fundTransactionRepository.findByFundId(eq(fundId), nullable(Instant.class), nullable(Instant.class), eq(TransactionEffect.CREDIT)))
                .thenReturn(List.of(contribution));

        mockMvc.perform(get("/reports/fund/" + fundId)
                        .param("transaction_effect", "CREDIT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total_balance").value(100.00))
                .andExpect(jsonPath("$.transactions.length()").value(1))
                .andExpect(jsonPath("$.transactions[0].amount").value(100.00));
    }
}
