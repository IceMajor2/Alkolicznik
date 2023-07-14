package com.demo.alkolicznik.gui.store;

import com.demo.alkolicznik.dto.requests.StoreRequestDTO;
import com.demo.alkolicznik.gui.templates.FormTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;

public class StoreForm extends FormTemplate<StoreRequestDTO> {

    private TextField name = new TextField("Sklep");
    private TextField city = new TextField("Miasto");
    private TextField street = new TextField("Ulica");

    public StoreForm() {
        super(StoreRequestDTO.class);
        super.binder.bindInstanceFields(this);

        add(
                name,
                city,
                street,
                this.createButtonLayout()
        );
    }

    @Override
    protected Component createButtonLayout() {
        create.addClickListener(event -> fireEvent(new StoreForm.CreateEvent(this, binder.getBean())));
        update.addClickListener(event -> fireEvent(new StoreForm.UpdateEvent(this, binder.getBean())));
        delete.addClickListener(event -> fireEvent(new StoreForm.DeleteEvent(this, binder.getBean())));
        close.addClickListener(event -> fireEvent(new StoreForm.CloseEvent(this)));
        return super.createButtonLayout();
    }

    public static abstract class StoreEditFormEvent extends ComponentEvent<StoreForm> {

        private StoreRequestDTO store;

        protected StoreEditFormEvent(StoreForm source, StoreRequestDTO store) {
            super(source, false);
            this.store = store;
        }

        public StoreRequestDTO getStore() {
            return store;
        }
    }

    public static class CreateEvent extends StoreForm.StoreEditFormEvent {

        CreateEvent(StoreForm source, StoreRequestDTO store) {
            super(source, store);
        }
    }

    public static class UpdateEvent extends StoreForm.StoreEditFormEvent {

        UpdateEvent(StoreForm source, StoreRequestDTO store) {
            super(source, store);
        }
    }

    public static class DeleteEvent extends StoreForm.StoreEditFormEvent {

        DeleteEvent(StoreForm source, StoreRequestDTO store) {
            super(source, store);
        }
    }

    public static class CloseEvent extends StoreForm.StoreEditFormEvent {

        CloseEvent(StoreForm source) {
            super(source, null);
        }
    }

    public Registration addCreateListener(ComponentEventListener<StoreForm.CreateEvent> listener) {
        return addListener(StoreForm.CreateEvent.class, listener);
    }

    public Registration addDeleteListener(ComponentEventListener<StoreForm.DeleteEvent> listener) {
        return addListener(StoreForm.DeleteEvent.class, listener);
    }

    public Registration addUpdateListener(ComponentEventListener<StoreForm.UpdateEvent> listener) {
        return addListener(StoreForm.UpdateEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<StoreForm.CloseEvent> listener) {
        return addListener(StoreForm.CloseEvent.class, listener);
    }
}
