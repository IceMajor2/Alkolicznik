package com.demo.alkolicznik.gui.store;

import com.demo.alkolicznik.api.services.StoreService;
import com.demo.alkolicznik.dto.put.StoreUpdateDTO;
import com.demo.alkolicznik.dto.requests.StoreRequestDTO;
import com.demo.alkolicznik.dto.responses.StoreResponseDTO;
import com.demo.alkolicznik.exceptions.classes.NoSuchCityException;
import com.demo.alkolicznik.exceptions.classes.ObjectsAreEqualException;
import com.demo.alkolicznik.exceptions.classes.StoreAlreadyExistsException;
import com.demo.alkolicznik.exceptions.classes.StoreNotFoundException;
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

@Route(value = "store", layout = MainLayout.class)
@PageTitle("Baza sklepów | Alkolicznik")
@PermitAll
public class StoreView extends VerticalLayout {

    private UserDetailsImpl loggedUser;
    private StoreService storeService;

    private TextField filterCity;
    private H2 displayText;
    private Grid<StoreResponseDTO> grid;
    private StoreForm wizard;

    public StoreView(StoreService storeService) {
        this.loggedUser = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        this.storeService = storeService;

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

    private Component getCityToolbar() {
        filterCity = new TextField();
        filterCity.setPlaceholder("Wpisz miasto...");
        filterCity.setClearButtonVisible(true);
        filterCity.setValueChangeMode(ValueChangeMode.LAZY);
        filterCity.addValueChangeListener(event -> {
            updateList(filterCity.getValue());
        });

        if (loggedUser.isUser()) {
            return new HorizontalLayout(filterCity);
        }

        Button editorButton = new Button("Otwórz edytor");
        editorButton.addClickListener(click -> {
            grid.asSingleSelect().clear();
            openEditor(new StoreRequestDTO());
        });
        return new HorizontalLayout(filterCity, editorButton);
    }

    private void updateList() {
        if (!loggedUser.isUser()) {
            var store = storeService.getStores();
            this.grid.setItems(store);
            updateDisplayText("cała Polska");
        } else {
            updateList("Olsztyn");
            updateDisplayText("Olsztyn");
        }
    }

    private void updateList(String city) {
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

    private void updateDisplayText(String city) {
        this.displayText.setText("Sklepy w: " + city);
    }

    private void updateDisplayText() {
        if (loggedUser.isUser()) {
            updateDisplayText("Olsztyn");
        } else {
            updateDisplayText("cała Polska");
        }
    }

    private Component getDisplayText() {
        H2 text = new H2("Sklepy w: ");
        this.displayText = text;
        return text;
    }

    private Component getContent() {
        HorizontalLayout content;
        if (loggedUser.isUser()) {
            content = new HorizontalLayout(getStoreGrid());
        } else {
            content = new HorizontalLayout(getStoreGrid(), getStoreForm());
            content.setFlexGrow(2, grid);
            content.setFlexGrow(1, wizard);
        }
        content.setSizeFull();
        return content;
    }

    private Grid getStoreGrid() {
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
                editStore(event.getValue());
            });
        }
        return grid;
    }

    private Component getStoreForm() {
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

    private void editStore(StoreResponseDTO store) {
        if (store == null) {
            closeEditor();
        } else {
            StoreRequestDTO formStore = convertToRequest(store);
            openEditor(formStore);
        }
    }

    private void openEditor(StoreRequestDTO store) {
        wizard.setStore(store);
        wizard.setVisible(true);
    }

    private StoreRequestDTO convertToRequest(StoreResponseDTO store) {
        StoreRequestDTO storeRequest = new StoreRequestDTO();
        storeRequest.setName(store.getName());
        storeRequest.setCity(store.getCity());
        storeRequest.setStreet(store.getStreet());
        return storeRequest;
    }

    private void closeEditor() {
        if (wizard != null) {
            wizard.setStore(null);
            wizard.setVisible(false);
        }
    }
}
