package com.demo.alkolicznik.gui;

import com.demo.alkolicznik.models.Beer;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("beer")
@PageTitle("Baza piw")
public class BeerPage extends VerticalLayout {

    private Grid<Beer> grid;
    private BeerForm form;
    private Component searchToolbar;

    public BeerPage() {
        setSizeFull();
        add(
                getCityToolbar(),
                getContent()
        );
    }

    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(getBeerGrid(), getBeerForm());
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.setSizeFull();
        return content;
    }

    private BeerForm getBeerForm() {
        this.form = new BeerForm();
        form.setWidth("25em");
        return form;
    }

    private Component getCityToolbar() {
        TextField filterCityText = new TextField();
        filterCityText.setPlaceholder("Wpisz miasto...");
        filterCityText.setClearButtonVisible(true);
        filterCityText.setValueChangeMode(ValueChangeMode.LAZY);

        Button getCityButton = new Button("Szukaj");

        HorizontalLayout toolbar = new HorizontalLayout(filterCityText, getCityButton);
        this.searchToolbar = toolbar;
        return toolbar;
    }

    private Component getBeerGrid() {
        this.grid = new Grid<>();
        grid.setSizeFull();

        grid.addColumn(beer -> beer.getId()).setHeader("Id");
        grid.addColumn(beer -> beer.getBrand()).setHeader("Marka");
        grid.addColumn(beer -> beer.getType()).setHeader("Typ");
        grid.addColumn(beer -> beer.getVolume()).setHeader("Objętość");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        return grid;
    }
}
