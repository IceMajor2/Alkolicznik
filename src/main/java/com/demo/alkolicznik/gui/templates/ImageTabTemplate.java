package com.demo.alkolicznik.gui.templates;

import com.demo.alkolicznik.dto.beer.BeerResponseDTO;
import com.demo.alkolicznik.dto.store.StoreNameDTO;
import com.demo.alkolicznik.utils.request.CookieUtils;
import com.demo.alkolicznik.utils.request.RequestUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.server.VaadinRequest;
import jakarta.servlet.http.Cookie;
import org.springframework.http.HttpMethod;

import java.util.List;

public abstract class ImageTabTemplate extends VerticalLayout {

    protected RadioButtonGroup<String> radioGroup;
    protected ComboBox<BeerResponseDTO> beerBox;
    protected ComboBox<StoreNameDTO> storeBox;

    public ImageTabTemplate() {
        this.radioGroup = getRadioButtonGroup();
        add(radioGroup);

        radioGroup.addValueChangeListener(this::radioGroupListener);
    }

    protected abstract void radioGroupListener(AbstractField.ComponentValueChangeEvent<RadioButtonGroup<String>, String> event);

    protected abstract void resetIfValueChanged(String selection);

    protected abstract void waitForStoreImageRequest();

    protected abstract void waitForBeerImageRequest();


    protected void displayItemList(String selection) {
        Cookie authCookie = CookieUtils.getAuthCookie(VaadinRequest.getCurrent());
        if ("Beer".equals(selection)) {
            this.beerBox = getBeerBox();
            List<BeerResponseDTO> beers = RequestUtils.request(HttpMethod.GET, "/api/beer",
                    authCookie, new TypeReference<>() {
                    });
            beerBox.setItems(beers);
            add(beerBox);
        } else if ("Store".equals(selection)) {
            this.storeBox = getStoreBox();
            List<StoreNameDTO> stores = RequestUtils.request(HttpMethod.GET, "/api/store?brand_only",
                    authCookie, new TypeReference<>() {
                    });
            storeBox.setItems(stores);
            add(storeBox);
        }
        ;
    }

    protected RadioButtonGroup<String> getRadioButtonGroup() {
        RadioButtonGroup<String> radioGroup = new RadioButtonGroup<>();
        radioGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        radioGroup.setItems("Beer", "Store");
        return radioGroup;
    }

    protected ComboBox<BeerResponseDTO> getBeerBox() {
        ComboBox<BeerResponseDTO> beerBox = new ComboBox<>("Beer");
        beerBox.setAllowCustomValue(false);
        beerBox.setItemLabelGenerator(beer -> String.format("[%d] %s %.2f l", // [1] Example Beer 0,50 l
                beer.getId(), beer.getFullName(), beer.getVolume()));
        beerBox.setWidth("20em");
        return beerBox;
    }

    protected ComboBox<StoreNameDTO> getStoreBox() {
        ComboBox<StoreNameDTO> storeBox = new ComboBox<>("Store");
        storeBox.setAllowCustomValue(false);
        storeBox.setItemLabelGenerator(StoreNameDTO::getStoreName);
        storeBox.setWidth("20em");
        return storeBox;
    }

    protected boolean isBeerBoxDisplayed() {
        return beerBox != null && beerBox.isAttached();
    }

    protected boolean isStoreBoxDisplayed() {
        return storeBox != null && storeBox.isAttached();
    }
}
