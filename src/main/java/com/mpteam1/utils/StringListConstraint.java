package com.mpteam1.utils;

import com.mpteam1.utils.impl.StringListConstraintImpl;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StringListConstraintImpl.class)
public @interface StringListConstraint {
    String message() default "elements must be non-empty strings";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}