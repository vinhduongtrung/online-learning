package com.mpteam1.utils;

import com.mpteam1.utils.impl.StringSetConstraintImpl;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StringSetConstraintImpl.class)
public @interface StringSetConstraint {
    String message() default "elements must be non-empty strings";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
