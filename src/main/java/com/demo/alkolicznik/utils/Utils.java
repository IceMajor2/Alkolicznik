package com.demo.alkolicznik.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Utils {

	public static String getExtensionFromPath(String path) {
		return path.substring(path.lastIndexOf('.') + 1);
	}

	public static BufferedImage getImage(File file) {
		try {
			return ImageIO.read(file);
		} catch(IOException e) {
			return null;
		}
	}
}
