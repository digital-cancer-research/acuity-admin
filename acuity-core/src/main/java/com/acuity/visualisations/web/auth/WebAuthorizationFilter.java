package com.acuity.visualisations.web.auth;

import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.acuity.visualisations.web.auth.UserInfoHolder.isCurrentUserDeveloper;

public class WebAuthorizationFilter extends GenericFilterBean {
    private static final String UNAUTHORIZED_ENDPOINT = "unauthorized";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (isCurrentUserDeveloper() || doesRequestContainUnauthorizedEndpoint(request)) {
            chain.doFilter(request, response);
        } else {
            ((HttpServletResponse) response).sendRedirect(UNAUTHORIZED_ENDPOINT);
        }
    }

    private boolean doesRequestContainUnauthorizedEndpoint(ServletRequest request) {
        return ((HttpServletRequest) request).getRequestURI().contains(UNAUTHORIZED_ENDPOINT);
    }
}
