package com.demo.alkolicznik.gui.beer;

import com.demo.alkolicznik.dto.beer.*;
import com.demo.alkolicznik.exceptions.ApiException;
import com.demo.alkolicznik.gui.MainLayout;
import com.demo.alkolicznik.gui.templates.FormTemplate;
import com.demo.alkolicznik.gui.templates.ViewTemplate;
import com.demo.alkolicznik.gui.utils.GuiUtils;
import com.demo.alkolicznik.security.AuthenticatedUser;
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

@Route(value = "beer", layout = MainLayout.class)
@PageTitle("BeerBase | Alkolicznik")
@PermitAll
public class BeerView extends ViewTemplate<BeerRequestDTO, BeerResponseDTO> {

    private static final String DEFAULT_CITY = "Olsztyn";

    private static final TypeReference<List<BeerResponseDTO>> BEERS_DTO_REF =
            new TypeReference<List<BeerResponseDTO>>() {
            };

    private BeerForm wizard;

    public BeerView() {
        super("Beers");

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

        if (!AuthenticatedUser.isUser()) {
            grid.addColumn(beer -> beer.getId()).setHeader("ID");
        }
        grid.addComponentColumn(GuiUtils::getVaadinImage).setHeader("Image");
        grid.addColumn(beer -> beer.getBrand()).setHeader("Brand");
        grid.addColumn(beer -> beer.getType()).setHeader("Type");
        grid.addColumn(beer -> beer.getVolume()).setHeader("Volume");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        if (!AuthenticatedUser.isUser()) {
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
        return new BeerRequestDTO(beerResponse);
    }

    @Override
    protected void updateList(String city) {
        if (city.isBlank()) {
            updateList();
            return;
        }
        try {
            Cookie authCookie = CookieUtils.getAuthCookie(VaadinRequest.getCurrent());
            List<BeerResponseDTO> beers = RequestUtils.request(HttpMethod.GET, "/api/beer",
                    Map.of("city", city), authCookie, BEERS_DTO_REF);
            grid.setItems(beers);
        } catch (ApiException e) {
            grid.setItems(Collections.EMPTY_LIST);
        }
        updateDisplayText(city);
    }

    @Override
    protected void updateList() {
        if (AuthenticatedUser.hasAccountantRole()) {
            Cookie authCookie = CookieUtils.getAuthCookie(VaadinRequest.getCurrent());
            var beers = RequestUtils.request(HttpMethod.GET, "/api/beer", authCookie, BEERS_DTO_REF);
            this.grid.setItems(beers);
            updateDisplayText("entire Poland");
        } else {
            updateList(DEFAULT_CITY);
            updateDisplayText(DEFAULT_CITY);
        }
    }

    private void deleteBeer(BeerForm.DeleteEvent event) {
        var request = event.getBeer();
        var deleteRequest = new BeerDeleteRequestDTO(request.getBrand(),
                request.getType(), request.getVolume());
        try {
            Cookie authCookie = CookieUtils.getAuthCookie(VaadinRequest.getCurrent());
            RequestUtils.request(HttpMethod.DELETE, "/api/beer", deleteRequest, authCookie,
                    BeerDeleteResponseDTO.class);
        } catch (ApiException e) {
            GuiUtils.showError(e.getMessage());
            return;
        }
        updateList();
        closeEditor();
    }

    private void createBeer(BeerForm.CreateEvent event) {
        var requestBody = event.getBeer();
        try {
            Cookie authCookie = CookieUtils.getAuthCookie(VaadinRequest.getCurrent());
            RequestUtils.request(HttpMethod.POST, "/api/beer", requestBody, authCookie,
                    BeerResponseDTO.class);
        } catch (ApiException e) {
            GuiUtils.showError(e.getMessage());
            return;
        }
        updateList();
        closeEditor();
    }

    private void updateBeer(BeerForm.UpdateEvent event) {
        Optional<BeerResponseDTO> selection = grid.getSelectionModel().getFirstSelectedItem();
        if (selection.isEmpty()) {
            GuiUtils.showError("First select the beer to update from the grid");
            return;
        }
        Long beerToUpdateId = selection.get().getId();
        BeerUpdateDTO requestBody = new BeerUpdateDTO(event.getBeer());
        try {
            Cookie cookie = CookieUtils.getAuthCookie(VaadinRequest.getCurrent());
            RequestUtils.request(HttpMethod.PATCH, "/api/beer/" + beerToUpdateId,
                    requestBody, cookie, BeerResponseDTO.class);
        } catch (ApiException e) {
            GuiUtils.showError(e.getMessage());
            return;
        }
        updateList();
        closeEditor();
    }
}
