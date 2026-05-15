package com.boulisa.dms.auth.internal.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountRequestTest {

    private static final String TEST_USERNAME = "testUser";

    private static final String TEST_EMAIL = "test@gmail.com";

    private static final String TEST_PASS = "test";

    private static Validator validator;

    private static ValidatorFactory validatorFactory;

    private static Stream<String> passwordProvider() {
        return Stream.of(
                "a".repeat(15),
                "a".repeat(65)
        );
    }

    @BeforeAll
    static void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    // Username testcases

    @Test
    @DisplayName("Should sanitize username with trim and lowercase")
    void shouldSanitizeUsername() {
        var request = new AccountRequest("   TUHAMI   ", TEST_EMAIL, TEST_PASS);
        assertThat(request.username()).isEqualTo("tuhami");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "\t", "    "})
    @DisplayName("Should reject blank values")
    void shouldFailForBlankUsername(String blankUsername) {
        var request = new AccountRequest(blankUsername, TEST_EMAIL, TEST_PASS);
        var violations = validator.validate(request);
        assertThat(violations)
                .extracting("message")
                .contains("Username is required");
    }

    @ParameterizedTest
    @ValueSource(strings = {"v", "va", "value.value.value.val"})
    @DisplayName("Should reject username length violations")
    void shouldFailForInvalidUsernameLength(String invalidLengthUsername) {
        var request = new AccountRequest(invalidLengthUsername, TEST_EMAIL, TEST_PASS);
        var violations = validator.validate(request);
        assertThat(violations)
                .extracting("message")
                .contains("Username must be 3-20 characters");
    }

    @ParameterizedTest
    @ValueSource(strings = {"v#2", "pf[f", "p2(kf"})
    @DisplayName("Should reject forbidden characters")
    void shouldFailForForbiddenCharacters(String usernameWithForbiddenCharacters) {
        var request = new AccountRequest(usernameWithForbiddenCharacters, TEST_EMAIL, TEST_PASS);
        var violations = validator.validate(request);
        assertThat(violations)
                .extracting("message")
                .contains("Letters, numbers, . and _ only are allowed");
    }

    @ParameterizedTest
    @ValueSource(strings = {".value", "_value"})
    @DisplayName("Should reject usernames start with symbols")
    void shouldFailForInvalidUsernameStartWithSymbols(String usernameStartsWithSymbol) {
        var request = new AccountRequest(usernameStartsWithSymbol, TEST_EMAIL, TEST_PASS);
        var violations = validator.validate(request);
        assertThat(violations)
                .extracting("message")
                .contains("Username cannot start with a symbol");
    }

    @ParameterizedTest
    @ValueSource(strings = {"value.", "value_"})
    @DisplayName("Should reject usernames end with symbols")
    void shouldFailForUsernamesEndWithSymbols(String usernameEndWithSymbol) {
        var request = new AccountRequest(usernameEndWithSymbol, TEST_EMAIL, TEST_PASS);
        var violations = validator.validate(request);
        assertThat(violations)
                .extracting("message")
                .contains("Username cannot end with a symbol");
    }

    @ParameterizedTest
    @ValueSource(strings = {"123", "3.3", "3_23.3"})
    @DisplayName("Should reject usernames with no letters")
    void shouldFailForNoLettersUsername(String noLettersUsername) {
        var request = new AccountRequest(noLettersUsername, TEST_EMAIL, TEST_PASS);
        var violations = validator.validate(request);
        assertThat(violations)
                .extracting("message")
                .contains("Username must contain at least one letter");
    }

    @ParameterizedTest
    @ValueSource(strings = {"value..value", "value._v", "value__value"})
    @DisplayName("Should reject consecutive symbols")
    void shouldFailForConsecutiveSymbols(String consecutiveSymbols) {
        var request = new AccountRequest(consecutiveSymbols, TEST_EMAIL, TEST_PASS);
        var violations = validator.validate(request);
        assertThat(violations)
                .extracting("message")
                .contains("Username cannot contain consecutive symbols");
    }

    @ParameterizedTest
    @ValueSource(strings = {"admin", "root", "support", "api", "help"})
    @DisplayName("Should reject reserved usernames")
    void ShouldFailForReservedUsername(String username) {
        var request = new AccountRequest(username, TEST_EMAIL, TEST_PASS);
        var violations = validator.validate(request);
        assertThat(violations)
                .extracting("message")
                .contains("Username is reserved");
    }

    // Email testcases

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"", "    "})
    @DisplayName("Should reject blank email")
    void shouldFailForBlankEmail(String blankEmail) {
        var request = new AccountRequest(TEST_USERNAME, blankEmail, TEST_PASS);
        var violations = validator.validate(request);
        assertThat(violations)
                .extracting("message")
                .contains("Email is required");
    }

    @ParameterizedTest
    @ValueSource(strings = {"userexample.com", "user@", "@example.com", "user@@example.com", "user @example.com", "user@example .com", "user()[]@example.com", ".user@example.com", "user@example.com."})
    @DisplayName("Should reject invalid email")
    void shouldFailForInvalidEmail(String invalidEmail) {
        var request = new AccountRequest(TEST_USERNAME, invalidEmail, TEST_PASS);
        var violations = validator.validate(request);
        assertThat(violations)
                .extracting("message")
                .contains("Invalid email");
    }

    // Password testcases

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"", "    "})
    @DisplayName("Should reject blank password")
    void shouldFailForBlankPassword(String blankPassword) {
        var request = new AccountRequest(TEST_USERNAME, TEST_EMAIL, blankPassword);
        var violations = validator.validate(request);
        assertThat(violations)
                .extracting("message")
                .contains("Password is required");
    }

    @ParameterizedTest
    @MethodSource("passwordProvider")
    @DisplayName("Should reject password length violations")
    void shouldFailForPasswordLengthViolations(String violatedLengthPassword) {
        var request = new AccountRequest(TEST_USERNAME, TEST_EMAIL, violatedLengthPassword);
        var violations = validator.validate(request);
        assertThat(violations)
                .extracting("message")
                .contains("Password must be 16-64 characters");
    }

    // Cross-field (username - password) testcases

    @Test
    @DisplayName("Should reject the password to be as username")
    void shouldFailForPasswordAsUsername() {
        var request = new AccountRequest("username12345678", TEST_EMAIL, "username12345678");
        var violations = validator.validate(request);
        assertThat(violations)
                .extracting("message")
                .contains("Password cannot be as username");
    }

    @AfterAll
    static void tearDown() {
        validatorFactory.close();
    }

}
