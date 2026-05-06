package mate.academy.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {

    private String field;
    private String fieldMatch;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.fieldMatch = constraintAnnotation.fieldMatch();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            Field firstField = value.getClass().getDeclaredField(field);
            firstField.setAccessible(true);
            Object firstValue = firstField.get(value);
            Field secondField = value.getClass().getDeclaredField(fieldMatch);
            secondField.setAccessible(true);
            Object secondValue = secondField.get(value);
            return firstValue != null && firstValue.equals(secondValue);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return false;
        }
    }
}
