package com.demo.alkolicznik.gui;

import com.demo.alkolicznik.gui.beer.BeerView;
import com.demo.alkolicznik.gui.beerprice.BeerPriceView;
import com.demo.alkolicznik.gui.image.ImageView;
import com.demo.alkolicznik.gui.store.StoreView;
import com.demo.alkolicznik.security.AuthenticatedUser;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;

public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("Alkolicznik");

        logo.addClassName("logo");
        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo, getAuthButton());

        header.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        header.expand(logo);
        header.setWidthFull();

        addToNavbar(header);
    }

    private void createDrawer() {
        RouterLink homeView = new RouterLink("Home", WelcomeView.class);
        RouterLink beerView = new RouterLink("Beers", BeerView.class);
        RouterLink storeView = new RouterLink("Stores", StoreView.class);
        RouterLink pricesView = new RouterLink("Prices", BeerPriceView.class);
        RouterLink imagesView = null;
        if(AuthenticatedUser.hasAccountantRole())
            imagesView = new RouterLink("Images", ImageView.class);
        beerView.setHighlightCondition(HighlightConditions.sameLocation());

        addToDrawer(new VerticalLayout(
                homeView,
                beerView,
                storeView,
                pricesView
        ));
        if(imagesView != null) addToDrawer(imagesView);
    }

    private Button getAuthButton() {
        Button authButton;
        if (AuthenticatedUser.isAuthenticated()) {
            authButton = new Button("Sign out");
        } else {
            authButton = new Button("Log in", click ->
                    UI.getCurrent().navigate("login"));
        }
        return authButton;
    }
}
