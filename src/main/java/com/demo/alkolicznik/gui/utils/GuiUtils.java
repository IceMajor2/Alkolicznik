package com.demo.alkolicznik.gui.utils;

import com.demo.alkolicznik.dto.beer.BeerResponseDTO;
import com.demo.alkolicznik.dto.image.ImageResponseDTO;
import com.demo.alkolicznik.dto.store.StoreResponseDTO;
import com.demo.alkolicznik.exceptions.ApiException;
import com.demo.alkolicznik.utils.request.RequestUtils;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import org.springframework.http.HttpMethod;

public class GuiUtils {

    public static void showError(String message) {
        Notification.show(message, 4000, Notification.Position.BOTTOM_END);
    }

    public static Image getVaadinImage(StoreResponseDTO store) {
        try {
            var response = RequestUtils.request(HttpMethod.GET, "/api/store/" +
                    store.getId() + "/image", ImageResponseDTO.class);
            return new Image(response.getImageUrl(), "Store image");
        } catch (ApiException e) {
            return null;
        }
    }

    public static Image getVaadinImage(BeerResponseDTO beer) {
        try {
            var response = RequestUtils.request(HttpMethod.GET, "/api/beer/" +
                    beer.getId() + "/image", ImageResponseDTO.class);
            return new Image(response.getImageUrl(), "Beer image");
        } catch (ApiException e) {
            return null;
        }
    }
}
