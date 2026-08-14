package com.sergeypetrunin.arkinvest.controllers;

import com.sergeypetrunin.arkinvest.models.Fund;
import com.sergeypetrunin.arkinvest.repositories.FundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/funds")
public class FundController {

    @Autowired
    FundRepository fundRepository;

    @GetMapping
    public ResponseEntity<List<Fund>> getFunds() {
        List<Fund> funds = fundRepository.findAll();
        return new ResponseEntity<>(funds, HttpStatus.OK);
    }

}
