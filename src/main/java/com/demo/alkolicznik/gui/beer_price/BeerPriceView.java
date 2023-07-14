package com.demo.alkolicznik.gui.beer_price;

import com.demo.alkolicznik.api.services.BeerPriceService;
import com.demo.alkolicznik.dto.requests.BeerPriceRequestDTO;
import com.demo.alkolicznik.dto.responses.BeerPriceResponseDTO;
import com.demo.alkolicznik.exceptions.classes.NoSuchCityException;
import com.demo.alkolicznik.gui.MainLayout;
import com.demo.alkolicznik.gui.templates.FormTemplate;
import com.demo.alkolicznik.gui.templates.ViewTemplate;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.util.Collections;

@Route(value = "beer-price", layout = MainLayout.class)
@PageTitle("Baza cen | Alkolicznik")
@PermitAll
public class BeerPriceView extends ViewTemplate<BeerPriceRequestDTO, BeerPriceResponseDTO> {

    private BeerPriceService beerPriceService;
    private BeerPriceForm wizard;

    public BeerPriceView(BeerPriceService beerPriceService) {
        super("Ceny");
        this.beerPriceService = beerPriceService;

        setSizeFull();
        add(
                getToolbar(new BeerPriceRequestDTO()),
                getSearchText(),
                getContent()
        );
        updateList();
        updateDisplayText();
        closeEditor();
    }

    @Override
    protected FormTemplate<BeerPriceRequestDTO> getForm() {
        wizard = new BeerPriceForm();
        wizard.setWidth("25em");

//        wizard.addCreateListener(this::createPrice);
//        wizard.addUpdateListener(this::updatePrice);
//        wizard.addDeleteListener(this::deletePrice);
        wizard.addCloseListener(event -> closeEditor());
        return wizard;
    }

    @Override
    protected Grid<BeerPriceResponseDTO> getGrid() {
        this.grid = new Grid<>();
        grid.setSizeFull();

        if (!loggedUser.isUser()) {
            setColumnsForAdmin();
        } else {
            setColumnsForUser();
        }
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        if (!loggedUser.isUser()) {
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
    protected BeerPriceRequestDTO convertToRequest(BeerPriceResponseDTO priceResponse) {
        BeerPriceRequestDTO priceRequest = new BeerPriceRequestDTO();
        priceRequest.setBeerName(priceResponse.getBeer().getFullName());
        priceRequest.setBeerVolume(priceResponse.getBeer().getVolume());
        priceRequest.setPrice(priceResponse.getAmountOnly());
        return priceRequest;
    }

    @Override
    protected void updateList(String city) {
        if (city.isBlank()) {
            updateList();
            return;
        }
        try {
            var prices = beerPriceService.getBeerPrices(city);
            this.grid.setItems(prices);
        } catch (NoSuchCityException e) {
            this.grid.setItems(Collections.EMPTY_LIST);
        }
        updateDisplayText(city);
    }

    @Override
    protected void updateList() {
        if (!loggedUser.isUser()) {
            var prices = beerPriceService.getBeerPrices();
            this.grid.setItems(prices);
            updateDisplayText("cała Polska");
        } else {
            updateList("Olsztyn");
            updateDisplayText("Olsztyn");
        }
    }

    private void setColumnsForAdmin() {
        grid.addColumn(price -> price.getStore().getId()).setHeader("Id sklepu");
        grid.addColumn(price -> price.getBeer().getId()).setHeader("Id piwa");
        grid.addColumn(price -> price.getStore().getName()).setHeader("Sklep");
        grid.addColumn(price -> price.getStore().getCity()).setHeader("Miasto");
        grid.addColumn(price -> price.getStore().getStreet()).setHeader("Ulica");
        grid.addColumn(price -> price.getBeer().getBrand()).setHeader("Piwo");
        grid.addColumn(price -> price.getBeer().getType()).setHeader("Typ");
        grid.addColumn(price -> price.getBeer().getVolume()).setHeader("Objętość");
        grid.addColumn(price -> price.getPrice()).setHeader("Cena");
    }

    private void setColumnsForUser() {
        grid.addColumn(price -> price.getStore().getName()).setHeader("Sklep");
        grid.addColumn(price -> price.getStore().getStreet()).setHeader("Ulica");
        grid.addColumn(price -> price.getBeer().getBrand()).setHeader("Piwo");
        grid.addColumn(price -> price.getBeer().getType()).setHeader("Typ");
        grid.addColumn(price -> price.getBeer().getVolume()).setHeader("Objętość");
        grid.addColumn(price -> price.getPrice()).setHeader("Cena");
    }
}
