package com.demo.alkolicznik.gui.templates;

import com.demo.alkolicznik.gui.config.ConfigProperties;
import com.demo.alkolicznik.security.AuthenticatedUser;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

public abstract class ViewTemplate<REQUEST, RESPONSE> extends VerticalLayout {

    protected TextField filterCity;
    protected H2 displayText;
    protected Grid<RESPONSE> grid;
    protected FormTemplate<REQUEST> wizard;

    protected String textModel;
    protected static String DEFAULT_CITY;
    protected static String COUNTRY;

    public ViewTemplate(String textModel, ConfigProperties configProperties) {
        this.textModel = textModel;
        ViewTemplate.DEFAULT_CITY = configProperties.getDefaultCity();
        ViewTemplate.COUNTRY = configProperties.getCountry();
    }

    protected Component getSearchText() {
        this.displayText = new H2("%s in: ".formatted(textModel));
        return displayText;
    }

    protected Component getToolbar(REQUEST emptyRequest) {
        filterCity = new TextField();
        filterCity.setPlaceholder("Enter a city...");
        filterCity.setClearButtonVisible(true);
        filterCity.setValueChangeMode(ValueChangeMode.LAZY);
        filterCity.addValueChangeListener(event -> {
            updateList(filterCity.getValue());
        });

        if (AuthenticatedUser.isUser()) {
            return new HorizontalLayout(filterCity);
        }

        Button editorButton = new Button("Open editor");
        editorButton.addClickListener(click -> {
            grid.asSingleSelect().clear();
            openEditor(emptyRequest);
        });
        return new HorizontalLayout(filterCity, editorButton);
    }

    protected Component getContent() {
        HorizontalLayout content;
        if (AuthenticatedUser.isUser()) {
            content = new HorizontalLayout(getGrid());
        } else {
            wizard = getForm();
            grid = getGrid();
            content = new HorizontalLayout(grid, wizard);
            content.setFlexGrow(2, grid);
            content.setFlexGrow(1, wizard);
        }
        content.setSizeFull();
        return content;
    }

    protected void openEditor(REQUEST model) {
        if (wizard != null) {
            wizard.setModel(model);
            wizard.setVisible(true);
        }
    }

    protected void closeEditor() {
        if (wizard != null) {
            wizard.setModel(null);
            wizard.setVisible(false);
        }
    }

    protected void editModel(RESPONSE model) {
        if (model == null) {
            closeEditor();
        } else {
            REQUEST requestModel = convertToRequest(model);
            openEditor(requestModel);
        }
    }

    protected void updateDisplayText(String city) {
        this.displayText.setText("%s in: %s".formatted(textModel, city));
    }

    protected void updateDisplayText() {
        if (AuthenticatedUser.isUser()) {
            updateDisplayText(DEFAULT_CITY);
        } else {
            updateDisplayText("entire " + COUNTRY);
        }
    }

    protected abstract FormTemplate<REQUEST> getForm();

    protected abstract Grid<RESPONSE> getGrid();

    protected abstract REQUEST convertToRequest(RESPONSE response);

    protected abstract void updateList(String city);

    protected abstract void updateList();
}
