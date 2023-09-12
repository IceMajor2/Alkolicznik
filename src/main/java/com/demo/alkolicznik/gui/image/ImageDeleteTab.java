package com.demo.alkolicznik.gui.image;

import com.demo.alkolicznik.dto.beer.BeerResponseDTO;
import com.demo.alkolicznik.dto.store.StoreNameDTO;
import com.demo.alkolicznik.exceptions.ApiException;
import com.demo.alkolicznik.gui.templates.ImageTabTemplate;
import com.demo.alkolicznik.gui.utils.GuiUtils;
import com.demo.alkolicznik.utils.request.CookieUtils;
import com.demo.alkolicznik.utils.request.RequestUtils;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.server.VaadinRequest;
import jakarta.servlet.http.Cookie;
import org.springframework.http.HttpMethod;

import java.util.Map;

public class ImageDeleteTab extends ImageTabTemplate {

    private Button delete;

    public ImageDeleteTab() {
        super();
        radioGroup.setLabel("Delete image of:");
    }

    @Override
    protected void radioGroupListener(AbstractField.ComponentValueChangeEvent<RadioButtonGroup<String>, String> event) {
        String selection = radioGroup.getOptionalValue().orElse(null);
        resetIfValueChanged(selection);
        displayItemList(selection);
        if (isBeerBoxDisplayed()) {
            this.delete = displayConfirmButton();
            waitForBeerImageRequest();
        } else if (isStoreBoxDisplayed()) {
            this.delete = displayConfirmButton();
            waitForStoreImageRequest();
        }
    }

    @Override
    protected void resetIfValueChanged(String selection) {
        if (isBeerBoxDisplayed() && !"Beer".equals(selection)) {
            remove(beerBox);
            remove(delete);
        } else if (isStoreBoxDisplayed() && !"Store".equals(selection)) {
            remove(storeBox);
            remove(delete);
        }
    }

    @Override
    protected void waitForStoreImageRequest() {
        storeBox.addValueChangeListener(storeSelect -> {
            StoreNameDTO store = storeSelect.getValue();
            delete.addClickListener(deleteRequest -> {
                if (store != null) {
                    Cookie authCookie = CookieUtils.getAuthCookie(VaadinRequest.getCurrent());
                    try {
                        RequestUtils.request(HttpMethod.DELETE, "/api/store/image",
                                Map.of("name", store.getStoreName()), authCookie, String.class);
                    } catch (ApiException e) {
                        GuiUtils.notify(e.getMessage());
                    }
                }
            });
        });
    }

    @Override
    protected void waitForBeerImageRequest() {
        beerBox.addValueChangeListener(beerSelect -> {
            BeerResponseDTO beer = beerSelect.getValue();
            delete.addClickListener(deleteRequest -> {
                if (beer != null) {
                    Cookie authCookie = CookieUtils.getAuthCookie(VaadinRequest.getCurrent());
                    try {
                        RequestUtils.request(HttpMethod.DELETE, "/api/beer/" + beer.getId() + "/image",
                                authCookie, String.class);
                    } catch (ApiException e) {
                        GuiUtils.notify(e.getMessage());
                    }
                }
            });
        });
    }

    private Button displayConfirmButton() {
        Button delete = new Button("Delete");
        delete.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        delete.addClickShortcut(Key.ENTER);
        add(delete);
        return delete;
    }
}
