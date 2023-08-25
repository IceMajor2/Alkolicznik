package com.demo.alkolicznik.gui.beerprice;

import java.util.Collections;

import com.demo.alkolicznik.api.services.BeerPriceService;
import com.demo.alkolicznik.dto.beerprice.BeerPriceParamRequestDTO;
import com.demo.alkolicznik.dto.beerprice.BeerPriceResponseDTO;
import com.demo.alkolicznik.exceptions.classes.NoSuchCityException;
import com.demo.alkolicznik.exceptions.classes.ObjectsAreEqualException;
import com.demo.alkolicznik.exceptions.classes.beer.BeerNotFoundException;
import com.demo.alkolicznik.exceptions.classes.beerprice.BeerPriceAlreadyExistsException;
import com.demo.alkolicznik.exceptions.classes.beerprice.BeerPriceNotFoundException;
import com.demo.alkolicznik.exceptions.classes.store.StoreNotFoundException;
import com.demo.alkolicznik.gui.MainLayout;
import com.demo.alkolicznik.gui.templates.FormTemplate;
import com.demo.alkolicznik.gui.templates.ViewTemplate;
import com.demo.alkolicznik.utils.ModelDtoConverter;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route(value = "beer-price", layout = MainLayout.class)
@PageTitle("Baza cen | Alkolicznik")
@PermitAll
public class BeerPriceView extends ViewTemplate<BeerPriceParamRequestDTO, BeerPriceResponseDTO> {

    private BeerPriceService beerPriceService;
    private BeerPriceForm wizard;

    public BeerPriceView(BeerPriceService beerPriceService) {
        super("Ceny");
        this.beerPriceService = beerPriceService;

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
    protected BeerPriceParamRequestDTO convertToRequest(BeerPriceResponseDTO priceResponse) {
        return ModelDtoConverter.convertToRequest(priceResponse);
    }

    @Override
    protected void updateList(String city) {
        if (city.isBlank()) {
            updateList();
            return;
        }
        try {
            var prices = beerPriceService.getAllByCity(city);
            this.grid.setItems(prices);
        } catch (NoSuchCityException e) {
            this.grid.setItems(Collections.EMPTY_LIST);
        }
        updateDisplayText(city);
    }

    @Override
    protected void updateList() {
        if (!loggedUser.isUser()) {
            var prices = beerPriceService.getAll();
            this.grid.setItems(prices);
            updateDisplayText("cała Polska");
        } else {
            updateList("Olsztyn");
            updateDisplayText("Olsztyn");
        }
    }

    private void deletePrice(BeerPriceForm.DeleteEvent event) {
        BeerPriceParamRequestDTO request = event.getPrice();
        try {
            beerPriceService.delete(request.getStoreId().longValue(),
                    request.getBeerId().longValue());
        } catch (BeerPriceNotFoundException e) {
            Notification.show("Nie znaleziono takiej relacji", 4000, Notification.Position.BOTTOM_END);
            return;
        }
        updateList();
        closeEditor();
    }

    private void updatePrice(BeerPriceForm.UpdateEvent event) {
        BeerPriceParamRequestDTO request = event.getPrice();
        try {
            beerPriceService.update(request.getStoreId().longValue(),
                    request.getBeerId().longValue(),
                    null
            );
        } catch (ObjectsAreEqualException e) {
            Notification.show("Nowe wartości są takie same jak poprzednie", 4000, Notification.Position.BOTTOM_END);
            return;
        } catch (BeerPriceNotFoundException | BeerNotFoundException |
				 StoreNotFoundException e) {
            Notification.show("Edytować można jedynie cenę (relacja sklep-piwo musi już istnieć)",
                    4000, Notification.Position.BOTTOM_END);
            return;
        }
        updateList();
        closeEditor();
    }

    private void createPrice(BeerPriceForm.CreateEvent event) {
        BeerPriceParamRequestDTO request = event.getPrice();
        try {
            beerPriceService.addByParam(request.getStoreId().longValue(),
                    request.getBeerId().longValue(),
                    request.getPrice());
        } catch (BeerPriceAlreadyExistsException e) {
            Notification.show("Relacja już istnieje", 4000, Notification.Position.BOTTOM_END);
            return;
        } catch (StoreNotFoundException e) {
            Notification.show("Nie znaleziono sklepu o tym id", 4000, Notification.Position.BOTTOM_END);
            return;
        } catch (BeerNotFoundException e) {
            Notification.show("Nie znaleziono piwa o tym id", 4000, Notification.Position.BOTTOM_END);
            return;
        }
        updateList();
        closeEditor();
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
