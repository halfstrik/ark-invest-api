package com.sergeypetrunin.arkinvest.controllers;

import com.sergeypetrunin.arkinvest.models.FundTransaction;
import com.sergeypetrunin.arkinvest.repositories.FundTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class FundTransactionController {

    @Autowired
    FundTransactionRepository fundTransactionRepository;

    @GetMapping
    public ResponseEntity<List<FundTransaction>> getTransactions() {
        List<FundTransaction> transactions = fundTransactionRepository.findAll();
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }
}
