package com.demo.alkolicznik.security.config;

import com.demo.alkolicznik.exceptions.config.ExceptionLogConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private ResponseStatusExceptionResolver exceptionResolver;

    @Autowired
    public WebConfig(ExceptionLogConfigurer exceptionResolver) {
        this.exceptionResolver = exceptionResolver;
    }

    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        resolvers.add(0, exceptionResolver);
    }
}
