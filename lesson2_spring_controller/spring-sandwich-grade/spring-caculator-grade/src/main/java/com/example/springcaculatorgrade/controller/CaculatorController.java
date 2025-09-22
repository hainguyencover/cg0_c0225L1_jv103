package com.example.springcaculatorgrade.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CaculatorController {
    @RequestMapping("/")
    public String home() {
        return "index";
    }

    @RequestMapping(value = "/calculate", method = RequestMethod.POST)
    public String calculate(@RequestParam("num1") double num1,
                            @RequestParam("num2") double num2,
                            @RequestParam("operator") String operator,
                            Model model) {

        double result = 0;
        String message = "";

        switch (operator) {
            case "+":
                result = num1 + num2;
                message = "Addition";
                break;
            case "-":
                result = num1 - num2;
                message = "Subtraction";
                break;
            case "*":
                result = num1 * num2;
                message = "Multiplication";
                break;
            case "/":
                if (num2 == 0) {
                    message = "Division by zero is not allowed!";
                } else {
                    result = num1 / num2;
                    message = "Division";
                }
                break;
        }

        model.addAttribute("num1", num1);
        model.addAttribute("num2", num2);
        model.addAttribute("operator", operator);
        model.addAttribute("message", message);
        model.addAttribute("result", result);

        return "result";
    }
}
