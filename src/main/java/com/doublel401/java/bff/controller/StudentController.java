package com.doublel401.java.bff.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StudentController {

    @GetMapping("/api/home")
    public String home() {
        return "hello";
    }
}
