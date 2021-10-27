package com.acuity.visualisations.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;

@Controller
public class LogoutController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogoutController.class);

    private static final String VIEW_NAME = "login";

    @RequestMapping("/logout")
    public String logout(HttpServletResponse response) {
        LOGGER.debug("Returning {} view", VIEW_NAME);
        response.addHeader("Cache-Control", "no-cache");
        response.addHeader("Expires", "-1");
        return VIEW_NAME;
    }
}
