package com.demo.alkolicznik.gui.auth;

import com.demo.alkolicznik.dto.security.AuthRequestDTO;
import com.demo.alkolicznik.dto.security.AuthResponseDTO;
import com.demo.alkolicznik.exceptions.ApiException;
import com.demo.alkolicznik.utils.request.CookieUtils;
import com.demo.alkolicznik.utils.request.RequestUtils;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.login.AbstractLogin;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.http.HttpMethod;

@PageTitle("Login | Alkolicznik")
@Route(value = "login")
@AnonymousAllowed
public class LoginView extends VerticalLayout {

    private VerticalLayout loginWrapper;
    private LoginForm loginForm;
    private Component accountsList;

    private HorizontalLayout toolbar;

    public LoginView() {
        toolbar = getToolbar();

        loginForm = configureLoginForm();
        loginWrapper = getLoginFormWrapped();
        //accountsList = getAccountInfoPanel();

        super.setAlignItems(Alignment.CENTER);

        add(toolbar, loginWrapper/*, accountsList*/);
    }

    private VerticalLayout getLoginFormWrapped() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        verticalLayout.setAlignItems(Alignment.CENTER);

        verticalLayout.add(loginForm);
        return verticalLayout;
    }

    private LoginForm configureLoginForm() {
        LoginForm loginForm = new LoginForm();
        loginForm.addLoginListener(loginEvent());
        loginForm.setForgotPasswordButtonVisible(false);
        return loginForm;
    }

    private HorizontalLayout getToolbar() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidthFull();
        horizontalLayout.setJustifyContentMode(JustifyContentMode.END);

        Button home = new Button("Home", event -> UI.getCurrent().navigate("/"));
        horizontalLayout.add(home);
        return horizontalLayout;
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
                UI.getCurrent().getPage().setLocation("/");
            } catch (ApiException e) {
                loginForm.setError(true);
            }
        };
    }

    private Component getAccountInfoPanel() {
        VerticalLayout layout = new VerticalLayout();
        layout.setAlignItems(Alignment.CENTER);

        H3 header = new H3("Example accounts");

        String accounts = "<p style=\"font-size:12px\"><b>[ROLE_ADMIN]</b>: <i>admin</i> : <i>admin</i><br>" +
                "<b>[ROLE_ACCOUNTANT]</b>: <i>user1</i> : <i>abcdef</i><br>" +
                "<b>[ROLE_USER]</b>: <i>user</i> : <i>user</i></p>";

        Html accountsCode = new Html(Jsoup.clean(accounts, Safelist.basic()));
        layout.add(header, accountsCode);
        return layout;
    }
}
