package com.demo.alkolicznik.gui.beer;

import com.demo.alkolicznik.api.services.BeerService;
import com.demo.alkolicznik.dto.put.BeerUpdateDTO;
import com.demo.alkolicznik.dto.requests.BeerRequestDTO;
import com.demo.alkolicznik.dto.responses.BeerResponseDTO;
import com.demo.alkolicznik.exceptions.classes.*;
import com.demo.alkolicznik.gui.MainLayout;
import com.demo.alkolicznik.gui.templates.FormTemplate;
import com.demo.alkolicznik.gui.templates.ViewTemplate;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.util.Collections;
import java.util.Optional;

@Route(value = "beer", layout = MainLayout.class)
@PageTitle("Baza piw | Alkolicznik")
@PermitAll
public class BeerView extends ViewTemplate<BeerRequestDTO, BeerResponseDTO> {

    private BeerService beerService;
    private BeerForm wizard;

    public BeerView(BeerService beerService) {
        super("Piwa");
        this.beerService = beerService;

        setSizeFull();
        add(
                getToolbar(new BeerRequestDTO()),
                getSearchText(),
                getContent()
        );
        updateList();
        updateDisplayText();
        closeEditor();
    }

    @Override
    protected FormTemplate<BeerRequestDTO> getForm() {
        wizard = new BeerForm();
        wizard.setWidth("25em");

        wizard.addCreateListener(this::createBeer);
        wizard.addUpdateListener(this::updateBeer);
        wizard.addDeleteListener(this::deleteBeer);
        wizard.addCloseListener(event -> closeEditor());
        return wizard;
    }

    @Override
    protected Grid getGrid() {
        this.grid = new Grid<>();
        grid.setSizeFull();

        if (!loggedUser.isUser()) {
            grid.addColumn(beer -> beer.getId()).setHeader("Id");
        }
        grid.addComponentColumn(beer -> this.getImage(beer)).setHeader("Zdjęcie");
        grid.addColumn(beer -> beer.getBrand()).setHeader("Marka");
        grid.addColumn(beer -> beer.getType()).setHeader("Typ");
        grid.addColumn(beer -> beer.getVolume()).setHeader("Objętość");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        if (!loggedUser.isUser()) {
            grid.asSingleSelect().addValueChangeListener(event -> {
                if (wizard == null) {
                    wizard = new BeerForm();
                }
                editModel(event.getValue());
            });
        }
        return grid;
    }

    @Override
    protected BeerRequestDTO convertToRequest(BeerResponseDTO beerResponse) {
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

    @Override
    protected void updateList(String city) {
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

    @Override
    protected void updateList() {
        if (!loggedUser.isUser()) {
            var beers = beerService.getBeers();
            this.grid.setItems(beers);
            updateDisplayText("cała Polska");
        } else {
            updateList("Olsztyn");
            updateDisplayText("Olsztyn");
        }
    }

    private void deleteBeer(BeerForm.DeleteEvent event) {
        try {
            beerService.delete(event.getBeer());
        } catch (BeerNotFoundException e) {
            Notification.show("Nie znaleziono takiego piwa", 4000, Notification.Position.BOTTOM_END);
            return;
        }
        updateList();
        closeEditor();
    }

    private void createBeer(BeerForm.CreateEvent event) {
        try {
            beerService.add(event.getBeer());
        } catch (BeerAlreadyExistsException e) {
            Notification.show("Piwo już istnieje", 4000, Notification.Position.BOTTOM_END);
            return;
        }
        updateList();
        closeEditor();
    }

    private void updateBeer(BeerForm.UpdateEvent event) {
        Optional<BeerResponseDTO> selection = grid.getSelectionModel().getFirstSelectedItem();
        if (selection.isEmpty()) {
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

    private Image getImage(BeerResponseDTO beer) {
        try {
            return beerService.getImageComponent(beer.getId());
        } catch (ImageNotFoundException e) {
            return null;
        }
    }
}
