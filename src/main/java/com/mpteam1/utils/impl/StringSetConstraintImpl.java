package com.mpteam1.utils.impl;

import com.mpteam1.utils.StringSetConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class StringSetConstraintImpl implements ConstraintValidator<StringSetConstraint, Set<String>> {
    @Override
    public void initialize(StringSetConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(Set<String> value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        for (String element : value) {
            if (element == null || element.trim().isEmpty() || element.matches("^\\d+$")) {
                return false;
            }
        }
        return true;
    }
}
