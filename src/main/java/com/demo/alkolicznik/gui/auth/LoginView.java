package com.demo.alkolicznik.gui.auth;

import com.demo.alkolicznik.dto.security.AuthRequestDTO;
import com.demo.alkolicznik.dto.security.AuthResponseDTO;
import com.demo.alkolicznik.exceptions.ApiException;
import com.demo.alkolicznik.gui.WelcomeView;
import com.demo.alkolicznik.utils.request.CookieUtils;
import com.demo.alkolicznik.utils.request.RequestUtils;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.AbstractLogin;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import org.springframework.http.HttpMethod;

@PageTitle("Login | Alkolicznik")
@Route(value = "login")
public class LoginView extends VerticalLayout {

    private LoginForm loginForm;

    public LoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        this.loginForm = configureLoginForm();
        add(loginForm);
    }

    private LoginForm configureLoginForm() {
        LoginForm loginForm = new LoginForm();
        loginForm.addLoginListener(loginEvent());
        loginForm.setForgotPasswordButtonVisible(false);
        return loginForm;
    }

    private ComponentEventListener<AbstractLogin.LoginEvent> loginEvent() {
        return event -> {
            try {
                AuthResponseDTO response = RequestUtils.request(HttpMethod.POST,
                        "/api/auth/authenticate",
                        new AuthRequestDTO(event.getUsername(), event.getPassword()),
                        AuthResponseDTO.class);
                VaadinService.getCurrentResponse()
                        .addCookie(CookieUtils.createTokenCookie(response.getToken()));
                UI.getCurrent().navigate(WelcomeView.class);
            } catch (ApiException e) {
                loginForm.setError(true);
            }
        };
    }
}
