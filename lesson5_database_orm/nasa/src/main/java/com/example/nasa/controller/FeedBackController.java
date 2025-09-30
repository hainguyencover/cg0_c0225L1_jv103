package com.example.nasa.controller;

import com.example.nasa.model.FeedBack;
import com.example.nasa.service.IFeedBackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class FeedBackController {

    @Autowired
    private IFeedBackService feedBackService;

    @GetMapping("/")
    public String showForm(Model model) {
        model.addAttribute("feedback", new FeedBack());
        model.addAttribute("list", feedBackService.findToday());
        return "feedback";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute FeedBack feedback) {
        feedBackService.save(feedback);
        return "redirect:/";
    }

    @GetMapping("/like/{id}")
    public String like(@PathVariable Long id) {
        feedBackService.like(id);
        return "redirect:/";
    }
}
