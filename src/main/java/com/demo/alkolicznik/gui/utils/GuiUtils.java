package com.demo.alkolicznik.gui.utils;

import com.demo.alkolicznik.dto.beer.BeerResponseDTO;
import com.demo.alkolicznik.dto.image.BeerImageResponseDTO;
import com.demo.alkolicznik.dto.image.StoreImageResponseDTO;
import com.demo.alkolicznik.dto.store.StoreResponseDTO;
import com.demo.alkolicznik.exceptions.ApiException;
import com.demo.alkolicznik.utils.Utils;
import com.demo.alkolicznik.utils.request.RequestUtils;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import org.springframework.http.HttpMethod;

import java.awt.image.BufferedImage;

public class GuiUtils {

    public static void notify(String message) {
        Notification.show(message, 4000, Notification.Position.BOTTOM_END);
    }

    public static Image getVaadinImage(StoreResponseDTO store) {
        try {
            var response = RequestUtils.request(HttpMethod.GET, "/api/store/" +
                    store.getId() + "/image", StoreImageResponseDTO.class);
            return new Image(response.getUrl(), "Store image");
        } catch (ApiException e) {
            return null;
        }
    }

    public static Image getVaadinImage(BeerResponseDTO beer) {
        try {
            var response = RequestUtils.request(HttpMethod.GET, "/api/beer/" +
                    beer.getId() + "/image", BeerImageResponseDTO.class);
            return new Image(response.getUrl(), "Beer image");
        } catch (ApiException e) {
            return null;
        }
    }

    public static int[] dimensionsForStoreImage(String imageSrcUrl) {
        BufferedImage image = Utils.getBufferedImageFromWeb(imageSrcUrl);
        int height = image.getHeight();
        int width = image.getWidth();
        if (height <= 80) return new int[]{height, width};

        double resizeBy = height / 80.0;

        height = (int) (height / resizeBy);
        width = (int) (width / resizeBy);
        return new int[]{height, width};
    }
}
