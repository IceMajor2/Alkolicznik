package com.demo.alkolicznik.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import jakarta.servlet.http.Cookie;

public class Utils {

	public static Cookie createTokenCookie(String token) {
		Cookie cookie = new Cookie("token", token);
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setDomain("");
		return cookie;
	}

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
