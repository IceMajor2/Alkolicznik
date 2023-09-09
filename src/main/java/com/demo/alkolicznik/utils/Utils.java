package com.demo.alkolicznik.utils;

import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Utils {

    public static String createStoreFilename(String storeName, String fullPath) {
        String polishedStoreName = replaceWhitespaces(storeName).toLowerCase();
        return appendExtensionIfExists(polishedStoreName, fullPath);
    }

    public static String createBeerFilename(String beerName, Double volume, String fullPath) {
        String polishedBeerName = replaceWhitespaces(beerName).toLowerCase() + '-' + volume;
        return appendExtensionIfExists(polishedBeerName, fullPath);
    }

    private static String appendExtensionIfExists(String filename, String fullPath) {
        String extension = FilenameUtils.getExtension(FilenameUtils.getName(fullPath));
        return extension.isEmpty() ? filename : filename + '.' + extension;
    }

    private static String replaceWhitespaces(String s) {
        return s.replace(' ', '-');
    }

    public static BufferedImage getBufferedImageFromWeb(String url) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new URL(url));
            return image;
        } catch (IOException e) {
            return null;
        }
    }

    public static BufferedImage getBufferedImageFromLocal(String path) {
        try {
            BufferedImage image = ImageIO.read(new File(path));
            return image;
        } catch (IOException e) {
            return null;
        }
    }
}
