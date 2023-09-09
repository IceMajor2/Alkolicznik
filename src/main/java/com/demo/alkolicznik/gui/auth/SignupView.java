package com.demo.alkolicznik.gui.auth;

import com.demo.alkolicznik.dto.security.SignupRequestDTO;
import com.demo.alkolicznik.dto.security.SignupResponseDTO;
import com.demo.alkolicznik.exceptions.ApiException;
import com.demo.alkolicznik.gui.utils.GuiUtils;
import com.demo.alkolicznik.utils.request.CookieUtils;
import com.demo.alkolicznik.utils.request.RequestUtils;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.http.HttpMethod;


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
        signupForm.getSubmit().addClickListener(signupEvent());

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

    private ComponentEventListener<ClickEvent<Button>> signupEvent() {
        return event -> {
            try {
                SignupRequestDTO request = new SignupRequestDTO(signupForm.getUsername(), signupForm.getPassword());
                SignupResponseDTO response = RequestUtils.request(HttpMethod.POST, "/api/auth/signup",
                        request, SignupResponseDTO.class);
                VaadinService.getCurrentResponse()
                        .addCookie(CookieUtils.createTokenCookie(response.getToken()));
                UI.getCurrent().getPage().setLocation("/");
                GuiUtils.notify("You have been registered! Welcome.");
            } catch (ApiException e) {
                signupForm.setError(e.getMessage());
            }
        };
    }
}
