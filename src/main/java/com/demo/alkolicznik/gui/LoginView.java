package com.demo.alkolicznik.gui;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.demo.alkolicznik.dto.security.AuthRequestDTO;
import com.demo.alkolicznik.dto.security.AuthResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import jakarta.servlet.http.Cookie;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

@PageTitle("Login")
@Route(value = "login")
public class LoginView extends VerticalLayout {

//  TODO: fix fetching of properties
//	@Value("${trust.store}")
//	private Resource trustStore;
//
//	@Value("${trust.store.password}")
//	private String trustStorePassword;

	private WebClient webClient;

	private TextField username;

	private TextField password;

	private Button login;

	public LoginView(WebClient webClient) {
		this.webClient = webClient;
		this.username = new TextField("Username");
		this.password = new TextField("Password");
		this.login = new Button("Login", loginEvent());

		add(username, password, login);
	}

	private ComponentEventListener<ClickEvent<Button>> loginEvent() {
		return event -> {
			StringBuilder sb = new StringBuilder("");
			webClient
					.post()
					.uri("/api/auth/authenticate")
					.contentType(MediaType.APPLICATION_JSON)
					.bodyValue(getLoginRequestBody(username.getValue(), password.getValue()))
					.exchangeToMono(response -> {
						sb.append(getSetCookieValue(response));
						return response.bodyToMono(AuthResponseDTO.class);
					}).block();
			VaadinService.getCurrentResponse()
					.addCookie(new Cookie("Set-Cookie", sb.toString()));
		};
	}

	private String getSetCookieValue(ClientResponse response) {
		String setCookieValue = String.join(";", response.headers().header("Set-Cookie"));
		// TODO: Fix encoding as it breaks the string cookie value
		//  (but does not throw exception)
		return URLEncoder.encode(setCookieValue, StandardCharsets.UTF_8);
	}

	private String getLoginRequestBody(String username, String password) {
		ObjectMapper mapper = new ObjectMapper();
		AuthRequestDTO request = new AuthRequestDTO(username, password);
		try {
			return mapper.writeValueAsString(request);
		}
		catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
