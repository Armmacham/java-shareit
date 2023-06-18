package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ItemRequestDtoTest {
    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    public static void createValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    public static void close() {
        validatorFactory.close();
    }

    @Test
    public void validationSuccess() {
        ItemRequestDto itemRequest = new ItemRequestDto(
                "description"
        );

        Set<ConstraintViolation<ItemRequestDto>> violations = validator.validate(itemRequest);

        assertTrue(violations.isEmpty());
    }

    @Test
    public void validationEmptyDescription() {
        ItemRequestDto itemRequest = new ItemRequestDto(
                ""
        );

        Set<ConstraintViolation<ItemRequestDto>> violations = validator.validate(itemRequest);

        assertFalse(violations.isEmpty());
    }

    @Test
    public void validationDescriptionTooLong() {
        ItemRequestDto itemRequest = new ItemRequestDto(
                "a".repeat(201)
        );

        Set<ConstraintViolation<ItemRequestDto>> violations = validator.validate(itemRequest);

        assertFalse(violations.isEmpty());
    }
}

