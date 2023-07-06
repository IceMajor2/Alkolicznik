package com.demo.alkolicznik.gui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;

public class BeerForm extends FormLayout {

    private TextField brand = new TextField("Marka");
    private TextField type = new TextField("Typ");
    private NumberField volume = new NumberField("Objętość");

    private Button save = new Button("Save");
    private Button cancel = new Button("Cancel");

    public BeerForm() {
        add(
                brand,
                type,
                volume,
                createButtonLayout()
        );
    }

    private Component createButtonLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        cancel.addClickShortcut(Key.ESCAPE);
        return new HorizontalLayout(save, cancel);
    }
}
