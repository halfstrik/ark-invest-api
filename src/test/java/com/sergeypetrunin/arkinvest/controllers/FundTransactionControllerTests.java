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
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
                "2026-08-14",
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
                .andExpect(jsonPath("$[0].transaction_date").value("2026-08-14"))
                .andExpect(jsonPath("$[0].description").value("Initial contribution"));
    }
}
