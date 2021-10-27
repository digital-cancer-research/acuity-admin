package com.acuity.visualisations.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {

    public static final String VIEW_NAME = "login";

    @RequestMapping("/login")
    public String login() {
        return VIEW_NAME;

    }
}
