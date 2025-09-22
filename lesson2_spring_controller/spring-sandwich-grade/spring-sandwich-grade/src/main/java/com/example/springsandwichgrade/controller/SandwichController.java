package com.example.springsandwichgrade.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class SandwichController {
    @RequestMapping("/")
    public String home() {
        return "index"; // mở file index.jsp
    }

    @RequestMapping("/save")
    public String save(@RequestParam(value = "condiment", required = false) String[] condiment, Model model) {
        model.addAttribute("condiments", condiment);
        return "result"; // mở file result.jsp
    }
}
