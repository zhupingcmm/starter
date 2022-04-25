package com.mf.starter.validation;


import com.mf.starter.annotation.ValidatePasswordMatch;
import com.mf.starter.domain.dto.UserDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<ValidatePasswordMatch, UserDto> {
    @Override
    public void initialize(ValidatePasswordMatch constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(UserDto userDto, ConstraintValidatorContext context) {
        return userDto.getPassword().equals(userDto.getMatchingPassword());
    }
}
