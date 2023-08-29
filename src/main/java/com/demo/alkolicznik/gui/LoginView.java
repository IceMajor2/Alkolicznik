package com.demo.alkolicznik.gui;

import com.demo.alkolicznik.dto.security.AuthRequestDTO;
import com.demo.alkolicznik.dto.security.AuthResponseDTO;
import com.demo.alkolicznik.utils.request.CookieUtils;
import com.demo.alkolicznik.utils.request.RequestUtils;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import org.springframework.http.HttpMethod;

@PageTitle("Login | Alkolicznik")
@Route(value = "login")
public class LoginView extends VerticalLayout {

    private TextField username;

    private PasswordField password;

    private Button login;

    public LoginView() {
        this.username = new TextField("Username");
        this.password = new PasswordField("Password");
        this.login = new Button("Login", loginEvent());

        add(username, password, login);
    }

    private ComponentEventListener<ClickEvent<Button>> loginEvent() {
        return event -> {
            AuthResponseDTO response = RequestUtils.request(HttpMethod.POST,
                    "/api/auth/authenticate",
                    new AuthRequestDTO(username.getValue(), password.getValue()),
                    AuthResponseDTO.class);
            VaadinService.getCurrentResponse()
                    .addCookie(CookieUtils.createTokenCookie(response.getToken()));
            UI.getCurrent().navigate(WelcomeView.class);
        };
    }
}
