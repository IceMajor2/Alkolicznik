package com.demo.alkolicznik.gui.image;

import com.demo.alkolicznik.dto.beer.BeerResponseDTO;
import com.demo.alkolicznik.dto.image.ImageRequestDTO;
import com.demo.alkolicznik.dto.store.StoreResponseDTO;
import com.demo.alkolicznik.gui.MainLayout;
import com.demo.alkolicznik.utils.request.CookieUtils;
import com.demo.alkolicznik.utils.request.RequestUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinRequest;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.Cookie;
import org.springframework.http.HttpMethod;

import java.util.List;

@Route(value = "image", layout = MainLayout.class)
@PageTitle("Upload image | Alkolicznik")
@RolesAllowed({"ADMIN", "ACCOUNTANT"})
public class ImageView extends VerticalLayout {

    private RadioButtonGroup<String> radioGroup;
    private Upload singleUpload;
    private FileBuffer fileBuffer;
    private ComboBox<BeerResponseDTO> beerBox;
    private ComboBox<StoreResponseDTO> storeBox;

    public ImageView() {
        this.radioGroup = getRadioButtonGroup();
        add(radioGroup);

        radioGroup.addValueChangeListener(event -> {
            String selection = radioGroup.getOptionalValue().orElse(null);
            displayItemList(selection);
            if (isBeerBoxDisplayed()) {
                displayUpload();
                waitForBeerImage();
            } else if (isStoreBoxDisplayed()) {
                displayUpload();
                // waitForStoreImage();
            }
            resetIfValueChanged(selection);
        });
    }

    private void waitForBeerImage() {
        beerBox.addValueChangeListener(beerSelect -> {
            BeerResponseDTO beer = beerSelect.getValue();
            singleUpload.addSucceededListener(upload -> {
                if (beer != null) {
                    String path = fileBuffer.getFileData().getFile().getAbsolutePath();
                    Cookie authCookie = CookieUtils.getAuthCookie(VaadinRequest.getCurrent());
                    RequestUtils.request(HttpMethod.POST, "/api/beer/" + beer.getId() + "/image",
                            new ImageRequestDTO(path), authCookie, BeerResponseDTO.class);
                }
            });
        });
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

            List<BeerResponseDTO> beers = RequestUtils.request(HttpMethod.GET, "/api/beer", authCookie,
                    new TypeReference<>() {
                    });
            beerBox.setItems(beers);
            add(beerBox);
        } else if ("Store".equals(selection)) {
            // TODO: new endpoint that provides a unique list of store names
//            this.storeBox = new ComboBox<>(selection);
//            List<StoreResponseDTO> stores = RequestUtils.request(HttpMethod.GET, "/api/store", authCookie,
//                    new TypeReference<>() {});
//            storeBox.setItems(stores);
//            storeBox.setAllowCustomValue(false);
//            storeBox.setItemLabelGenerator(store -> String.format("[%d] %s"));
        }
    }

    private void resetIfValueChanged(String selection) {
        boolean uploadToRemove = false;
        if (beerBox != null && beerBox.isAttached() && !"Beer".equals(selection)) {
            remove(beerBox);
            if (isUploadDisplayed()) uploadToRemove = true;
        } else if (storeBox != null && storeBox.isAttached() && !"Store".equals(selection)) {
            remove(storeBox);
            if (isUploadDisplayed()) uploadToRemove = true;
        }
        if (uploadToRemove) {
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
}
