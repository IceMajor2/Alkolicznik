package com.demo.alkolicznik.utils;

import com.demo.alkolicznik.models.image.BeerImage;
import com.demo.alkolicznik.models.image.StoreImage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;

public class Utils {

    public static Long getBeerIdFromFilename(String filename) {
        StringBuilder sb = new StringBuilder("");
        String trimmed = filename.trim();
        for (char ch : trimmed.toCharArray()) {
            if (Character.isDigit(ch))
                sb.append((ch));
            else break;
        }
        return !sb.isEmpty() ? Long.valueOf(sb.toString()) : null;
    }

    public static String getRawStoreNameFromFilename(String filename) {
        String noExtension = FilenameUtils.removeExtension(filename);
        return reverseWhitespaces(noExtension);
    }

    public static String createStoreFilename(String storeName, String extension) {
        String polishedStoreName = replaceWhitespaces(storeName).toLowerCase();
        return extension.isBlank() ? polishedStoreName : polishedStoreName + '.' + extension;
    }

    public static String createBeerFilename(String fullname, Double volume, String extension) {
        StringBuilder sb = new StringBuilder(replaceWhitespaces(fullname).toLowerCase())
                .append('-')
                .append(volume);
        if (extension != null && !extension.isEmpty()) sb.append('.').append(extension);
        return sb.toString();
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

    public static File createTempFile(String filename, InputStream content) throws IOException {
        String filenameNoExtension = FilenameUtils.getExtension(filename);
        String extension = FilenameUtils.getExtension(filename);
        File tempFile = File.createTempFile(filenameNoExtension, extension);
        FileUtils.copyInputStreamToFile(content, tempFile);
        return tempFile;
    }

    public static Properties getProperties(String classpathFile) throws IOException {
        ResourcePatternResolver resourcePatResolver = new PathMatchingResourcePatternResolver();
        Resource file = resourcePatResolver.getResource("classpath:" + classpathFile);

        InputStream inputStream = file.getInputStream();
        Properties properties = new Properties();
        properties.load(inputStream);
        return properties;
    }

    public static boolean isStoreImage(Class<?> clazz) {
        return Objects.equals(clazz, StoreImage.class);
    }

    public static boolean isBeerImage(Class<?> clazz) {
        return Objects.equals(clazz, BeerImage.class);
    }

    private static String replaceWhitespaces(String s) {
        return s.replace(' ', '-');
    }

    private static String reverseWhitespaces(String s) {
        return s.replace('-', ' ');
    }
}
