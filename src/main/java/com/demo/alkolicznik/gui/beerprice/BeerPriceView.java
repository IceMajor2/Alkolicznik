package com.demo.alkolicznik.gui.beerprice;

import com.demo.alkolicznik.dto.beerprice.BeerPriceDeleteDTO;
import com.demo.alkolicznik.dto.beerprice.BeerPriceParamRequestDTO;
import com.demo.alkolicznik.dto.beerprice.BeerPriceResponseDTO;
import com.demo.alkolicznik.exceptions.ApiException;
import com.demo.alkolicznik.exceptions.classes.NoSuchCityException;
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

@Route(value = "beer-price", layout = MainLayout.class)
@PageTitle("PriceBase | Alkolicznik")
@PermitAll
public class BeerPriceView extends ViewTemplate<BeerPriceParamRequestDTO, BeerPriceResponseDTO> {

    private static final TypeReference<List<BeerPriceResponseDTO>> PRICES_DTO_REF =
            new TypeReference<List<BeerPriceResponseDTO>>() {
            };

    private BeerPriceForm wizard;

    public BeerPriceView() {
        super("Prices");

        setSizeFull();
        add(
                getToolbar(new BeerPriceParamRequestDTO()),
                getSearchText(),
                getContent()
        );
        updateList();
        updateDisplayText();
        closeEditor();
    }

    @Override
    protected FormTemplate<BeerPriceParamRequestDTO> getForm() {
        wizard = new BeerPriceForm();
        wizard.setWidth("25em");

        wizard.addCreateListener(this::createPrice);
        wizard.addUpdateListener(this::updatePrice);
        wizard.addDeleteListener(this::deletePrice);
        wizard.addCloseListener(event -> closeEditor());
        return wizard;
    }

    @Override
    protected Grid<BeerPriceResponseDTO> getGrid() {
        this.grid = new Grid<>();
        grid.setSizeFull();

        if (!AuthenticatedUser.isUser()) {
            setColumnsForAdmin();
        } else {
            setColumnsForUser();
        }
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        if (!AuthenticatedUser.isUser()) {
            grid.asSingleSelect().addValueChangeListener(event -> {
                if (!(wizard instanceof BeerPriceForm)) {
                    wizard = new BeerPriceForm();
                }
                editModel(event.getValue());
            });
        }
        return grid;
    }

    @Override
    protected BeerPriceParamRequestDTO convertToRequest(BeerPriceResponseDTO priceResponse) {
        return new BeerPriceParamRequestDTO(priceResponse);
    }

    @Override
    protected void updateList(String city) {
        if (city.isBlank()) {
            updateList();
            return;
        }
        try {
            Cookie authCookie = CookieUtils.getAuthCookie(VaadinRequest.getCurrent());
            List<BeerPriceResponseDTO> prices = RequestUtils.request(HttpMethod.GET,
                    "/api/beer-price", Map.of("city", city), authCookie, PRICES_DTO_REF);
            this.grid.setItems(prices);
        } catch (NoSuchCityException e) {
            this.grid.setItems(Collections.EMPTY_LIST);
        }
        updateDisplayText(city);
    }

    @Override
    protected void updateList() {
        if (!AuthenticatedUser.isUser()) {
            Cookie authCookie = CookieUtils.getAuthCookie(VaadinRequest.getCurrent());
            List<BeerPriceResponseDTO> prices = RequestUtils.request(HttpMethod.GET,
                    "/api/beer-price", authCookie, PRICES_DTO_REF);
            this.grid.setItems(prices);
            updateDisplayText("entire Poland");
        } else {
            updateList("Olsztyn");
            updateDisplayText("Olsztyn");
        }
    }

    private void deletePrice(BeerPriceForm.DeleteEvent event) {
        BeerPriceParamRequestDTO request = event.getPrice();
        try {
            Cookie authCookie = CookieUtils.getAuthCookie(VaadinRequest.getCurrent());
            RequestUtils.request(HttpMethod.DELETE, "/api/beer-price", Map.of("store_id",
                            request.getLongStoreId(), "beer_id", request.getLongBeerId()),
                    authCookie, BeerPriceDeleteDTO.class);
        } catch (ApiException e) {
            GuiUtils.showError(e.getMessage());
            return;
        }
        updateList();
        closeEditor();
    }

    private void updatePrice(BeerPriceForm.UpdateEvent event) {
        BeerPriceParamRequestDTO request = event.getPrice();
        try {
            Cookie authCookie = CookieUtils.getAuthCookie(VaadinRequest.getCurrent());
            RequestUtils.request(HttpMethod.PATCH, "/api/beer-price", Map.of("store_id",
                    request.getLongStoreId(), "beer_id", request.getLongBeerId(), "price",
                    request.getPrice()), authCookie, BeerPriceResponseDTO.class);
        } catch (ApiException e) {
            GuiUtils.showError(e.getMessage());
            return;
        }
        updateList();
        closeEditor();
    }

    private void createPrice(BeerPriceForm.CreateEvent event) {
        BeerPriceParamRequestDTO request = event.getPrice();
        try {
            Cookie authCookie = CookieUtils.getAuthCookie(VaadinRequest.getCurrent());
            RequestUtils.request(HttpMethod.POST, "/api/store/" + request.getLongStoreId()
                    + "/beer-price", Map.of("beer_id", request.getLongBeerId(), "beer_price",
                    request.getPrice()), authCookie, BeerPriceResponseDTO.class);
        } catch (ApiException e) {
            GuiUtils.showError(e.getMessage());
            return;
        }
        updateList();
        closeEditor();
    }

    private void setColumnsForAdmin() {
        grid.addColumn(price -> price.getStore().getId()).setHeader("Store ID");
        grid.addColumn(price -> price.getBeer().getId()).setHeader("Beer ID");
        grid.addColumn(price -> price.getStore().getName()).setHeader("Store");
        grid.addColumn(price -> price.getStore().getCity()).setHeader("City");
        grid.addColumn(price -> price.getStore().getStreet()).setHeader("Street");
        grid.addColumn(price -> price.getBeer().getBrand()).setHeader("Beer");
        grid.addColumn(price -> price.getBeer().getType()).setHeader("Type");
        grid.addColumn(price -> price.getBeer().getVolume()).setHeader("Volume");
        grid.addColumn(price -> price.getPrice()).setHeader("Price");
    }

    private void setColumnsForUser() {
        grid.addColumn(price -> price.getStore().getName()).setHeader("Store");
        grid.addColumn(price -> price.getStore().getStreet()).setHeader("Street");
        grid.addColumn(price -> price.getBeer().getBrand()).setHeader("Beer");
        grid.addColumn(price -> price.getBeer().getType()).setHeader("Type");
        grid.addColumn(price -> price.getBeer().getVolume()).setHeader("Volume");
        grid.addColumn(price -> price.getPrice()).setHeader("Price");
    }
}
