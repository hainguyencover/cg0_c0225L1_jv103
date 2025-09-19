package com.example.springtranslation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
public class TranslationController {
    private static final Map<String, String> dictionary = new HashMap<>();

    static {
        dictionary.put("hello", "xin chào");
        dictionary.put("book", "quyển sách");
        dictionary.put("computer", "máy tính");
        dictionary.put("dog", "con chó");
        dictionary.put("cat", "con mèo");
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping("/translate")
    public String translate(@RequestParam("word") String word, Model model) {
        String result = dictionary.get(word.toLowerCase());
        if (result != null) {
            model.addAttribute("word", word);
            model.addAttribute("result", result);
        } else {
            model.addAttribute("word", word);
            model.addAttribute("result", "Không tìm thấy nghĩa của từ này!");
        }
        return "result";
    }
}
