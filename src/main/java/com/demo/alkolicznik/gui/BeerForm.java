package com.demo.alkolicznik.gui;

import com.demo.alkolicznik.dto.requests.BeerRequestDTO;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

public class BeerForm extends FormLayout {

    private Binder<BeerRequestDTO> binder;

    private BeerRequestDTO beer;

    private TextField brand = new TextField("Marka");
    private TextField type = new TextField("Typ");
    private NumberField volume = new NumberField("Objętość");

    private Button save = new Button("Save");
    private Button delete = new Button("Delete");
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
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, beer)));
        cancel.addClickListener(event -> fireEvent(new CloseEvent(this)));

        save.addClickShortcut(Key.ENTER);
        cancel.addClickShortcut(Key.ESCAPE);
        return new HorizontalLayout(save, delete, cancel);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(beer);
            fireEvent(new SaveEvent(this, beer));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    public void setBeer(BeerRequestDTO beer) {
        this.beer = beer;
        binder.readBean(beer);
    }

    private Binder<BeerRequestDTO> getBinder() {
        this.binder = new BeanValidationBinder<>(BeerRequestDTO.class);
        binder.bindInstanceFields(this);
        return binder;
    }

    public static abstract class BeerFormEvent extends ComponentEvent<BeerForm> {

        private BeerRequestDTO beer;

        protected BeerFormEvent(BeerForm source, BeerRequestDTO beer) {
            super(source, false);
            this.beer = beer;
        }

        public BeerRequestDTO getBeer() {
            return beer;
        }
    }

    public static class SaveEvent extends BeerFormEvent {

        SaveEvent(BeerForm source, BeerRequestDTO beer) {
            super(source, beer);
        }
    }

    public static class DeleteEvent extends BeerFormEvent {

        DeleteEvent(BeerForm source, BeerRequestDTO beer) {
            super(source, beer);
        }
    }

    public static class CloseEvent extends BeerFormEvent {

        CloseEvent(BeerForm source) {
            super(source, null);
        }
    }
}
