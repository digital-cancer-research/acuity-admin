package com.acuity.visualisations.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/unauthorized")
public class AuthController {
    @RequestMapping
    public String createLoginForm() {
        return "unauthorized-user";
    }
}
