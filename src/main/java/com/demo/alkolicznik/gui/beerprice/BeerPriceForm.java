package com.demo.alkolicznik.gui.beerprice;

import com.demo.alkolicznik.dto.beerprice.BeerPriceParamRequestDTO;
import com.demo.alkolicznik.gui.templates.FormTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.shared.Registration;

public class BeerPriceForm extends FormTemplate<BeerPriceParamRequestDTO> {

    private NumberField storeId = new NumberField("Id sklepu");
    private NumberField beerId = new NumberField("Id piwa");
    private NumberField price = new NumberField("Cena");

    protected BeerPriceForm() {
        super(BeerPriceParamRequestDTO.class);
        configureBinder();

        add(
                storeId,
                beerId,
                price,
                this.createButtonLayout()
        );
    }

    private void configureBinder() {
        binder.bindInstanceFields(this);
        binder.bind(storeId, BeerPriceParamRequestDTO::getStoreId, BeerPriceParamRequestDTO::setStoreId);
        binder.bind(beerId, BeerPriceParamRequestDTO::getBeerId, BeerPriceParamRequestDTO::setBeerId);
        binder.bind(price, BeerPriceParamRequestDTO::getPrice, BeerPriceParamRequestDTO::setPrice);
    }

    @Override
    protected Component createButtonLayout() {
        create.addClickListener(event -> fireEvent(new CreateEvent(this, binder.getBean())));
        update.addClickListener(event -> fireEvent(new UpdateEvent(this, binder.getBean())));
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, binder.getBean())));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));
        return super.createButtonLayout();
    }

    public static abstract class BeerPriceEditFormEvent extends ComponentEvent<BeerPriceForm> {

        private BeerPriceParamRequestDTO price;

        protected BeerPriceEditFormEvent(BeerPriceForm source, BeerPriceParamRequestDTO price) {
            super(source, false);
            this.price = price;
        }

        public BeerPriceParamRequestDTO getPrice() {
            return price;
        }
    }

    public static class CreateEvent extends BeerPriceEditFormEvent {

        CreateEvent(BeerPriceForm source, BeerPriceParamRequestDTO price) {
            super(source, price);
        }
    }

    public static class UpdateEvent extends BeerPriceEditFormEvent {

        UpdateEvent(BeerPriceForm source, BeerPriceParamRequestDTO price) {
            super(source, price);
        }
    }

    public static class DeleteEvent extends BeerPriceEditFormEvent {

        DeleteEvent(BeerPriceForm source, BeerPriceParamRequestDTO price) {
            super(source, price);
        }
    }

    public static class CloseEvent extends BeerPriceEditFormEvent {

        CloseEvent(BeerPriceForm source) {
            super(source, null);
        }
    }

    public Registration addCreateListener(ComponentEventListener<CreateEvent> listener) {
        return addListener(BeerPriceForm.CreateEvent.class, listener);
    }

    public Registration addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
        return addListener(BeerPriceForm.DeleteEvent.class, listener);
    }

    public Registration addUpdateListener(ComponentEventListener<UpdateEvent> listener) {
        return addListener(BeerPriceForm.UpdateEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(BeerPriceForm.CloseEvent.class, listener);
    }
}
