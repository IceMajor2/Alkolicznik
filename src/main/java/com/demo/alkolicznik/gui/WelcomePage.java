package com.demo.alkolicznik.gui;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("")
public class WelcomePage extends VerticalLayout {

    public WelcomePage() {
        add(new H1("Hello world!"));
    }
}
