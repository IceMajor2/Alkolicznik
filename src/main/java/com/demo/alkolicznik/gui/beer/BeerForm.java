package com.demo.alkolicznik.gui.beer;

import com.demo.alkolicznik.dto.beer.BeerRequestDTO;
import com.demo.alkolicznik.gui.templates.FormTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;

public class BeerForm extends FormTemplate<BeerRequestDTO> {

    private TextField brand = new TextField("Brand");
    private TextField type = new TextField("Type");
    private NumberField volume = new NumberField("Volume");

    public BeerForm() {
        super(BeerRequestDTO.class);
        super.binder.bindInstanceFields(this);

        add(
                brand,
                type,
                volume,
                this.createButtonLayout()
        );
    }

    @Override
    protected Component createButtonLayout() {
        create.addClickListener(event -> fireEvent(new CreateEvent(this, binder.getBean())));
        update.addClickListener(event -> fireEvent(new UpdateEvent(this, binder.getBean())));
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, binder.getBean())));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));
        return super.createButtonLayout();
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
