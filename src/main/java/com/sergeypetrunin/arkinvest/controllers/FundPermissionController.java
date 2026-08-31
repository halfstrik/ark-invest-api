package com.sergeypetrunin.arkinvest.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sergeypetrunin.arkinvest.models.FundPermission;
import com.sergeypetrunin.arkinvest.repositories.FundPermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/fund-permissions")
public class FundPermissionController {

    @Autowired
    private FundPermissionRepository fundPermissionRepository;

    public record CreateFundPermissionRequest(
            @JsonProperty("fund_id") UUID fundId,
            @JsonProperty("investor_id") UUID investorId
    ) {}

    @PostMapping
    public ResponseEntity<FundPermission> createPermission(@RequestBody CreateFundPermissionRequest request) {
        boolean created = fundPermissionRepository.create(request.fundId(), request.investorId());
        FundPermission permission = new FundPermission(request.fundId(), request.investorId());

        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .buildAndExpand()
                .toUri();

        return ResponseEntity.status(created ? HttpStatus.CREATED : HttpStatus.OK)
                .location(location)
                .body(permission);
    }
}
