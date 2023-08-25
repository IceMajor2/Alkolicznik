package com.demo.alkolicznik.gui;

import java.util.ArrayList;
import java.util.List;

import com.demo.alkolicznik.models.User;
import com.demo.alkolicznik.security.AuthService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import org.springframework.security.core.context.SecurityContextHolder;

@Route("")
@PageTitle("Alkolicznik")
@AnonymousAllowed
public class WelcomeView extends VerticalLayout {

	private AuthService authService;

	private User loggedUser;

	public WelcomeView(AuthService authService) {
		this.loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		this.authService = authService;

		HorizontalLayout hl = getHorizontalLayout();
		VerticalLayout vl = getMainLayout();

		add(hl, vl);
	}

	private HorizontalLayout getHorizontalLayout() {
		HorizontalLayout hl = new HorizontalLayout();
		hl.setWidthFull();
		hl.setJustifyContentMode(JustifyContentMode.END);

		Button button = getAuthButton();
		hl.add(button);
		return hl;
	}

	private VerticalLayout getMainLayout() {
		VerticalLayout vl = new VerticalLayout();
		vl.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

		var components = getMainComponents();
		vl.add(components);

		return vl;
	}

	private Button getAuthButton() {
		Button authButton;
		if (loggedUser != null) {
			authButton = new Button("Wyloguj się");
		} else {
			authButton = new Button("Zaloguj się", click ->
					UI.getCurrent().navigate("login"));
		}
		return authButton;
	}


	private List<Component> getMainComponents() {
		List<Component> components = new ArrayList<>();

		H1 header = new H1("Alkolicznik");
		Button beers = new Button("Piwa", click -> UI.getCurrent().navigate("beer"));
		Button stores = new Button("Sklepy", click -> UI.getCurrent().navigate("store"));
		Button button = new Button("Ceny", click -> UI.getCurrent().navigate("beer-price"));

		components.add(header);
		components.add(beers);
		components.add(stores);
		components.add(button);
		return components;
	}
}
