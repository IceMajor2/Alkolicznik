package com.demo.alkolicznik.gui.image;

import com.demo.alkolicznik.gui.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "image", layout = MainLayout.class)
@PageTitle("Upload image | Alkolicznik")
@RolesAllowed({"ADMIN", "ACCOUNTANT"})
public class ImageView extends VerticalLayout {

    private TabSheet tabSheet;
    private ImageUploadTab uploadTab;
    private ImageDeleteTab deleteTab;

    public ImageView() {
        this.uploadTab = new ImageUploadTab();
        this.deleteTab = new ImageDeleteTab();
        this.tabSheet = new TabSheet();
        tabSheet.add("Upload", uploadTab);
        tabSheet.add("Delete", deleteTab);
        add(tabSheet);
    }
}
