package com.demo.alkolicznik.gui.auth;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;


@PageTitle("Sign up | Alkolicznik")
@Route(value = "signup")
@AnonymousAllowed
public class SignupView extends VerticalLayout {

    private HorizontalLayout signupWrapper;
    private SignupForm signupForm;

    private HorizontalLayout toolbar;

    public SignupView() {
        toolbar = getToolbar();

        signupForm = new SignupForm();
        signupWrapper = getSignupFormWrapped();

        add(toolbar, signupWrapper);
    }

    // due to Vaadin's awkard handling of 'FormLayouts' inside 'VerticalLayout'
    // a HorizontalLayout containing SignupForm must be created to center the latter component
    private HorizontalLayout getSignupFormWrapped() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidthFull();
        horizontalLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        horizontalLayout.add(signupForm);
        return horizontalLayout;
    }

    private HorizontalLayout getToolbar() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidthFull();
        horizontalLayout.setJustifyContentMode(JustifyContentMode.END);

        Button home = new Button("Home", event -> UI.getCurrent().navigate("/"));
        horizontalLayout.add(home);
        return horizontalLayout;
    }
}
