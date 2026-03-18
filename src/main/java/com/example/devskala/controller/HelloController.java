package com.example.devskala.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String home() {
        return "Hello, Devskala!";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
