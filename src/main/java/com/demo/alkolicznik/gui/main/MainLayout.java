package com.demo.alkolicznik.gui.main;

import com.demo.alkolicznik.gui.WelcomeView;
import com.demo.alkolicznik.gui.auth.LogoutButton;
import com.demo.alkolicznik.gui.beer.BeerView;
import com.demo.alkolicznik.gui.beerprice.BeerPriceView;
import com.demo.alkolicznik.gui.image.ImageView;
import com.demo.alkolicznik.gui.store.StoreView;
import com.demo.alkolicznik.security.AuthenticatedUser;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
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
        Image textLogo = new Logo.TextLogo();
        HorizontalLayout authButtonWrapper = getAuthButtonWrapped();

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), textLogo, authButtonWrapper);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, textLogo);
        header.setWidth(99, Unit.PERCENTAGE);

        addToNavbar(header);
    }

    private void createDrawer() {
        RouterLink homeView = new RouterLink("Home", WelcomeView.class);
        RouterLink beerView = new RouterLink("Beers", BeerView.class);
        RouterLink storeView = new RouterLink("Stores", StoreView.class);
        RouterLink pricesView = new RouterLink("Prices", BeerPriceView.class);
        RouterLink imagesView = null;
        if (AuthenticatedUser.hasAccountantRole())
            imagesView = new RouterLink("Images", ImageView.class);
        beerView.setHighlightCondition(HighlightConditions.sameLocation());

        VerticalLayout drawerLayout = new VerticalLayout();
        drawerLayout.add(homeView, beerView, storeView, pricesView);
        if (imagesView != null) drawerLayout.add(imagesView);
        addToDrawer(drawerLayout);
    }

    private HorizontalLayout getAuthButtonWrapped() {
        Button authButton = getAuthButton();
        HorizontalLayout wrapper = new HorizontalLayout(authButton);
        wrapper.setSizeFull();
        wrapper.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        return wrapper;
    }

    private Button getAuthButton() {
        Button authButton;
        if (AuthenticatedUser.isAuthenticated()) {
            authButton = new LogoutButton();
        } else {
            authButton = new Button("Log in", click ->
                    UI.getCurrent().navigate("login"));
        }
        return authButton;
    }
}
