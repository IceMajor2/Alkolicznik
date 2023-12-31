package com.demo.alkolicznik.gui.image;

import com.demo.alkolicznik.dto.beer.BeerResponseDTO;
import com.demo.alkolicznik.dto.image.BeerImageResponseDTO;
import com.demo.alkolicznik.dto.image.ImageRequestDTO;
import com.demo.alkolicznik.dto.image.StoreImageResponseDTO;
import com.demo.alkolicznik.dto.store.StoreNameDTO;
import com.demo.alkolicznik.exceptions.ApiException;
import com.demo.alkolicznik.gui.templates.ImageTabTemplate;
import com.demo.alkolicznik.gui.utils.GuiUtils;
import com.demo.alkolicznik.utils.request.CookieUtils;
import com.demo.alkolicznik.utils.request.RequestUtils;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.server.VaadinRequest;
import jakarta.servlet.http.Cookie;
import org.springframework.http.HttpMethod;

import java.util.Map;

public class ImageUploadTab extends ImageTabTemplate {

    private Upload singleUpload;
    private Text info;
    private FileBuffer fileBuffer;

    public ImageUploadTab() {
        super();
        radioGroup.setLabel("Add image for:");
    }

    @Override
    protected void radioGroupListener(AbstractField.ComponentValueChangeEvent<RadioButtonGroup<String>, String> event) {
        String selection = radioGroup.getOptionalValue().orElse(null);
        resetIfValueChanged(selection);
        displayItemList(selection);
        if (isBeerBoxDisplayed()) {
            displayInfo("Proportions must be close to 3:7");
            displayUpload();
            waitForBeerImageRequest();
        } else if (isStoreBoxDisplayed()) {
            displayInfo("Proportions must not be bigger than 7:2");
            displayUpload();
            waitForStoreImageRequest();
        }
    }

    @Override
    protected void waitForStoreImageRequest() {
        storeBox.addValueChangeListener(storeSelect -> {
            StoreNameDTO store = storeSelect.getValue();
            singleUpload.addSucceededListener(upload -> {
                if (store != null) {
                    String path = fileBuffer.getFileData().getFile().getAbsolutePath();
                    Cookie authCookie = CookieUtils.getAuthCookie(VaadinRequest.getCurrent());
                    try {
                        RequestUtils.request(HttpMethod.POST, "/api/store/image", Map.of("name", store.getStoreName()),
                                new ImageRequestDTO(path), authCookie, StoreImageResponseDTO.class);
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
            singleUpload.addSucceededListener(upload -> {
                if (beer != null) {
                    String path = fileBuffer.getFileData().getFile().getAbsolutePath();
                    Cookie authCookie = CookieUtils.getAuthCookie(VaadinRequest.getCurrent());
                    try {
                        RequestUtils.request(HttpMethod.POST, "/api/beer/" + beer.getId() + "/image",
                                new ImageRequestDTO(path), authCookie, BeerImageResponseDTO.class);
                    } catch (ApiException e) {
                        GuiUtils.notify(e.getMessage());
                    }
                }
            });
        });
    }

    private void displayInfo(String info) {
        this.info = new Text(info);
        add(this.info);
    }

    private void displayUpload() {
        if (isUploadDisplayed()) return;
        this.fileBuffer = new FileBuffer();
        this.singleUpload = new Upload(fileBuffer);
        singleUpload.setDropAllowed(true);
        singleUpload.setAutoUpload(false);
        add(singleUpload);
    }

    @Override
    protected void resetIfValueChanged(String selection) {
        if (isBeerBoxDisplayed() && !"Beer".equals(selection)) {
            remove(info);
            remove(beerBox);
            remove(singleUpload);
        } else if (isStoreBoxDisplayed() && !"Store".equals(selection)) {
            remove(info);
            remove(storeBox);
            remove(singleUpload);
        }
    }

    private boolean isUploadDisplayed() {
        return singleUpload != null && singleUpload.isAttached();
    }
}
