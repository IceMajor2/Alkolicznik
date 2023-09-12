package com.demo.alkolicznik.gui.auth;

import com.demo.alkolicznik.dto.security.AuthLogoutDTO;
import com.demo.alkolicznik.utils.request.CookieUtils;
import com.demo.alkolicznik.utils.request.RequestUtils;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;

public class LogoutButton extends Button {

    public LogoutButton() {
        super("Sign out", LogoutButton::onClickEvent);
    }

    private static void onClickEvent(ClickEvent<Button> event) {
        Cookie authCookie = CookieUtils.getAuthCookie(VaadinRequest.getCurrent());
        RequestUtils.request(HttpMethod.POST, "/api/auth/logout", authCookie, AuthLogoutDTO.class);
        HttpServletRequest request = (HttpServletRequest) VaadinRequest.getCurrent();
        VaadinService.getCurrentResponse()
                .addCookie(CookieUtils.createExpiredTokenCookie(request));
        UI.getCurrent().getPage().setLocation("/");
    }
}
