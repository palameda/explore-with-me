package ru.practicum.utility.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class EventStartValidator implements ConstraintValidator<StartTimeValidation, LocalDateTime> {

    @Override
    public boolean isValid(LocalDateTime startTime, ConstraintValidatorContext constraintValidatorContext) {
        if (startTime == null) return true;
        return startTime.minusHours(2).isAfter(LocalDateTime.now());
    }
}
