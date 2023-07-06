package com.demo.alkolicznik.gui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

@Route("")
@PageTitle("Alkolicznik")
public class WelcomePage extends VerticalLayout {

    public WelcomePage() {
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        List<Component> components = getMainPageComponents();
        add(components);
    }

    public List<Component> getMainPageComponents() {
        List<Component> components = new ArrayList<>();

        H1 header = new H1("Alkolicznik");
        Button login = new Button("Zaloguj się");
        Button beers = new Button("Piwa");
        Button stores = new Button("Sklepy");
        Button button = new Button("Ceny");

        components.add(header);
        components.add(login);
        components.add(beers);
        components.add(stores);
        components.add(button);
        return components;
    }
}
