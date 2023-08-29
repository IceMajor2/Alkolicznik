package com.demo.alkolicznik.gui.store;

import com.demo.alkolicznik.dto.store.StoreDeleteDTO;
import com.demo.alkolicznik.dto.store.StoreRequestDTO;
import com.demo.alkolicznik.dto.store.StoreResponseDTO;
import com.demo.alkolicznik.dto.store.StoreUpdateDTO;
import com.demo.alkolicznik.exceptions.ApiException;
import com.demo.alkolicznik.gui.MainLayout;
import com.demo.alkolicznik.gui.templates.FormTemplate;
import com.demo.alkolicznik.gui.templates.ViewTemplate;
import com.demo.alkolicznik.gui.utils.GuiUtils;
import com.demo.alkolicznik.security.AuthenticatedUser;
import com.demo.alkolicznik.utils.ModelDtoConverter;
import com.demo.alkolicznik.utils.request.CookieUtils;
import com.demo.alkolicznik.utils.request.RequestUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinRequest;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.Cookie;
import org.springframework.http.HttpMethod;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Route(value = "store", layout = MainLayout.class)
@PageTitle("StoreBase | Alkolicznik")
@PermitAll
public class StoreView extends ViewTemplate<StoreRequestDTO, StoreResponseDTO> {

    private static final TypeReference<List<StoreResponseDTO>> STORES_DTO_REF =
            new TypeReference<List<StoreResponseDTO>>() {
            };

    private StoreForm wizard;

    public StoreView() {
        super("Stores");

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
        if (!AuthenticatedUser.isUser()) {
            Cookie authCookie = CookieUtils.getAuthCookie(VaadinRequest.getCurrent());
            List<StoreResponseDTO> stores = RequestUtils.request(HttpMethod.GET,
                    "/api/store", authCookie, STORES_DTO_REF);
            this.grid.setItems(stores);
            updateDisplayText("entire Poland");
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
            Cookie authCookie = CookieUtils.getAuthCookie(VaadinRequest.getCurrent());
            List<StoreResponseDTO> stores = RequestUtils.request(HttpMethod.GET, "/api/store",
                    Map.of("city", city), authCookie, STORES_DTO_REF);
            this.grid.setItems(stores);
        } catch (ApiException e) {
            this.grid.setItems(Collections.EMPTY_LIST);
        }
        updateDisplayText(city);
    }

    @Override
    protected Grid getGrid() {
        this.grid = new Grid<>();
        grid.setSizeFull();

        if (!AuthenticatedUser.isUser()) {
            grid.addColumn(store -> store.getId()).setHeader("ID");
        }
        grid.addComponentColumn(GuiUtils::getVaadinImage).setHeader("Image");
        grid.addColumn(store -> store.getName()).setHeader("Store");
        grid.addColumn(store -> store.getCity()).setHeader("City");
        grid.addColumn(store -> store.getStreet()).setHeader("Street");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        if (!AuthenticatedUser.isUser()) {
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
            GuiUtils.showError("You did not select a store to update");
            return;
        }
        Long storeToUpdateId = selection.get().getId();
        StoreUpdateDTO newItem = ModelDtoConverter.convertToUpdate(event.getStore());
        try {
            Cookie authCookie = CookieUtils.getAuthCookie(VaadinRequest.getCurrent());
            RequestUtils.request(HttpMethod.PATCH, "/api/store/" + storeToUpdateId,
                    newItem, authCookie, StoreResponseDTO.class);
        } catch (ApiException e) {
            GuiUtils.showError(e.getMessage());
            return;
        }
        updateList();
        closeEditor();
    }

    private void deleteStore(StoreForm.DeleteEvent event) {
        try {
            Cookie authCookie = CookieUtils.getAuthCookie(VaadinRequest.getCurrent());
            RequestUtils.request(HttpMethod.DELETE, "/api/store", event.getStore(), authCookie, StoreDeleteDTO.class);
        } catch (ApiException e) {
            GuiUtils.showError(e.getMessage());
            return;
        }
        updateList();
        closeEditor();
    }

    private void createStore(StoreForm.CreateEvent event) {
        try {
            Cookie authCookie = CookieUtils.getAuthCookie(VaadinRequest.getCurrent());
            RequestUtils.request(HttpMethod.POST, "/api/store", event.getStore(), authCookie, StoreDeleteDTO.class);
        } catch (ApiException e) {
            GuiUtils.showError(e.getMessage());
            return;
        }
        updateList();
        closeEditor();
    }

    @Override
    protected StoreRequestDTO convertToRequest(StoreResponseDTO store) {
        return ModelDtoConverter.convertToRequest(store);
    }
}
