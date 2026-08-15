package com.sergeypetrunin.arkinvest.controllers;

import com.sergeypetrunin.arkinvest.models.Investor;
import com.sergeypetrunin.arkinvest.repositories.InvestorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/investors")
public class InvestorController {

    @Autowired
    InvestorRepository investorRepository;

    @GetMapping
    public ResponseEntity<List<Investor>> getInvestors() {
        List<Investor> investors = investorRepository.findAll();
        return new ResponseEntity<>(investors, HttpStatus.OK);
    }
}
