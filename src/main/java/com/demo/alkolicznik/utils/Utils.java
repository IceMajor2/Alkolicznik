package com.demo.alkolicznik.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Utils {

    public static String getExtensionFromPath(String path) {
        return path.substring(path.lastIndexOf('.') + 1);
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
