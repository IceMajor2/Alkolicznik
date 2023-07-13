package com.demo.alkolicznik.gui.store;

import com.demo.alkolicznik.dto.requests.StoreRequestDTO;
import com.demo.alkolicznik.gui.Template;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

import java.util.List;

public class StoreForm extends FormLayout {

    private Binder<StoreRequestDTO> binder = new BeanValidationBinder<>(StoreRequestDTO.class);

    private TextField name = new TextField("Sklep");
    private TextField city = new TextField("Miasto");
    private TextField street = new TextField("Ulica");

    private Button create;
    private Button update;
    private Button delete;
    private Button close;

    public StoreForm() {
        binder.bindInstanceFields(this);

        add(
                name,
                city,
                street,
                createButtonLayout()
        );
    }

//    private void configureBinder() {
//        binder.bindInstanceFields(this);
//
//        binder.forField(name)
//                .withValidator(name -> name != null && !name.trim().isBlank(), "Name was not specified")
//                .bind(StoreRequestDTO::getName, StoreRequestDTO::setName);
//        binder.forField(city)
//                .withValidator(city -> city != null && !city.trim().isBlank(), "City was not specified")
//                .bind(StoreRequestDTO::getCity, StoreRequestDTO::setCity);
//        binder.forField(street)
//                .withValidator(street -> street != null && !street.trim().isBlank(), "Street was not specified")
//                .bind(StoreRequestDTO::getStreet, StoreRequestDTO::setStreet);
//    }

    private Component createButtonLayout() {
        List<Button> buttons = Template.getStyledButtons();
        create = buttons.get(0);
        update = buttons.get(1);
        delete = buttons.get(2);
        close = buttons.get(3);

        create.addClickListener(event -> fireEvent(new StoreForm.CreateEvent(this, binder.getBean())));
        update.addClickListener(event -> fireEvent(new StoreForm.UpdateEvent(this, binder.getBean())));
        delete.addClickListener(event -> fireEvent(new StoreForm.DeleteEvent(this, binder.getBean())));
        close.addClickListener(event -> fireEvent(new StoreForm.CloseEvent(this)));

        return new HorizontalLayout(create, update, delete, close);
    }

    public void setStore(StoreRequestDTO store) {
        binder.setBean(store);
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

    public static class UpdateEvent extends StoreForm.StoreEditFormEvent  {

        UpdateEvent(StoreForm source, StoreRequestDTO store) {
            super(source, store);
        }
    }

    public static class DeleteEvent extends StoreForm.StoreEditFormEvent  {

        DeleteEvent(StoreForm source, StoreRequestDTO store) {
            super(source, store);
        }
    }

    public static class CloseEvent extends StoreForm.StoreEditFormEvent  {

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
