package xyz.mdbots.api.Config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import xyz.mdbots.api.Filters.AuthFilter;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<AuthFilter> authFilterRegistration(AuthFilter authFilter) {
        FilterRegistrationBean<AuthFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(authFilter);
        registration.addUrlPatterns("/bots/me/*");
        registration.addUrlPatterns("/me/*");
        registration.addUrlPatterns("/auth/verify");
        registration.setOrder(1);
        return registration;
    }
}
