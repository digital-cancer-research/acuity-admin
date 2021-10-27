package com.acuity.visualisations.web.controller;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AdminUiErrorController implements ErrorController {
    public static final String VIEW_NAME = "errorPage";

    @Override
    public String getErrorPath() {
        return "/error";
    }

    @GetMapping("/error")
    public ModelAndView errorPage() {
        return new ModelAndView(VIEW_NAME);
    }
}
