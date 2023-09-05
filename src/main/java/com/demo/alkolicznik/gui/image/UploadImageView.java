package com.demo.alkolicznik.gui.image;

import com.demo.alkolicznik.dto.beer.BeerResponseDTO;
import com.demo.alkolicznik.dto.image.ImageRequestDTO;
import com.demo.alkolicznik.dto.image.ImageResponseDTO;
import com.demo.alkolicznik.dto.store.StoreNameDTO;
import com.demo.alkolicznik.exceptions.ApiException;
import com.demo.alkolicznik.gui.utils.GuiUtils;
import com.demo.alkolicznik.utils.request.CookieUtils;
import com.demo.alkolicznik.utils.request.RequestUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.server.VaadinRequest;
import jakarta.servlet.http.Cookie;
import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.Map;

public class UploadImageView extends VerticalLayout {

    private RadioButtonGroup<String> radioGroup;
    private Upload singleUpload;
    private FileBuffer fileBuffer;
    private ComboBox<BeerResponseDTO> beerBox;
    private ComboBox<StoreNameDTO> storeBox;

    public UploadImageView() {
        this.radioGroup = getRadioButtonGroup();
        add(radioGroup);

        radioGroup.addValueChangeListener(event -> {
            String selection = radioGroup.getOptionalValue().orElse(null);
            resetIfValueChanged(selection);
            displayItemList(selection);
            if (isBeerBoxDisplayed()) {
                displayUpload();
                waitForBeerImage();
            } else if (isStoreBoxDisplayed()) {
                displayUpload();
                waitForStoreImage();
            }
        });
    }

    private void waitForStoreImage() {
        storeBox.addValueChangeListener(storeSelect -> {
            StoreNameDTO store = storeSelect.getValue();
            singleUpload.addSucceededListener(upload -> {
                if (store != null) {
                    String path = fileBuffer.getFileData().getFile().getAbsolutePath();
                    Cookie authCookie = CookieUtils.getAuthCookie(VaadinRequest.getCurrent());
                    try {
                        RequestUtils.request(HttpMethod.POST, "/api/store/image", Map.of("name", store.getStoreName()),
                                new ImageRequestDTO(path), authCookie, ImageResponseDTO.class);
                    } catch (ApiException e) {
                        GuiUtils.showError(e.getMessage());
                    }
                }
            });
        });
    }

    private void waitForBeerImage() {
        beerBox.addValueChangeListener(beerSelect -> {
            BeerResponseDTO beer = beerSelect.getValue();
            singleUpload.addSucceededListener(upload -> {
                if (beer != null) {
                    String path = fileBuffer.getFileData().getFile().getAbsolutePath();
                    Cookie authCookie = CookieUtils.getAuthCookie(VaadinRequest.getCurrent());
                    try {
                        RequestUtils.request(HttpMethod.POST, "/api/beer/" + beer.getId() + "/image",
                                new ImageRequestDTO(path), authCookie, BeerResponseDTO.class);
                    } catch (ApiException e) {
                        GuiUtils.showError(e.getMessage());
                    }
                }
            });
        });
    }

    private void displayUpload() {
        if (isUploadDisplayed()) return;
        this.fileBuffer = new FileBuffer();
        this.singleUpload = new Upload(fileBuffer);
        singleUpload.setDropAllowed(true);
        singleUpload.setAutoUpload(false);
        add(singleUpload);
    }

    private void displayItemList(String selection) {
        Cookie authCookie = CookieUtils.getAuthCookie(VaadinRequest.getCurrent());
        if ("Beer".equals(selection)) {
            this.beerBox = new ComboBox<>(selection);
            beerBox.setAllowCustomValue(false);
            beerBox.setItemLabelGenerator(beer -> String.format("[%d] %s %.2f l", // [1] Example Beer 0,50 l
                    beer.getId(), beer.getFullName(), beer.getVolume()));
            beerBox.setWidth("20em");

            List<BeerResponseDTO> beers = RequestUtils.request(HttpMethod.GET, "/api/beer",
                    authCookie, new TypeReference<>() {
                    });
            beerBox.setItems(beers);
            add(beerBox);
        } else if ("Store".equals(selection)) {
            this.storeBox = new ComboBox<>(selection);
            storeBox.setAllowCustomValue(false);
            storeBox.setItemLabelGenerator(StoreNameDTO::getStoreName);
            storeBox.setWidth("20em");

            List<StoreNameDTO> stores = RequestUtils.request(HttpMethod.GET, "/api/store?brand_only",
                    authCookie, new TypeReference<>() {
                    });
            storeBox.setItems(stores);
            add(storeBox);
        }
    }

    private void resetIfValueChanged(String selection) {
        if (beerBox != null && beerBox.isAttached() && !"Beer".equals(selection)) {
            remove(beerBox);
            remove(singleUpload);
        } else if (storeBox != null && storeBox.isAttached() && !"Store".equals(selection)) {
            remove(storeBox);
            remove(singleUpload);
        }
    }

    private RadioButtonGroup<String> getRadioButtonGroup() {
        RadioButtonGroup<String> radioGroup = new RadioButtonGroup<>();
        radioGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        radioGroup.setLabel("Add image for:");
        radioGroup.setItems("Beer", "Store");
        return radioGroup;
    }

    private boolean isUploadDisplayed() {
        return singleUpload != null && singleUpload.isAttached();
    }

    private boolean isBeerBoxDisplayed() {
        return beerBox != null && beerBox.isAttached();
    }

    private boolean isStoreBoxDisplayed() {
        return storeBox != null && storeBox.isAttached();
    }
}
