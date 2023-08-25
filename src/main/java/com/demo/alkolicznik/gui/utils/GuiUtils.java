package com.demo.alkolicznik.gui.utils;

import com.vaadin.flow.component.notification.Notification;

public class GuiUtils {

	public static void showError(String message) {
		Notification.show(message, 4000, Notification.Position.BOTTOM_END);
	}
}
