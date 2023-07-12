package com.demo.alkolicznik.gui.beer;

import com.demo.alkolicznik.api.services.BeerService;
import com.demo.alkolicznik.dto.requests.BeerRequestDTO;
import com.demo.alkolicznik.dto.responses.BeerResponseDTO;
import com.demo.alkolicznik.exceptions.classes.NoSuchCityException;
import com.demo.alkolicznik.gui.MainLayout;
import com.demo.alkolicznik.security.UserDetailsImpl;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

@Route(value = "beer", layout = MainLayout.class)
@PageTitle("Baza piw")
@PermitAll
public class BeerView extends VerticalLayout {

    private UserDetailsImpl loggedUser;
    private BeerService beerService;

    private Component searchToolbar;
    private TextField filterCity;
    private H2 displayText;
    private Grid<BeerResponseDTO> grid;
    private BeerForm form;

    public BeerView(BeerService beerService) {
        this.loggedUser = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        this.beerService = beerService;

        // addClassName("list-view");
        setSizeFull();
        add(
                getCityToolbar(),
                getDisplayText(),
                getContent()
        );
        updateList();
        updateDisplayText();
        closeEditor();
    }

    private Component getDisplayText() {
        H2 text = new H2("Piwa w: ");
        this.displayText = text;
        return text;
    }

    private Component getContent() {
        HorizontalLayout content;
        if (loggedUser.isUser()) {
            content = new HorizontalLayout(getBeerGrid());
        } else {
            content = new HorizontalLayout(getBeerGrid(), getBeerForm());
            content.setFlexGrow(2, grid);
            content.setFlexGrow(1, form);
        }
        content.setSizeFull();
        return content;
    }

    private Component getBeerForm() {
        this.form = new BeerForm();
        form.setWidth("25em");

        form.addSaveListener(this::saveBeer);
        // form.addDeleteListener(this::deleteBeer);
        form.addCloseListener(event -> closeEditor());
        return form;
    }

    private Component getCityToolbar() {
        filterCity = new TextField();
        filterCity.setPlaceholder("Wpisz miasto...");
        filterCity.setClearButtonVisible(true);
        filterCity.setValueChangeMode(ValueChangeMode.LAZY);
        filterCity.addValueChangeListener(event -> {
            updateList(filterCity.getValue());
        });

        Button getCityButton = new Button("Szukaj");

        HorizontalLayout toolbar = new HorizontalLayout(filterCity, getCityButton);
        this.searchToolbar = toolbar;
        return toolbar;
    }

    private void closeEditor() {
        if (!loggedUser.isUser()) {
            form.setBeer(null);
            form.setVisible(false);
            removeClassName("editing");
        }
    }

    private void saveBeer(BeerForm.SaveEvent saveEvent) {
        //closeEditor();
    }

    private void deleteBeer(BeerForm.DeleteEvent deleteEvent) {
        // to implement
    }

    private Grid getBeerGrid() {
        this.grid = new Grid<>();
        grid.setSizeFull();

        grid.addColumn(beer -> beer.getId()).setHeader("Id");
        grid.addColumn(beer -> beer.getBrand()).setHeader("Marka");
        grid.addColumn(beer -> beer.getType()).setHeader("Typ");
        grid.addColumn(beer -> beer.getVolume()).setHeader("Objętość");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        if (!loggedUser.isUser()) {
            grid.asSingleSelect().addValueChangeListener(event -> editBeer(event.getValue()));
        }
        return grid;
    }

    private void editBeer(BeerResponseDTO beer) {
        if (beer == null) {
            closeEditor();
        } else {
            BeerRequestDTO formBeer = convertToRequest(beer);
            form.setBeer(formBeer);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private BeerRequestDTO convertToRequest(BeerResponseDTO beerResponse) {
        BeerRequestDTO beerRequest = new BeerRequestDTO();
        beerRequest.setBrand(beerResponse.getBrand());
        beerRequest.setType(beerResponse.getType());
        beerRequest.setVolume(beerResponse.getVolume());
        return beerRequest;
    }

    private void updateList(String city) {
        if (city.isBlank()) {
            updateList();
            return;
        }
        try {
            var beers = beerService.getBeers(city);
            this.grid.setItems(beers);
        } catch (NoSuchCityException e) {
            this.grid.setItems(Collections.EMPTY_LIST);
        }
        updateDisplayText(city);
    }

    private void updateList() {
        if (!loggedUser.isUser()) {
            var beers = beerService.getBeers();
            this.grid.setItems(beers);
            updateDisplayText("cała Polska");
        } else {
            updateList("Olsztyn");
            updateDisplayText("Olsztyn");
        }
    }

    private void updateDisplayText(String city) {
        this.displayText.setText("Piwa w: " + city);
    }

    private void updateDisplayText() {
        if (loggedUser.isUser()) {
            updateDisplayText("Olsztyn");
        } else {
            updateDisplayText("cała Polska");
        }
    }
}
