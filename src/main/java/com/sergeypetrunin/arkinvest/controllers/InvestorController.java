package com.sergeypetrunin.arkinvest.controllers;

import com.sergeypetrunin.arkinvest.models.Investor;
import com.sergeypetrunin.arkinvest.repositories.InvestorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import jakarta.validation.constraints.Email;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;
import java.util.UUID;

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

    public record CreateInvestorRequest(
            String name,
            @Email
            String email
    ) {}

    @PostMapping
    public ResponseEntity<Investor> createInvestor(@Valid @RequestBody CreateInvestorRequest request) {
        UUID id = investorRepository.create(request.name(), request.email());
        Investor investor = new Investor(id, request.name(), request.email());

        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(location).body(investor);
    }
}
