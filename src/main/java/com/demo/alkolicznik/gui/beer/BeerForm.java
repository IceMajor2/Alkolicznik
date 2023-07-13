package com.demo.alkolicznik.gui.beer;

import com.demo.alkolicznik.dto.requests.BeerRequestDTO;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

public class BeerForm extends FormLayout {

    private Binder<BeerRequestDTO> binder = new BeanValidationBinder<>(BeerRequestDTO.class);

    private TextField brand = new TextField("Marka");
    private TextField type = new TextField("Typ");
    private NumberField volume = new NumberField("Objętość");

    private Button create = new Button("Dodaj");
    private Button update = new Button("Aktualizuj");
    private Button delete = new Button("Usuń");
    private Button close = new Button("Zamknij");

    public BeerForm() {
        binder.bindInstanceFields(this);

        add(
                brand,
                type,
                volume,
                createButtonLayout()
        );
    }

//    private void configureBinder() {
//        binder.bindInstanceFields(this);
//
//        binder.forField(brand)
//                .withValidator(brand -> brand != null && !brand.trim().isBlank(), "Brand is null")
//                .bind(BeerRequestDTO::getBrand, BeerRequestDTO::setBrand);
//        binder.forField(type)
//                .withValidator(type -> type == null || !type.trim().isBlank(), "Type is blank")
//                .bind(BeerRequestDTO::getType, BeerRequestDTO::setType);
//        binder.forField(volume)
//                .withValidator(volume -> volume > 0d, "Volume must be a positive number")
//                .bind(BeerRequestDTO::getVolume, BeerRequestDTO::setVolume);
//    }

    private Component createButtonLayout() {
        create.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        update.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        create.addClickListener(event -> fireEvent(new CreateEvent(this, binder.getBean())));
        update.addClickListener(event -> fireEvent(new UpdateEvent(this, binder.getBean())));
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, binder.getBean())));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        close.addClickShortcut(Key.ESCAPE);
        return new HorizontalLayout(create, update, delete, close);
    }

    public void setBeer(BeerRequestDTO beer) {
        binder.setBean(beer);
    }

    public static abstract class BeerEditFormEvent extends ComponentEvent<BeerForm> {

        private BeerRequestDTO beer;

        protected BeerEditFormEvent(BeerForm source, BeerRequestDTO beer) {
            super(source, false);
            this.beer = beer;
        }

        public BeerRequestDTO getBeer() {
            return beer;
        }
    }

    public static class CreateEvent extends BeerEditFormEvent {

        CreateEvent(BeerForm source, BeerRequestDTO beer) {
            super(source, beer);
        }
    }

    public static class UpdateEvent extends BeerEditFormEvent {

        UpdateEvent(BeerForm source, BeerRequestDTO beer) {
            super(source, beer);
        }
    }

    public static class DeleteEvent extends BeerEditFormEvent {

        DeleteEvent(BeerForm source, BeerRequestDTO beer) {
            super(source, beer);
        }
    }

    public static class CloseEvent extends BeerEditFormEvent {

        CloseEvent(BeerForm source) {
            super(source, null);
        }
    }

    public Registration addCreateListener(ComponentEventListener<CreateEvent> listener) {
        return addListener(CreateEvent.class, listener);
    }

    public Registration addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
        return addListener(DeleteEvent.class, listener);
    }

    public Registration addUpdateListener(ComponentEventListener<UpdateEvent> listener) {
        return addListener(UpdateEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }
}
