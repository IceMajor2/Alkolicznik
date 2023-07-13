package com.demo.alkolicznik.gui;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;

import java.util.List;

public class Template {

    public static List<Button> getStyledButtons() {
        Button create = new Button("Dodaj");
        Button update = new Button("Modyfikuj");
        Button delete = new Button("Usuń");
        Button close = new Button("Zamknij");

        create.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        update.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        List<Button> buttons = List.of(create, update, delete, close);

        close.addClickShortcut(Key.ESCAPE);
        return buttons;
    }
}
