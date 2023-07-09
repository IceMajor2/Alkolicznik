package com.demo.alkolicznik.gui;

import com.demo.alkolicznik.security.AuthService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Route("")
@PageTitle("Alkolicznik")
@AnonymousAllowed
public class WelcomeView extends VerticalLayout {

    private AuthService authService;

    public WelcomeView(@Autowired AuthService authService) {
        this.authService = authService;

        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        List<Component> components = getMainPageComponents();
        add(components);
    }

    public List<Component> getMainPageComponents() {
        List<Component> components = new ArrayList<>();

        H1 header = new H1("Alkolicznik");
        Button login = new Button("Zaloguj się", click -> UI.getCurrent().navigate("login"));
        Button beers = new Button("Piwa", click -> UI.getCurrent().navigate("beer"));
        Button stores = new Button("Sklepy", click -> UI.getCurrent().navigate("store"));
        Button button = new Button("Ceny", click -> UI.getCurrent().navigate("beer-price"));

        components.add(header);
        components.add(login);
        components.add(beers);
        components.add(stores);
        components.add(button);

        if (authService.getAuthenticatedUser() != null) {
            Button logout = new Button("Wyloguj się", click -> authService.logout());
            components.add(logout);
        }
        return components;
    }
}
