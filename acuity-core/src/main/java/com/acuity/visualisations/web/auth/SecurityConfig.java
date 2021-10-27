package com.acuity.visualisations.web.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import javax.servlet.http.HttpSessionEvent;
import java.util.Optional;

@Configuration
@EnableWebSecurity
@Order(150)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .anyRequest()
                .authenticated();
    }

    @Bean
    public FilterRegistrationBean authorizationRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new WebAuthorizationFilter());
        return filterRegistrationBean;
    }


    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher() {
            @Override
            public void sessionCreated(HttpSessionEvent event) {
                log.debug("==== Session is created ====");
                event.getSession().setMaxInactiveInterval(3600); // 1 hour
                super.sessionCreated(event);
            }

            @Override
            public void sessionDestroyed(HttpSessionEvent event) {
                String user = Optional.ofNullable(event.getSession())
                        .map(s -> (SecurityContext) s.getAttribute("SPRING_SECURITY_CONTEXT"))
                        .map(SecurityContext::getAuthentication)
                        .map(Authentication::getPrincipal)
                        .map(Object::toString)
                        .orElse("");
                log.debug("==== Session is destroyed for user {}====", user);
                super.sessionDestroyed(event);
            }
        };
    }
}
