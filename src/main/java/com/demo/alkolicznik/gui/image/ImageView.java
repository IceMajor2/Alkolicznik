package com.demo.alkolicznik.gui.image;

import com.demo.alkolicznik.gui.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "image", layout = MainLayout.class)
@PageTitle("Upload image | Alkolicznik")
@RolesAllowed({"ADMIN"})
public class ImageView extends VerticalLayout {

    public ImageView() {

        RadioButtonGroup<String> radioGroup = new RadioButtonGroup<>();
        radioGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        radioGroup.setLabel("Add image for:");
        radioGroup.setItems("Beer", "Store");
        add(radioGroup);
    }
}
