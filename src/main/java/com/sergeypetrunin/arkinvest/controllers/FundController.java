package com.sergeypetrunin.arkinvest.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/funds")
public class FundController {

    @GetMapping
    public String getFunds() {
        return "ok";
    }

}
