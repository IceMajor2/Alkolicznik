package com.demo.alkolicznik.gui.templates;

import com.demo.alkolicznik.dto.city.CityDTO;
import com.demo.alkolicznik.gui.properties.ConfigProperties;
import com.demo.alkolicznik.security.AuthenticatedUser;
import com.demo.alkolicznik.utils.request.CookieUtils;
import com.demo.alkolicznik.utils.request.RequestUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinRequest;
import jakarta.servlet.http.Cookie;
import org.springframework.http.HttpMethod;

import java.util.List;

public abstract class ViewTemplate<REQUEST, RESPONSE> extends VerticalLayout {

    protected ComboBox<CityDTO> cityBox;
    protected H2 displayText;
    protected Grid<RESPONSE> grid;
    protected FormTemplate<REQUEST> wizard;

    protected String textModel;
    protected static String DEFAULT_CITY;
    protected static String EVERYWHERE_TEXT = "everywhere ;)";

    public ViewTemplate(String textModel, ConfigProperties configProperties) {
        this.textModel = textModel;
        ViewTemplate.DEFAULT_CITY = configProperties.getDefaultCity();
    }

    protected Component getSearchText() {
        this.displayText = new H2("%s in: ".formatted(textModel));
        return displayText;
    }

    protected Component getToolbar(REQUEST emptyRequest) {
        cityBox = getCityBox();

        if (AuthenticatedUser.isUser()) {
            return new HorizontalLayout(cityBox);
        }

        Button editorButton = new Button("Open editor");
        editorButton.addClickListener(click -> {
            grid.asSingleSelect().clear();
            openEditor(emptyRequest);
        });
        return new HorizontalLayout(cityBox, editorButton);
    }

    protected ComboBox<CityDTO> getCityBox() {
        ComboBox<CityDTO> cityBox = new ComboBox<>();
        cityBox.setAllowCustomValue(false);
        cityBox.setPlaceholder("Choose a city...");
        cityBox.setClearButtonVisible(true);
        cityBox.setWidth("20em");
        cityBox.setItemLabelGenerator(CityDTO::getCity);
        cityBox.addValueChangeListener(event -> {
            if(cityBox.getValue() == null) updateList();
            else updateList(cityBox.getValue().getCity());
        });
        setCityBoxItems(cityBox);
        return cityBox;
    }

    protected void setCityBoxItems(ComboBox<CityDTO> cityBox) {
        Cookie authCookie = CookieUtils.getAuthCookie(VaadinRequest.getCurrent());
        List<CityDTO> cities = RequestUtils.request(HttpMethod.GET, "/api/city", authCookie, new TypeReference<>() {});
        cityBox.setItems(cities);
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
            updateDisplayText(EVERYWHERE_TEXT);
        }
    }

    protected abstract FormTemplate<REQUEST> getForm();

    protected abstract Grid<RESPONSE> getGrid();

    protected abstract REQUEST convertToRequest(RESPONSE response);

    protected abstract void updateList(String city);

    protected abstract void updateList();
}
