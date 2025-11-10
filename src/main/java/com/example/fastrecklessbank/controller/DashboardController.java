package com.example.fastrecklessbank.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/dashboard")
public class DashboardController {

    @GetMapping("")
    public ResponseEntity<String> index() {
        return ResponseEntity.ok("Welcome to the Fast and Reckless Bank!");
    }
}
