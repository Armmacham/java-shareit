package ru.practicum.shareit.user;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@JsonTest
public class UserDtoTest {

    @Autowired
    private JacksonTester<UserDTO> jacksonTester;

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
    @SneakyThrows
    public void validationTest() {
        UserDTO userDTO = new UserDTO(
                1L, "user", "user@user.com"
        );

        JsonContent<UserDTO> json = jacksonTester.write(userDTO);
        assertThat(json).extractingJsonPathStringValue("$.email").isEqualTo("user@user.com");
    }

    @Test
    public void validation() {
        UserDTO userDTO = new UserDTO(
                1L, "user", "user@user.com"
        );

        Set<ConstraintViolation<UserDTO>> violations = validator.validate(userDTO);

        assertTrue(violations.isEmpty());
    }

    @Test
    public void invalidDto() {
        UserDTO userDTO = new UserDTO(
                1L, "", "user"
        );

        Set<ConstraintViolation<UserDTO>> violations = validator.validate(userDTO);

        assertEquals(2, violations.size());
    }
}

