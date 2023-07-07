package com.demo.alkolicznik.gui;

import com.demo.alkolicznik.gui.beer.BeerPage;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
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
        H1 logo = new H1("Alkolicznik");

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo);

        /*
        response = GET is_user_logged
        if(response == true) {
            addLogoutButton().onClickLogout();
         } else {
            do nothing;
         }
         */

        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setWidthFull();

        addToNavbar(header);
    }

    private void createDrawer() {
        RouterLink listView = new RouterLink("Piwa", BeerPage.class);
        listView.setHighlightCondition(HighlightConditions.sameLocation());

        addToDrawer(new VerticalLayout(
                listView
        ));
    }
}
