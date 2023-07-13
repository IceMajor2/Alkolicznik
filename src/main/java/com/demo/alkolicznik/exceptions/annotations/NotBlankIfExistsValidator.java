package com.demo.alkolicznik.exceptions.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotBlankIfExistsValidator implements
        ConstraintValidator<NotBlankIfExists, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null) {
            return true;
        }
        return !value.trim().isEmpty();
    }

    @Override
    public void initialize(NotBlankIfExists constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }
}
