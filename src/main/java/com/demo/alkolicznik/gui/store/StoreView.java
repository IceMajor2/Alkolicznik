package com.demo.alkolicznik.gui.store;

import com.demo.alkolicznik.api.services.StoreService;
import com.demo.alkolicznik.dto.store.StoreUpdateDTO;
import com.demo.alkolicznik.dto.store.StoreRequestDTO;
import com.demo.alkolicznik.dto.store.StoreResponseDTO;
import com.demo.alkolicznik.exceptions.classes.NoSuchCityException;
import com.demo.alkolicznik.exceptions.classes.ObjectsAreEqualException;
import com.demo.alkolicznik.exceptions.classes.store.StoreAlreadyExistsException;
import com.demo.alkolicznik.exceptions.classes.store.StoreNotFoundException;
import com.demo.alkolicznik.gui.templates.FormTemplate;
import com.demo.alkolicznik.gui.MainLayout;
import com.demo.alkolicznik.gui.templates.ViewTemplate;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.util.Collections;
import java.util.Optional;

@Route(value = "store", layout = MainLayout.class)
@PageTitle("Baza sklepów | Alkolicznik")
@PermitAll
public class StoreView extends ViewTemplate<StoreRequestDTO, StoreResponseDTO> {

    private StoreService storeService;
    private StoreForm wizard;

    public StoreView(StoreService storeService) {
        super("Sklepy");
        this.storeService = storeService;

        setSizeFull();
        add(
                getToolbar(new StoreRequestDTO()),
                getSearchText(),
                getContent()
        );
        updateList();
        updateDisplayText();
        closeEditor();
    }

    @Override
    protected void updateList() {
        if (!loggedUser.isUser()) {
            var store = storeService.getStores();
            this.grid.setItems(store);
            updateDisplayText("cała Polska");
        } else {
            updateList("Olsztyn");
            updateDisplayText("Olsztyn");
        }
    }

    @Override
    protected void updateList(String city) {
        if (city.isBlank()) {
            updateList();
            return;
        }
        try {
            var stores = storeService.getStores(city);
            this.grid.setItems(stores);
        } catch (NoSuchCityException e) {
            this.grid.setItems(Collections.EMPTY_LIST);
        }
        updateDisplayText(city);
    }

    @Override
    protected Grid getGrid() {
        this.grid = new Grid<>();
        grid.setSizeFull();

        if (!loggedUser.isUser()) {
            grid.addColumn(store -> store.getId()).setHeader("Id");
        }
        grid.addColumn(store -> store.getName()).setHeader("Sklep");
        grid.addColumn(store -> store.getCity()).setHeader("Miasto");
        grid.addColumn(store -> store.getStreet()).setHeader("Ulica");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        if (!loggedUser.isUser()) {
            grid.asSingleSelect().addValueChangeListener(event -> {
                if (!(wizard instanceof StoreForm)) {
                    wizard = new StoreForm();
                }
                editModel(event.getValue());
            });
        }
        return grid;
    }

    @Override
    protected FormTemplate<StoreRequestDTO> getForm() {
        wizard = new StoreForm();
        wizard.setWidth("25em");

        wizard.addCreateListener(this::createStore);
        wizard.addUpdateListener(this::updateStore);
        wizard.addDeleteListener(this::deleteStore);
        wizard.addCloseListener(event -> closeEditor());
        return wizard;
    }

    private void updateStore(StoreForm.UpdateEvent event) {
        Optional<StoreResponseDTO> selection = grid.getSelectionModel().getFirstSelectedItem();
        if (selection.isEmpty()) {
            Notification.show("Nie zaznaczyłeś sklepu do aktualizacji", 4000, Notification.Position.BOTTOM_END);
            return;
        }
        Long storeToUpdateId = selection.get().getId();
        StoreUpdateDTO newItem = convertToUpdate(event.getStore());
        try {
            storeService.update(storeToUpdateId, newItem);
        } catch (ObjectsAreEqualException e) {
            Notification.show("Nowe wartości są takie same jak poprzednie", 4000, Notification.Position.BOTTOM_END);
            return;
        }
        updateList();
        closeEditor();
    }

    private StoreUpdateDTO convertToUpdate(StoreRequestDTO store) {
        StoreUpdateDTO storeUpdate = new StoreUpdateDTO();
        storeUpdate.setName(store.getName());
        storeUpdate.setCity(store.getCity());
        storeUpdate.setStreet(store.getStreet());
        return storeUpdate;
    }

    private void deleteStore(StoreForm.DeleteEvent event) {
        try {
            storeService.delete(event.getStore());
        } catch (StoreNotFoundException e) {
            Notification.show("Nie znaleziono takiego sklepu", 4000, Notification.Position.BOTTOM_END);
        }
        updateList();
        closeEditor();
    }

    private void createStore(StoreForm.CreateEvent event) {
        try {
            storeService.add(event.getStore());
        } catch (StoreAlreadyExistsException e) {
            Notification.show("Sklep już istnieje", 4000, Notification.Position.BOTTOM_END);
            return;
        }
        updateList();
        closeEditor();
    }

    @Override
    protected StoreRequestDTO convertToRequest(StoreResponseDTO store) {
        StoreRequestDTO storeRequest = new StoreRequestDTO();
        storeRequest.setName(store.getName());
        storeRequest.setCity(store.getCity());
        storeRequest.setStreet(store.getStreet());
        return storeRequest;
    }
}
