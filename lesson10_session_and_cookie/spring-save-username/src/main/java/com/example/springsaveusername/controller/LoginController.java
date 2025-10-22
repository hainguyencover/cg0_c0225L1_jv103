package com.example.springsaveusername.controller;

import com.example.springsaveusername.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Controller
@SessionAttributes("user")
public class LoginController {

    @ModelAttribute("user")
    public User setupUser() {
        return new User();
    }

    @GetMapping("/login")
    public String showForm(@CookieValue(value = "rememberEmail", defaultValue = "") String rememberedEmail,
                           Model model) {
        Cookie cookie = new Cookie("rememberEmail", rememberedEmail);
        model.addAttribute("emailRemembered", cookie.getValue());
        return "login";
    }

    @PostMapping("/doLogin")
    public String doLogin(@ModelAttribute("user") User user,
                          Model model,
                          HttpServletResponse response) {

        if ("admin@gmail.com".equals(user.getEmail()) && "123456".equals(user.getPassword())) {
            Cookie cookie = new Cookie("rememberEmail", user.getEmail());
            cookie.setMaxAge(24 * 60 * 60);
            response.addCookie(cookie);

            model.addAttribute("message", "✅ Login successful!");
        } else {
            model.addAttribute("message", "❌ Invalid credentials. Try again.");
            user.setEmail("");
        }

        return "login";
    }
}
