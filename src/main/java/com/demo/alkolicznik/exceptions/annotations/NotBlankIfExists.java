package com.demo.alkolicznik.exceptions.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NotBlankIfExistsValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotBlankIfExists {

    String message() default "Field must contain at least one non-whitespace character";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
