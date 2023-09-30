package com.demo.alkolicznik.gui.main;

import com.demo.alkolicznik.utils.Utils;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.StreamResource;
import org.springframework.core.io.Resource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Logo extends Image {

    private static final String DEFAULT_RESOURCE = "META-INF%sresources%slogo.png"
            .formatted(File.separatorChar, File.separatorChar);

    public Logo() {
        this(DEFAULT_RESOURCE);
    }

    public Logo(String filename) {
        super(new StreamResource("logo_str", () -> {
            try {
                return Utils.getSpringResource(filename).getInputStream();
            } catch (IOException e) {
                return null;
            }
        }), "Logo");
        configure(filename);
    }

    private void configure(String filename) {
        Resource resource = Utils.getSpringResource(filename);
        try {
            InputStream inputStream = resource.getInputStream();
            int[] displayDimensions = getLogoDimensions(ImageIO.read(inputStream));
            setHeight(displayDimensions[0], Unit.PIXELS);
            setWidth(displayDimensions[1], Unit.PIXELS);
            getStyle().setPadding("0px 0px 45px 0px");
        } catch (IOException e) {}
    }

    private int[] getLogoDimensions(BufferedImage bufferedImage) {
        int orgHeight = bufferedImage.getHeight();
        int orgWidth = bufferedImage.getWidth();
        int newHeight = orgHeight / 7;
        int newWidth = orgWidth / 7;
        return new int[]{newHeight, newWidth};
    }
}
