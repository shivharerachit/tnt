package com.project.ReimbursementPortal.validator;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EmailValidatorTest {

    private static final String VALID_EMAIL = "leanson@company.com";
    private static final String INVALID_EMAIL = "leanson@gmail.com";

    @Spy
    @InjectMocks
    private EmailValidator emailValidator = new EmailValidator("@company.com");

    @BeforeAll
    public static void init() {
        System.out.println("BeforeAll");
    }

    @BeforeEach
    public void initEachTest() {
        System.out.println("BeforeEach");
    }

    @Test
    void validateShouldValidateEmailSuccessfully() {
        assertDoesNotThrow(() -> emailValidator.validate(VALID_EMAIL));
        verify(emailValidator).validate(VALID_EMAIL);
    }

    @Test
    void validateShouldThrowExceptionForInvalidDomain() {
        RuntimeException runtimeException = assertThrows(RuntimeException.class,
                () -> emailValidator.validate(INVALID_EMAIL));

        assertEquals("Invalid email domain", runtimeException.getMessage());
        verify(emailValidator).validate(INVALID_EMAIL);
    }

    @Test
    void validateShouldThrowExceptionWhenAllowedDomainIsNotConfigured() {
        EmailValidator misConfiguredValidator = new EmailValidator("");

        RuntimeException runtimeException = assertThrows(RuntimeException.class,
                () -> misConfiguredValidator.validate(VALID_EMAIL));

        assertEquals("Allowed email domain not configured", runtimeException.getMessage());
        verify(emailValidator, never()).validate(INVALID_EMAIL);
    }

    @AfterEach
    public void cleanup() {
        System.out.println("AfterEach");
    }

    @AfterAll
    public static void destroy() {
        System.out.println("AfterAll");
    }
}
