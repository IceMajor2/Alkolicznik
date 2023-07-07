package com.demo.alkolicznik;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
@Theme(value = "style")
@PWA(
        name = "Alkolicznik",
        shortName = "Alkolicznik"
)
public class AlkolicznikApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(AlkolicznikApplication.class, args);
    }

}
