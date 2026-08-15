package com.sergeypetrunin.arkinvest.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sergeypetrunin.arkinvest.business.ReportCalculator;
import com.sergeypetrunin.arkinvest.models.FundTransaction;
import com.sergeypetrunin.arkinvest.repositories.FundTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private FundTransactionRepository fundTransactionRepository;

    public record FundReport(
            @JsonProperty("total_balance") BigDecimal totalBalance,
            List<FundTransaction> transactions
    ) {}

    @GetMapping("/fund/{id}")
    public ResponseEntity<FundReport> getFundReport(@PathVariable UUID id) {
        List<FundTransaction> transactions = fundTransactionRepository.findByFundId(id);

        ReportCalculator calculator = new ReportCalculator();
        BigDecimal totalBalance = calculator.calculateTotalBalance(transactions);

        return new ResponseEntity<>(new FundReport(totalBalance, transactions), HttpStatus.OK);
    }
}
