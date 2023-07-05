package com.demo.alkolicznik.dto.put;

public interface UpdateModel<T> {

    boolean propertiesMissing();

    boolean anythingToUpdate(T mainModel);
}
