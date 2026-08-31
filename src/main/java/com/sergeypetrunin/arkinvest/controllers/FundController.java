package com.sergeypetrunin.arkinvest.controllers;

import com.sergeypetrunin.arkinvest.models.Fund;
import com.sergeypetrunin.arkinvest.repositories.FundRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
            @NotBlank
            String name,
            String description
    ) {}

    public record UpdateFundDescriptionRequest(
            String description
    ) {}

    @PostMapping
    public ResponseEntity<Fund> createFund(@Valid @RequestBody CreateFundRequest request) {
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

    @PutMapping("/{id}")
    public ResponseEntity<Fund> updateFundDescription(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateFundDescriptionRequest request) {
        Optional<Fund> fund = fundRepository.updateDescription(id, request.description());
        return fund.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Fund> softDeleteFund(@PathVariable UUID id) {
        Optional<Fund> fund = fundRepository.softDelete(id);
        return fund.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public void handleIllegalArgument(IllegalArgumentException e) {
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public void handleDataIntegrityViolation(DataIntegrityViolationException e) {
    }

}
