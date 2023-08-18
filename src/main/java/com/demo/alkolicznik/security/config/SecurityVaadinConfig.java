package com.demo.alkolicznik.security.config;

import com.demo.alkolicznik.gui.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

//@Configuration
//@EnableWebSecurity
//@EnableVaadin
public class SecurityVaadinConfig extends VaadinWebSecurity {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((auth) -> auth
				.requestMatchers(new AntPathRequestMatcher("/api/**")).anonymous()
				.requestMatchers("/error").anonymous()
                .requestMatchers(new AntPathRequestMatcher("/**")).permitAll());
        super.configure(http);
        setLoginView(http, LoginView.class);
    }

    @Override
    protected void configure(WebSecurity web) throws Exception {
		web.ignoring()
				.requestMatchers(new AntPathRequestMatcher("/api/**"))
				.requestMatchers("/error");
        super.configure(web);
    }
}
