package com.demo.alkolicznik;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Theme(value = "style")
public class AlkolicznikApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(AlkolicznikApplication.class, args);
    }
}
