package com.demo.alkolicznik.gui;

import com.demo.alkolicznik.gui.beer.BeerView;
import com.demo.alkolicznik.security.AuthService;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;
import org.springframework.beans.factory.annotation.Autowired;

public class MainLayout extends AppLayout {

    private AuthService authService;

    public MainLayout(@Autowired AuthService authService) {
        this.authService = authService;

        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("Alkolicznik");

        logo.addClassName("logo");
        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo, getAuthButton());

        header.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        header.expand(logo);
        header.setWidthFull();

        addToNavbar(header);
    }

    private void createDrawer() {
        RouterLink listView = new RouterLink("Piwa", BeerView.class);
        listView.setHighlightCondition(HighlightConditions.sameLocation());

        addToDrawer(new VerticalLayout(
                listView
        ));
    }

    private Button getAuthButton() {
        Button loginout = new Button("Wyloguj się", click -> authService.logout());
        return loginout;
    }
}
