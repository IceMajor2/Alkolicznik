package com.demo.alkolicznik.gui;

import com.demo.alkolicznik.gui.auth.LogoutButton;
import com.demo.alkolicznik.security.AuthenticatedUser;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.ArrayList;
import java.util.List;

@Route("")
@PageTitle("Alkolicznik")
@AnonymousAllowed
public class WelcomeView extends VerticalLayout {

    public WelcomeView() {
        HorizontalLayout hl = getHorizontalLayout();
        VerticalLayout vl = getMainLayout();

        add(hl, vl);
    }

    private HorizontalLayout getHorizontalLayout() {
        HorizontalLayout hl = new HorizontalLayout();
        hl.setWidthFull();
        hl.setJustifyContentMode(JustifyContentMode.END);

        Button logInOut = getLogInOutButton();
        Button signup = getSignupButton();
        hl.add(signup, logInOut);
        return hl;
    }

    private VerticalLayout getMainLayout() {
        VerticalLayout vl = new VerticalLayout();
        vl.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        var components = getMainComponents();
        vl.add(components);

        return vl;
    }

    private Button getSignupButton() {
        Button signup = new Button("Sign up", event -> UI.getCurrent().navigate("signup"));
        if (AuthenticatedUser.isAuthenticated()) signup.setVisible(false);
        return signup;
    }

    private Button getLogInOutButton() {
        Button authButton;
        if (AuthenticatedUser.isAuthenticated()) {
            authButton = new LogoutButton();
        } else {
            authButton = new Button("Log in", click ->
                    UI.getCurrent().navigate("login"));
        }
        return authButton;
    }


    private List<Component> getMainComponents() {
        List<Component> components = new ArrayList<>();

        H1 header = new H1("Alkolicznik");
        Button beers = new Button("Beers", click -> UI.getCurrent().navigate("beer"));
        Button stores = new Button("Stores", click -> UI.getCurrent().navigate("store"));
        Button prices = new Button("Prices", click -> UI.getCurrent().navigate("beer-price"));
        Button images = null;
        if (AuthenticatedUser.hasAccountantRole())
            images = new Button("Images", click -> UI.getCurrent().navigate("image"));

        components.add(header);
        components.add(beers);
        components.add(stores);
        components.add(prices);
        if (images != null)
            components.add(images);
        return components;
    }
}
