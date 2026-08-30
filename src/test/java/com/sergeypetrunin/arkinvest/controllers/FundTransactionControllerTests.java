package com.sergeypetrunin.arkinvest.controllers;

import com.sergeypetrunin.arkinvest.models.FundTransaction;
import com.sergeypetrunin.arkinvest.models.TransactionEffect;
import com.sergeypetrunin.arkinvest.models.TransactionType;
import com.sergeypetrunin.arkinvest.repositories.FundTransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FundTransactionController.class)
public class FundTransactionControllerTests {

    @MockitoBean
    FundTransactionRepository fundTransactionRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldGetTransactions() throws Exception {
        UUID transactionId = UUID.randomUUID();
        UUID fundId = UUID.randomUUID();
        UUID investorId = UUID.randomUUID();
        FundTransaction transaction = new FundTransaction(
                transactionId,
                fundId,
                investorId,
                TransactionType.CONTRIBUTION,
                TransactionEffect.CREDIT,
                new BigDecimal("100.50"),
                Instant.parse("2026-08-15T12:00:00Z"),
                "Initial contribution"
        );
        when(fundTransactionRepository.findAll()).thenReturn(List.of(transaction));

        mockMvc.perform(get("/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(transactionId.toString()))
                .andExpect(jsonPath("$[0].fund_id").value(fundId.toString()))
                .andExpect(jsonPath("$[0].investor_id").value(investorId.toString()))
                .andExpect(jsonPath("$[0].transaction_type").value("CONTRIBUTION"))
                .andExpect(jsonPath("$[0].transaction_effect").value("CREDIT"))
                .andExpect(jsonPath("$[0].amount").value(100.50))
                .andExpect(jsonPath("$[0].transaction_date").value("2026-08-15T12:00:00Z"))
                .andExpect(jsonPath("$[0].description").value("Initial contribution"));
    }

    @Test
    void shouldCreateTransactionWithValidRequest() throws Exception {
        UUID transactionId = UUID.randomUUID();
        UUID fundId = UUID.randomUUID();
        UUID investorId = UUID.randomUUID();

        when(fundTransactionRepository.create(
                eq(fundId),
                eq(investorId),
                eq(TransactionType.CONTRIBUTION),
                eq(TransactionEffect.CREDIT),
                any(BigDecimal.class),
                eq("Initial contribution")
        )).thenReturn(transactionId);

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "fund_id": "%s",
                                    "investor_id": "%s",
                                    "transaction_type": "CONTRIBUTION",
                                    "transaction_effect": "CREDIT",
                                    "amount": 100.50,
                                    "description": "Initial contribution"
                                }
                                """.formatted(fundId, investorId)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/transactions/" + transactionId))
                .andExpect(jsonPath("$.id").value(transactionId.toString()))
                .andExpect(jsonPath("$.fund_id").value(fundId.toString()))
                .andExpect(jsonPath("$.investor_id").value(investorId.toString()))
                .andExpect(jsonPath("$.transaction_type").value("CONTRIBUTION"))
                .andExpect(jsonPath("$.transaction_effect").value("CREDIT"))
                .andExpect(jsonPath("$.amount").value(100.50))
                .andExpect(jsonPath("$.transaction_date").exists())
                .andExpect(jsonPath("$.description").value("Initial contribution"));
    }

    @Test
    void shouldReturn400WhenCreatingTransactionForNonExistentFund() throws Exception {
        UUID fundId = UUID.randomUUID();
        UUID investorId = UUID.randomUUID();

        doThrow(new IllegalArgumentException("Fund with id '" + fundId + "' does not exist"))
                .when(fundTransactionRepository)
                .create(
                        eq(fundId),
                        eq(investorId),
                        eq(TransactionType.CONTRIBUTION),
                        eq(TransactionEffect.CREDIT),
                        any(BigDecimal.class),
                        eq("Initial contribution")
                );

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "fund_id": "%s",
                                    "investor_id": "%s",
                                    "transaction_type": "CONTRIBUTION",
                                    "transaction_effect": "CREDIT",
                                    "amount": 100.50,
                                    "description": "Initial contribution"
                                }
                                """.formatted(fundId, investorId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenTransactionAmountIsNegative() throws Exception {
        UUID fundId = UUID.randomUUID();
        UUID investorId = UUID.randomUUID();

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "fund_id": "%s",
                                    "investor_id": "%s",
                                    "transaction_type": "CONTRIBUTION",
                                    "transaction_effect": "CREDIT",
                                    "amount": -50.00,
                                    "description": "Invalid transaction"
                                }
                                """.formatted(fundId, investorId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenTransactionTypeIsInvalid() throws Exception {
        UUID fundId = UUID.randomUUID();
        UUID investorId = UUID.randomUUID();

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "fund_id": "%s",
                                    "investor_id": "%s",
                                    "transaction_type": "UNKNOWN_TYPE",
                                    "transaction_effect": "CREDIT",
                                    "amount": 50.00,
                                    "description": "Invalid type"
                                }
                                """.formatted(fundId, investorId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenTransactionEffectIsInvalid() throws Exception {
        UUID fundId = UUID.randomUUID();
        UUID investorId = UUID.randomUUID();

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "fund_id": "%s",
                                    "investor_id": "%s",
                                    "transaction_type": "CONTRIBUTION",
                                    "transaction_effect": "UNKNOWN_EFFECT",
                                    "amount": 50.00,
                                    "description": "Invalid effect"
                                }
                                """.formatted(fundId, investorId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenCreatingTransactionForDeletedFund() throws Exception {
        UUID fundId = UUID.randomUUID();
        UUID investorId = UUID.randomUUID();

        doThrow(new IllegalArgumentException("Fund with id '" + fundId + "' is deleted"))
                .when(fundTransactionRepository)
                .create(
                        eq(fundId),
                        eq(investorId),
                        eq(TransactionType.CONTRIBUTION),
                        eq(TransactionEffect.CREDIT),
                        any(BigDecimal.class),
                        eq("Initial contribution")
                );

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "fund_id": "%s",
                                    "investor_id": "%s",
                                    "transaction_type": "CONTRIBUTION",
                                    "transaction_effect": "CREDIT",
                                    "amount": 100.50,
                                    "description": "Initial contribution"
                                }
                                """.formatted(fundId, investorId)))
                .andExpect(status().isBadRequest());
    }
}
