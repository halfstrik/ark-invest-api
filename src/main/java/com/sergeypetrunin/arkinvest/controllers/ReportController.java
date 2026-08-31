package com.sergeypetrunin.arkinvest.controllers;

import com.sergeypetrunin.arkinvest.business.ReportCalculator;
import com.sergeypetrunin.arkinvest.models.FundReport;
import com.sergeypetrunin.arkinvest.models.FundTransaction;
import com.sergeypetrunin.arkinvest.repositories.FundTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private FundTransactionRepository fundTransactionRepository;

    @GetMapping("/fund/{id}")
    public ResponseEntity<FundReport> getFundReport(
            @PathVariable UUID id,
            @RequestParam(name = "from_date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fromDate,
            @RequestParam(name = "to_date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant toDate
    ) {
        List<FundTransaction> transactions = fundTransactionRepository.findByFundId(id, fromDate, toDate);

        ReportCalculator calculator = new ReportCalculator();
        BigDecimal totalBalance = calculator.calculateTotalBalance(transactions);

        return new ResponseEntity<>(new FundReport(totalBalance, fromDate, toDate, transactions), HttpStatus.OK);
    }
}
