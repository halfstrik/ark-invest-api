package com.sergeypetrunin.arkinvest.controllers;

import com.sergeypetrunin.arkinvest.models.Fund;
import com.sergeypetrunin.arkinvest.repositories.FundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


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

    public record CreateFundRequest(
            String name,
            String description
    ) {}

    @PostMapping
    public ResponseEntity<Fund> createFund(@RequestBody CreateFundRequest request) {
        UUID id = fundRepository.create(request.name(), request.description());
        Fund fund = new Fund(id, request.name(), request.description());

        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(location).body(fund);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Fund> getFundById(@PathVariable UUID id) {
        Optional<Fund> fund = fundRepository.findById(id);
        return fund.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
