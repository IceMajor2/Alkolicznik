package com.demo.alkolicznik.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Component
public class FileUtils {

    private static ResourceLoader resourceLoader;

    @Autowired
    public void setResourceLoader(ResourceLoader resourceLoader) {
        FileUtils.resourceLoader = resourceLoader;
    }

    public static BufferedImage getBufferedImageFromWeb(String url) {
        try {
            return ImageIO.read(new URL(url));
        } catch (IOException e) {
            return null;
        }
    }

    public static BufferedImage getBufferedImageFromLocal(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            return null;
        }
    }

    public static boolean imageEquals(BufferedImage imgA, BufferedImage imgB) {
        if (!dimensionsSame(imgA, imgB)) return false;
        int width = imgA.getWidth();
        int height = imgA.getHeight();
        // Loop over every pixel.
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Compare the pixels for equality.
                if (imgA.getRGB(x, y) != imgB.getRGB(x, y)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean dimensionsSame(BufferedImage imgA, BufferedImage imgB) {
        return imgA.getWidth() == imgB.getWidth() && imgA.getHeight() == imgB.getHeight();
    }

    public static Properties readPropertiesFile(String path) {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(path));
            return properties;
        } catch (IOException e) {
            return null;
        }
    }

    public static String getRawPathToClassPathResource(String resource) {
        URI uri = null;
        try {
            uri = resourceLoader.getResource("classpath:" + resource).getURI();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Paths.get(uri).toAbsolutePath().toString();
    }

    public static String getRawPathToImage(String imageFilename) {
        URI uri = null;
        try {
            uri = resourceLoader.getResource("classpath:data_img/" + imageFilename).getURI();
        } catch (IOException e) {
            try {
                uri = resourceLoader.getResource("classpath:data_img").getURI();
                return Paths.get(uri).toAbsolutePath().toString() + '\\' + imageFilename;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        String rawPath = Paths.get(uri).toAbsolutePath().toString();
        return rawPath;
    }

    public static List<String> convertToFilenamesList(File[] files) {
        return Arrays.stream(files)
                .map(File::getName)
                .toList();
    }
}
