package com.boulisa.dms.auth.internal.dto;

import com.boulisa.dms.auth.internal.validation.ValidEnum;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.LuhnCheck;
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

public class BillingRequestTest {

    private static final String PLAN = "PRO";

    private static final String NAME_ON_CARD = "Name";

    private static final String CARD_NUMBER = "4111111111111111";

    private static final String EXPIRY_DATE = "1230";

    private static final String CVV = "123";

    private static Validator validator;

    private static ValidatorFactory validatorFactory;

    private static Stream<String> lengthViolatedNameOnCardProvider() {
        return Stream.of("a", "a".repeat(51));
    }

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    /*
    * Plan test cases
    */

    // Negative test cases

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should reject blank plan")
    void shouldFailForBlankPlan(String blankPlan) {
        var request = new BillingRequest(blankPlan, NAME_ON_CARD, CARD_NUMBER, EXPIRY_DATE, CVV);
        var violations = validator.validate(request);
        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString(), violation -> violation.getConstraintDescriptor().getAnnotation().annotationType())
                .contains(tuple("plan", NotBlank.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "plan", "pro", "Pro", "PRo", "elite", "Elite", "ELite", "ELIte", "ELITe"})
    @DisplayName("Should reject invalid plan")
    void shouldFailForInvalidPlan(String invalidPlan) {
        var request = new BillingRequest(invalidPlan, NAME_ON_CARD, CARD_NUMBER, EXPIRY_DATE, CVV);
        var violations = validator.validate(request);
        violations.forEach(System.out::println);
        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString(), violation -> violation.getConstraintDescriptor().getAnnotation().annotationType())
                .containsExactly(tuple("plan", ValidEnum.class));
    }

    // Positive test cases

    @ParameterizedTest
    @ValueSource(strings = {"PRO", "ELITE"})
    @DisplayName("Should accept valid plan")
    void shouldAcceptValidPlan(String validPlan) {
        var request = new BillingRequest(validPlan, NAME_ON_CARD, CARD_NUMBER, EXPIRY_DATE, CVV);
        var violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    /*
    * Name on card test cases
    */

    // Negative test cases

    @Test
    @DisplayName("Should transform [trim - lowercase] name on card")
    void shouldTransformTrimLowercaseNameOnCard() {
        var request = new BillingRequest(PLAN, "    NAME  ", CARD_NUMBER, EXPIRY_DATE, CVV);
        assertThat(request.nameOnCard()).isEqualTo("name");
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
    @DisplayName("Should transform [normalise] name on card")
    void shouldTransformNormaliseNameOnCard(String nameOnCard, String expected) {
        var request = new BillingRequest(PLAN, nameOnCard, CARD_NUMBER, EXPIRY_DATE, CVV);
        assertThat(request.nameOnCard()).isEqualTo(expected);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should reject blank name on card")
    void shouldFailForBlankNameOnCard(String blankNameOnCard) {
        var request = new BillingRequest(PLAN, blankNameOnCard, CARD_NUMBER, EXPIRY_DATE, CVV);
        var violations = validator.validate(request);
        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString(), violation -> violation.getConstraintDescriptor().getAnnotation().annotationType())
                .contains(tuple("nameOnCard", NotBlank.class));
    }

    @ParameterizedTest
    @MethodSource("lengthViolatedNameOnCardProvider")
    @DisplayName("Should reject length-violated name on card")
    void shouldFailForLengthViolatedNameOnCard(String lengthViolatedNameOnCard) {
        var request = new BillingRequest(PLAN, lengthViolatedNameOnCard, CARD_NUMBER, EXPIRY_DATE, CVV);
        var violations = validator.validate(request);
        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString(), violation -> violation.getConstraintDescriptor().getAnnotation().annotationType())
                .containsExactly(tuple("nameOnCard", Size.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"a1a", "a$a", "a_we"})
    @DisplayName("Should reject forbidden characters for name on card")
    void shouldFailForbiddenCharactersNameOnCard(String forbiddenCharactersNameOnCard) {
        var request = new BillingRequest(PLAN, forbiddenCharactersNameOnCard, CARD_NUMBER, EXPIRY_DATE, CVV);
        var violations = validator.validate(request);
        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString(), violation -> violation.getConstraintDescriptor().getAnnotation().annotationType())
                .containsExactly(tuple("nameOnCard", Pattern.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"'auw", "-auw"})
    @DisplayName("Should reject start-violated name on card")
    void shouldFailForStartViolatedNameOnCard(String startViolatedNameOnCard) {
        var request = new BillingRequest(PLAN, startViolatedNameOnCard, CARD_NUMBER, EXPIRY_DATE, CVV);
        var violations = validator.validate(request);
        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString(), violation -> violation.getConstraintDescriptor().getAnnotation().annotationType())
                .containsExactly(tuple("nameOnCard", Pattern.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"auw'", "auw-"})
    @DisplayName("Should reject end-violated name on card")
    void shouldFailForEndViolatedNameOnCard(String endViolatedNameOnCard) {
        var request = new BillingRequest(PLAN, endViolatedNameOnCard, CARD_NUMBER, EXPIRY_DATE, CVV);
        var violations = validator.validate(request);
        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString(), violation -> violation.getConstraintDescriptor().getAnnotation().annotationType())
                .containsExactly(tuple("nameOnCard", Pattern.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"a''a", "a--a", "a  a"})
    @DisplayName("Should reject consecutive symbols and spaces for name on card")
    void shouldFailForConsecutiveSymbolsSpacesNameOnCard(String consecutiveNameOnCard) {
        var request = new BillingRequest(PLAN, consecutiveNameOnCard, CARD_NUMBER, EXPIRY_DATE, CVV);
        var violations = validator.validate(request);
        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString(), violation -> violation.getConstraintDescriptor().getAnnotation().annotationType())
                .containsExactly(tuple("nameOnCard", Pattern.class));
    }

    /*
    * Card number test cases
    */

    // Negative test cases

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should reject blank card number")
    void shouldFailForBlankCardNumber(String blankCardNumber) {
        var request = new BillingRequest(PLAN, NAME_ON_CARD, blankCardNumber, EXPIRY_DATE, CVV);
        var violations = validator.validate(request);
        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString(), violation -> violation.getConstraintDescriptor().getAnnotation().annotationType())
                .contains(tuple("cardNumber", NotBlank.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"3528000000000007", "6011003179988686", "6201089999995464"})
    @DisplayName("Should reject unsupported card number")
    void shouldFailForUnsupportedCardNumber(String unsupportedCardNumber) {
        var request = new BillingRequest(PLAN, NAME_ON_CARD, unsupportedCardNumber, EXPIRY_DATE, CVV);
        var violations = validator.validate(request);
        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString(),violation -> violation.getConstraintDescriptor().getAnnotation().annotationType())
                .containsExactly(tuple("cardNumber", Pattern.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"36259600000012", "371881911767006"})
    @DisplayName("Should reject length-violated card number")
    void shouldFailForLengthViolatedCardNumber(String lengthViolatedCardNumber) {
        var request = new BillingRequest(PLAN, NAME_ON_CARD, lengthViolatedCardNumber, EXPIRY_DATE, CVV);
        var violations = validator.validate(request);
        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString(),violation -> violation.getConstraintDescriptor().getAnnotation().annotationType())
                .contains(tuple("cardNumber", Size.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"4000000000000000", "4111111111111112", "5100000000000000", "5555555555555555"})
    @DisplayName("Should reject non-Luhn card number")
    void shouldFailForNonLuhnCardNumber(String nonLuhnCardNumber) {
        var request = new BillingRequest(PLAN, NAME_ON_CARD, nonLuhnCardNumber, EXPIRY_DATE, CVV);
        var violations = validator.validate(request);
        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString(),violation -> violation.getConstraintDescriptor().getAnnotation().annotationType())
                .containsExactly(tuple("cardNumber", LuhnCheck.class));
    }

    // Positive test cases

    @ParameterizedTest
    @ValueSource(strings = {"4111111111111111", "4222222222222220", "5105105105105100", "5555555555555540"})
    @DisplayName("Should accept valid card number")
    void shouldAcceptValidCardNumber(String validCardNumber) {
        var request = new BillingRequest(PLAN, NAME_ON_CARD, validCardNumber, EXPIRY_DATE, CVV);
        var violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    /*
     * Expiry date test cases
     */

    // Negative test cases

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should reject blank expiry date")
    void shouldFailForBlankExpiryDate(String blankExpiryDate) {
        var request = new BillingRequest(PLAN, NAME_ON_CARD, CARD_NUMBER, blankExpiryDate, CVV);
        var violations = validator.validate(request);
        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString(), violation -> violation.getConstraintDescriptor().getAnnotation().annotationType())
                .containsExactly(tuple("expiryDate", NotBlank.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "aa", "aaa", "aaaa", "a230", "1a30", "12a0", "123a", "1330", "1430", "0426", "0525", "0220"})
    @DisplayName("Should reject invalid expiry date")
    void shouldFailForInvalidExpiryDate(String invalidExpiryDate) {
        var request = new BillingRequest(PLAN, NAME_ON_CARD, CARD_NUMBER, invalidExpiryDate, CVV);
        var violations = validator.validate(request);
        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString(), violation -> violation.getConstraintDescriptor().getAnnotation().annotationType())
                .containsExactly(tuple("validExpiryDate", AssertTrue.class));
    }

    // Positive test cases

    @ParameterizedTest
    @ValueSource(strings = {"0526", "0527", "1230"})
    @DisplayName("Should accept valid expiry date")
    void shouldAcceptValidExpiryDate(String validExpiryDate) {
        var request = new BillingRequest(PLAN, NAME_ON_CARD, CARD_NUMBER, validExpiryDate, CVV);
        var violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    /*
     * CVV test cases
     */

    // Negative test cases

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should reject blank CVV")
    void shouldFailForBlankCVV(String blankCVV) {
        var request = new BillingRequest(PLAN, NAME_ON_CARD, CARD_NUMBER, EXPIRY_DATE, blankCVV);
        var violations = validator.validate(request);
        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString(), violation -> violation.getConstraintDescriptor().getAnnotation().annotationType())
                .contains(tuple("cvv", NotBlank.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "aa", "aaa", "a23", "1a3", "12a"})
    @DisplayName("Should reject invalid CVV format")
    void shouldFailForInvalidCVVFormat(String invalidCVVFormat) {
        var request = new BillingRequest(PLAN, NAME_ON_CARD, CARD_NUMBER, EXPIRY_DATE, invalidCVVFormat);
        var violations = validator.validate(request);
        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString(), violation -> violation.getConstraintDescriptor().getAnnotation().annotationType())
                .containsExactly(tuple("cvv", Pattern.class));
    }

    // Positive test cases

    @ParameterizedTest
    @ValueSource(strings = {"123", "432", "532", "235", "512"})
    @DisplayName("Should accept valid CVV")
    void shouldAcceptValidCVV(String validCVV) {
        var request = new BillingRequest(PLAN, NAME_ON_CARD, CARD_NUMBER, EXPIRY_DATE, validCVV);
        var violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @AfterAll
    static void tearDown() {
        validatorFactory.close();
    }

}
