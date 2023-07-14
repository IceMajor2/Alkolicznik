package com.demo.alkolicznik.gui.beer_price;

import com.demo.alkolicznik.dto.requests.BeerPriceParamDTO;
import com.demo.alkolicznik.gui.templates.FormTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.shared.Registration;

public class BeerPriceForm extends FormTemplate<BeerPriceParamDTO> {

    private NumberField storeId = new NumberField("Id sklepu");
    private NumberField beerId = new NumberField("Id piwa");
    private NumberField price = new NumberField("Cena");

    protected BeerPriceForm() {
        super(BeerPriceParamDTO.class);
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
        binder.bind(storeId, BeerPriceParamDTO::getStoreId, BeerPriceParamDTO::setStoreId);
        binder.bind(beerId, BeerPriceParamDTO::getBeerId, BeerPriceParamDTO::setBeerId);
        binder.bind(price, BeerPriceParamDTO::getPrice, BeerPriceParamDTO::setPrice);
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

        private BeerPriceParamDTO price;

        protected BeerPriceEditFormEvent(BeerPriceForm source, BeerPriceParamDTO price) {
            super(source, false);
            this.price = price;
        }

        public BeerPriceParamDTO getPrice() {
            return price;
        }
    }

    public static class CreateEvent extends BeerPriceEditFormEvent {

        CreateEvent(BeerPriceForm source, BeerPriceParamDTO price) {
            super(source, price);
        }
    }

    public static class UpdateEvent extends BeerPriceEditFormEvent {

        UpdateEvent(BeerPriceForm source, BeerPriceParamDTO price) {
            super(source, price);
        }
    }

    public static class DeleteEvent extends BeerPriceEditFormEvent {

        DeleteEvent(BeerPriceForm source, BeerPriceParamDTO price) {
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
