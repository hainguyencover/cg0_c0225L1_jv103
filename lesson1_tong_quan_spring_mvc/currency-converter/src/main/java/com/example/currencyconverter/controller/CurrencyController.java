package com.example.currencyconverter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CurrencyController {

    private static final double RATE = 25000; // 1 USD = 25,000 VND

    // Trang chủ hiển thị form
    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping("/convert")
    public String convert(@RequestParam("usd") double usd, Model model) {
        double vnd = usd * RATE; // dùng RATE = 25000
        model.addAttribute("usd", usd);
        model.addAttribute("vnd", vnd);
        return "result";
    }
}
