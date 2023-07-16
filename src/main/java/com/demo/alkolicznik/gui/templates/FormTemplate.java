package com.demo.alkolicznik.gui.templates;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.Binder;

public abstract class FormTemplate<REQUEST> extends FormLayout {


    private final Class<REQUEST> type;

    protected Binder<REQUEST> binder;

    protected Button create = new Button("Dodaj");
    protected Button update = new Button("Modyfikuj");
    protected Button delete = new Button("Usuń");
    protected Button close = new Button("Zamknij");

    protected void setModel(REQUEST model) {
        binder.setBean(model);
    }

    protected FormTemplate(Class<REQUEST> type) {
        this.type = type;
        binder = new Binder<>(type);
    }

    protected Component createButtonLayout() {
        create.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        update.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        close.addClickShortcut(Key.ESCAPE);
        return new HorizontalLayout(create, update, delete, close);
    }
}
