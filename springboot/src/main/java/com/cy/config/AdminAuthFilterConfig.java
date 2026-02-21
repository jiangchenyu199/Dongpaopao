package com.cy.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class AdminAuthFilterConfig {

    @Bean
    public FilterRegistrationBean<AdminAuthFilter> adminAuthFilterRegistration(AdminAuthFilter filter) {
        FilterRegistrationBean<AdminAuthFilter> reg = new FilterRegistrationBean<>(filter);
        reg.setOrder(Ordered.LOWEST_PRECEDENCE - 10);
        return reg;
    }
}
