package com.sergeypetrunin.arkinvest.controllers;

import com.sergeypetrunin.arkinvest.models.Investor;
import com.sergeypetrunin.arkinvest.repositories.InvestorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

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

    @GetMapping("/{id}")
    public ResponseEntity<Investor> getInvestorById(@PathVariable UUID id) {
        return investorRepository.findById(id)
                .map(investor -> new ResponseEntity<>(investor, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    public record CreateInvestorRequest(
            @NotBlank
            String name,
            @NotNull @Email
            String email
    ) {}

    @PostMapping
    public ResponseEntity<Investor> createInvestor(@Valid @RequestBody CreateInvestorRequest request) {
        UUID id = investorRepository.create(request.name(), request.email());
        Investor investor = new Investor(id, request.name(), request.email(), false);

        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(location).body(investor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Investor> softDeleteInvestor(@PathVariable UUID id) {
        return investorRepository.softDelete(id)
                .map(investor -> new ResponseEntity<>(investor, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(IllegalArgumentException.class)
    public void handleIllegalArgument(IllegalArgumentException e) {
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public void handleDataIntegrityViolation(DataIntegrityViolationException e) {
    }
}
