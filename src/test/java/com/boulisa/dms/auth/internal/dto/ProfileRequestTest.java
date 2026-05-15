package com.boulisa.dms.auth.internal.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class ProfileRequestTest {

    private static ValidatorFactory validatorFactory;

    private static Validator validator;

    private static final String NAME = "Name";

    private static final String NATIONAL_ID = "30001010112347";

    private static final String MOBILE = "01212345678";

    private static Stream<String> lengthViolatedNamesProvider() {
        return Stream.of("a", "a".repeat(31));
    }

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    // Transformation [trim - lowercase - normalize] test cases

    @Test
    @DisplayName("Should transform [trim - lowercase] name")
    void shouldTransformNameTrimLowerCase() {
        var request = new ProfileRequest("  Abdulrahman    ", NAME, NAME, NAME, NATIONAL_ID, MOBILE);
        assertThat(request.firstName()).isEqualTo("abdulrahman");
    }

    @ParameterizedTest
    @CsvSource({
            "ﬁ, fi",
            "², 2",
            "①, 1",
            "Ⅸ, ix",
            "½, 1⁄2",
            "ℌ, h",
            "Ａ, a",
            "１, 1",
            "％, %",
            "Ω, ω"
    })
    @DisplayName("Should transform [normalise] name")
    void shouldTransformNameNormalise(String name, String expected) {
        var request = new ProfileRequest(name, NAME, NAME, NAME, NATIONAL_ID, MOBILE);
        assertThat(request.firstName()).isEqualTo(expected);
    }

    // Name test cases

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should reject blank names")
    void shouldFailForBlankNames(String blankName) {
        var request = new ProfileRequest(blankName, NAME, NAME, NAME, NATIONAL_ID, MOBILE);
        var violations = validator.validate(request);
        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString(), violation -> violation.getConstraintDescriptor().getAnnotation().annotationType())
                .contains(tuple("firstName", NotBlank.class));
    }

    @ParameterizedTest
    @MethodSource("lengthViolatedNamesProvider")
    @DisplayName("Should reject length-violated names")
    void shouldFailForLengthViolatedNames(String lengthViolatedName) {
        var request = new ProfileRequest(lengthViolatedName, NAME, NAME, NAME, NATIONAL_ID, MOBILE);
        var violations = validator.validate(request);
        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString(), violation -> violation.getConstraintDescriptor().getAnnotation().annotationType())
                .contains(tuple("firstName", Size.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"N@a", "Na#aa", "li@wow$ie", "'na", "-na", "na'", "na-", "n''s", "n--s", "n  s"})
    @DisplayName("Should reject format-violated names")
    void shouldFailForFormatViolatedNames(String formatViolatedName) {
        var request = new ProfileRequest(formatViolatedName, NAME, NAME, NAME, NATIONAL_ID, MOBILE);
        var violations = validator.validate(request);
        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString(), violation -> violation.getConstraintDescriptor().getAnnotation().annotationType())
                .contains(tuple("firstName", Pattern.class));
    }

    // National ID test cases

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should reject blank national ID")
    void shouldFailForBlankNationalId(String blankNationalId) {
        var request = new ProfileRequest(NAME, NAME, NAME, NAME, blankNationalId, MOBILE);
        var violations = validator.validate(request);
        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString(), violation -> violation.getConstraintDescriptor().getAnnotation().annotationType())
                .contains(tuple("nationalId", NotBlank.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            // length
            "3",
            "30",
            "303",
            "3031",
            "30312",
            "303121",
            "3031217",
            "30312171",
            "303121712",
            "3031217127",
            "30312171272",
            "303121712721",
            "3031217127210",

            // century
            "08306100135214",
            "48306100135217",

            // province
            "30209110554215",
            "30209118954215"
    })
    @DisplayName("Should reject format-violated national ID")
    void shouldFailForFormatViolatedNationalId(String formatViolatedNationalId) {
        var request = new ProfileRequest(NAME, NAME, NAME, NAME, formatViolatedNationalId, MOBILE);
        var violations = validator.validate(request);
        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString(), violation -> violation.getConstraintDescriptor().getAnnotation().annotationType())
                .containsExactly(tuple("nationalId", Pattern.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            // DOB
            "30102290182133",
            "30104310182138",
            "30106310182131",
            "30109310182130",
            "30111310182134"
    })
    @DisplayName("Should reject illogical [invalid DOB] national ID")
    void shouldFailForIllegalNationalId(String invalidDOBNationalId) {
        var request = new ProfileRequest(NAME, NAME, NAME, NAME, invalidDOBNationalId, MOBILE);
        var violations = validator.validate(request);
        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString(), violation -> violation.getConstraintDescriptor().getAnnotation().annotationType())
                .containsExactly(tuple("validNationalIdDOB", AssertTrue.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            // Invalid checksum
            "28301170188223",
            "30502110188235",
            "29912030161508"
    })
    @DisplayName("Should reject illogical [invalid checksum] national ID")
    void shouldFailForInvalidChecksumNationalId(String invalidChecksumNationalId) {
        var request = new ProfileRequest(NAME, NAME, NAME, NAME, invalidChecksumNationalId, MOBILE);
        var violations = validator.validate(request);
        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString(), violation -> violation.getConstraintDescriptor().getAnnotation().annotationType())
                .containsExactly(tuple("validNationalIdChecksum", AssertTrue.class));
    }

    // Mobile test cases

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should reject blank mobile")
    void shouldFailForBlankMobile(String blankMobile) {
        var request = new ProfileRequest(NAME, NAME, NAME, NAME, NATIONAL_ID, blankMobile);
        var violations = validator.validate(request);
        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString(), violation -> violation.getConstraintDescriptor().getAnnotation().annotationType())
                .contains(tuple("mobile", NotBlank.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            // Length
            "0",
            "01",
            "012",
            "0120",
            "01201",
            "012012",
            "0120123",
            "01201234",
            "012012345",
            "0120123456",

            // Format
            "11201234567",
            "01601234567",
    })
    @DisplayName("Should reject invalid mobile format")
    void shouldFailForInvalidMobileFormat(String invalidMobile) {
        var request = new ProfileRequest(NAME, NAME, NAME, NAME, NATIONAL_ID, invalidMobile);
        var violations = validator.validate(request);
        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString(), violation -> violation.getConstraintDescriptor().getAnnotation().annotationType())
                .containsExactly(tuple("mobile", Pattern.class));
    }

    @AfterAll
    static void tearDown() {
        validatorFactory.close();
    }

}
