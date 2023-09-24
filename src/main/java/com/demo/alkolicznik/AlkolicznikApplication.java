package com.demo.alkolicznik;

import com.vaadin.flow.component.page.AppShellConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AlkolicznikApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(AlkolicznikApplication.class, args);
    }
}
