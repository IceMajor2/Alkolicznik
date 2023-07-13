package com.demo.alkolicznik.gui.templates;

import com.vaadin.flow.component.formlayout.FormLayout;

public abstract class FormTemplate<T> extends FormLayout {

    protected abstract void setModel(T model);
}
