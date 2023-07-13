package com.demo.alkolicznik.gui.templates;

import com.demo.alkolicznik.security.UserDetailsImpl;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class ViewTemplate<REQUEST, RESPONSE> extends VerticalLayout {

    protected UserDetailsImpl loggedUser;

    protected TextField filterCity;
    protected H2 displayText;
    protected Grid<RESPONSE> grid;
    protected FormTemplate<REQUEST> wizard;

    public ViewTemplate() {
        this.loggedUser = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    protected Component getSearchText(String model) {
        this.displayText = new H2("%s w: ".formatted(model));
        return displayText;
    }

    protected Component getToolbar(REQUEST emptyRequest) {
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
            openEditor(emptyRequest);
        });
        return new HorizontalLayout(filterCity, editorButton);
    }

    protected Component getContent() {
        HorizontalLayout content;
        if (loggedUser.isUser()) {
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
        wizard.setModel(model);
        wizard.setVisible(true);
    }

    protected void closeEditor() {
        wizard.setModel(null);
        wizard.setVisible(false);
    }

    protected void editModel(RESPONSE model) {
        if (model == null) {
            closeEditor();
        } else {
            REQUEST requestModel = convertToRequest(model);
            openEditor(requestModel);
        }
    }

    protected abstract FormTemplate<REQUEST> getForm();

    protected abstract Grid<RESPONSE> getGrid();

    protected abstract REQUEST convertToRequest(RESPONSE response);

    protected abstract void updateList(String city);

    protected abstract void updateList();

    protected abstract void updateDisplayText(String city);

    protected abstract void updateDisplayText();
}
