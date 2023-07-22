package com.demo.alkolicznik.dto;

public interface UpdateModel<T> {

    boolean propertiesMissing();

    boolean anythingToUpdate(T mainModel);
}
