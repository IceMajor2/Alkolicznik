package com.demo.alkolicznik.gui.beer;

import com.demo.alkolicznik.api.services.BeerService;
import com.demo.alkolicznik.dto.put.BeerUpdateDTO;
import com.demo.alkolicznik.dto.requests.BeerRequestDTO;
import com.demo.alkolicznik.dto.responses.BeerResponseDTO;
import com.demo.alkolicznik.exceptions.classes.BeerAlreadyExistsException;
import com.demo.alkolicznik.exceptions.classes.BeerNotFoundException;
import com.demo.alkolicznik.exceptions.classes.NoSuchCityException;
import com.demo.alkolicznik.exceptions.classes.ObjectsAreEqualException;
import com.demo.alkolicznik.gui.MainLayout;
import com.demo.alkolicznik.security.UserDetailsImpl;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Optional;

@Route(value = "beer", layout = MainLayout.class)
@PageTitle("Baza piw | Alkolicznik")
@PermitAll
public class BeerView extends VerticalLayout {

    private UserDetailsImpl loggedUser;
    private BeerService beerService;

    private TextField filterCity;
    private H2 displayText;
    private Grid<BeerResponseDTO> grid;
    private BeerForm wizard;

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
            content.setFlexGrow(1, wizard);
        }
        content.setSizeFull();
        return content;
    }

    private Component getBeerForm() {
        wizard = new BeerForm();
        wizard.setWidth("25em");

        wizard.addCreateListener(this::createBeer);
        wizard.addUpdateListener(this::updateBeer);
        wizard.addDeleteListener(this::deleteBeer);
        wizard.addCloseListener(event -> closeEditor());
        return wizard;
    }

    private void deleteBeer(BeerForm.DeleteEvent event) {
        try {
            beerService.delete(event.getBeer());
        } catch (BeerNotFoundException e) {
            Notification.show("Nie znaleziono takiego piwa", 4000, Notification.Position.BOTTOM_END);
        }
        updateList();
        closeEditor();
    }

    private Component getCityToolbar() {
        filterCity = new TextField();
        filterCity.setPlaceholder("Wpisz miasto...");
        filterCity.setClearButtonVisible(true);
        filterCity.setValueChangeMode(ValueChangeMode.LAZY);
        filterCity.addValueChangeListener(event -> {
            updateList(filterCity.getValue());
        });

        if(loggedUser.isUser()) {
            return new HorizontalLayout(filterCity);
        }

        Button editorButton = new Button("Otwórz edytor");
        editorButton.addClickListener(click -> {
            grid.asSingleSelect().clear();
            openEditor(new BeerRequestDTO());
        });
        return new HorizontalLayout(filterCity, editorButton);
    }

    private void createBeer(BeerForm.CreateEvent event) {
        try {
            beerService.add(event.getBeer());
        } catch(BeerAlreadyExistsException e) {
            Notification.show("Piwo już istnieje", 4000, Notification.Position.BOTTOM_END);
            return;
        }
        updateList();
        closeEditor();
    }

    private void updateBeer(BeerForm.UpdateEvent event) {
        Optional<BeerResponseDTO> selection = grid.getSelectionModel().getFirstSelectedItem();
        if(selection.isEmpty()) {
            Notification.show("Nie zaznaczyłeś piwa do aktualizacji", 4000, Notification.Position.BOTTOM_END);
            return;
        }
        Long beerToUpdateId = selection.get().getId();
        BeerUpdateDTO newItem = convertToUpdate(event.getBeer());
        try {
            beerService.update(beerToUpdateId, newItem);
        } catch (ObjectsAreEqualException e) {
            Notification.show("Nowe wartości są takie same jak poprzednie", 4000, Notification.Position.BOTTOM_END);
            return;
        }
        updateList();
        closeEditor();
    }

    private void closeEditor() {
        if (wizard != null) {
            wizard.setBeer(null);
            wizard.setVisible(false);
        }
    }

    private Grid getBeerGrid() {
        this.grid = new Grid<>();
        grid.setSizeFull();

        if (!loggedUser.isUser()) {
            grid.addColumn(beer -> beer.getId()).setHeader("Id");
        }
        grid.addColumn(beer -> beer.getBrand()).setHeader("Marka");
        grid.addColumn(beer -> beer.getType()).setHeader("Typ");
        grid.addColumn(beer -> beer.getVolume()).setHeader("Objętość");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        if (!loggedUser.isUser()) {
            grid.asSingleSelect().addValueChangeListener(event -> {
                if (!(wizard instanceof BeerForm)) {
                    wizard = new BeerForm();
                }
                editBeer(event.getValue());
            });
        }
        return grid;
    }

    private void openEditor(BeerRequestDTO beer) {
        wizard.setBeer(beer);
        wizard.setVisible(true);
    }

    private void editBeer(BeerResponseDTO beer) {
        if (beer == null) {
            closeEditor();
        } else {
            BeerRequestDTO formBeer = convertToRequest(beer);
            openEditor(formBeer);
        }
    }

    private BeerRequestDTO convertToRequest(BeerResponseDTO beerResponse) {
        BeerRequestDTO beerRequest = new BeerRequestDTO();
        beerRequest.setBrand(beerResponse.getBrand());
        beerRequest.setType(beerResponse.getType());
        beerRequest.setVolume(beerResponse.getVolume());
        return beerRequest;
    }

    private BeerUpdateDTO convertToUpdate(BeerRequestDTO beer) {
        BeerUpdateDTO beerUpdate = new BeerUpdateDTO();
        beerUpdate.setBrand(beer.getBrand());
        beerUpdate.setType(beer.getType());
        beerUpdate.setVolume(beer.getVolume());
        return beerUpdate;
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
