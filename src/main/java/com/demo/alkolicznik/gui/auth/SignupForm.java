package com.demo.alkolicznik.gui.auth;

import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

import java.util.stream.Stream;

public class SignupForm extends FormLayout {

    private H3 title;

    private TextField username;

    private PasswordField password;
    private PasswordField confirmPassword;

    private Span errorMessageField;

    private Button submit;

    public SignupForm() {
        title = new H3("Signup");
        username = new TextField("Username");
        password = new PasswordField("Password");
        confirmPassword = new PasswordField("Confirm password");

        setRequiredIndicatorVisible(username, password, confirmPassword);

        errorMessageField = new Span();

        submit = new Button("Create account");
        submit.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(title, username, password, confirmPassword, errorMessageField, submit);

        // Max width of the Form
        setMaxWidth("500px");

        // Allow the form layout to be responsive.
        // On device widths 0-490px we have one column.
        // Otherwise, we have two columns.
        setResponsiveSteps(
                new ResponsiveStep("0", 1, ResponsiveStep.LabelsPosition.TOP),
                new ResponsiveStep("490px", 2, ResponsiveStep.LabelsPosition.TOP));

        // These components always take full width
        setColspan(title, 2);
        setColspan(username, 2);
        setColspan(errorMessageField, 2);
        setColspan(submit, 2);
    }


    private void setRequiredIndicatorVisible(HasValueAndElement<?, ?>... components) {
        Stream.of(components).forEach(comp -> comp.setRequiredIndicatorVisible(true));
    }
}