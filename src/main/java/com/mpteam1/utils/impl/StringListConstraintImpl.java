package com.mpteam1.utils.impl;

import com.mpteam1.utils.StringListConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;


public class StringListConstraintImpl implements ConstraintValidator<StringListConstraint, List<String>> {
    @Override
    public void initialize(StringListConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(List<String> value, ConstraintValidatorContext context) {
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

