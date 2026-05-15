package com.boulisa.dms.auth.internal.dto;

import com.boulisa.dms.auth.internal.domain.Plan;

import com.boulisa.dms.auth.internal.validation.ValidEnum;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.LuhnCheck;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import static com.boulisa.dms.auth.internal.util.Transformer.transform;

@GroupSequence({BillingRequest.class, BillingRequest.FinalCheck.class})
public record BillingRequest(

        @NotBlank(message = "Plan is required")
        @ValidEnum(value = Plan.class, message = "Plan is invalid")
        String plan,

        @NotBlank(message = "Name on card is required")
        @Size(min = 2, max = 50, message = "Name on card must be 2-50 characters")
        @Pattern(regexp= "^[\\p{L}\\s'-]+$", message = "Letters, spaces, ' and - are only allowed")
        @Pattern(regexp = "^\\p{L}.*$", message = "Name on card must start with a letter")
        @Pattern(regexp = "^.*\\p{L}$", message = "Name on card must end with a letter")
        @Pattern(regexp = "^(?!.*[\\s'-]{2,}).*$", message = "Name on card cannot contain consecutive symbols or spaces")
        String nameOnCard,

        @NotBlank(message = "Card number is required")
        @Pattern(regexp = "^[245]\\d{15}$", message = "Unsupported card number")
        @Size(min = 16, max = 16, message = "Card number must be 16 digits")
        @LuhnCheck(message = "Card number is invalid")
        String cardNumber,

        @NotBlank(message = "Expiry date is required")
        String expiryDate,

        @NotBlank(message = "CVV is required")
        @Pattern(regexp = "^\\d{3}$", message = "CVV is invalid")
        String cvv
) {

    public BillingRequest { nameOnCard = transform(nameOnCard); }

    @SuppressWarnings("unused")
    @AssertTrue(message = "Invalid expiry date", groups = BillingRequest.FinalCheck.class)
    public boolean isValidExpiryDate() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMuu").withResolverStyle(ResolverStyle.STRICT);
            try {
                    YearMonth parsedYearMonth = YearMonth.parse(expiryDate, formatter);
                    YearMonth nowYearMonth = YearMonth.now();
                    return !parsedYearMonth.isBefore(nowYearMonth);
            } catch (DateTimeParseException e) {
                    return false;
            }
    }

    public interface FinalCheck {}

}
