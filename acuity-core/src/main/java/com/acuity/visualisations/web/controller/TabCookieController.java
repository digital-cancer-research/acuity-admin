package com.acuity.visualisations.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Controller
public class TabCookieController {
    private static final String TAB_ID = "TabId";

    @RequestMapping(value = "/setTabCookie", method = RequestMethod.POST)
    public void setBrowserTabId(final HttpServletResponse response, @RequestBody String browserTabId) {
        response.addCookie(new Cookie(TAB_ID, browserTabId));
    }
}
