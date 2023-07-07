package com.demo.alkolicznik.models;

public enum Roles {
    USER, ACCOUNTANT, ADMIN;

    public boolean isAdmin() {
        return this == ADMIN;
    }
}
