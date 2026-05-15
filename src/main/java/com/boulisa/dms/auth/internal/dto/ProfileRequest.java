package com.boulisa.dms.auth.internal.dto;

import com.boulisa.dms.auth.internal.validation.Name;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.stream.IntStream;

import static com.boulisa.dms.auth.internal.util.Transformer.transform;

@GroupSequence({ProfileRequest.class, ProfileRequest.FinalCheck.class})
public record ProfileRequest(
        @Name String firstName,
        @Name String fatherName,
        @Name String grandfatherName,
        @Name String familyName,

        @NotBlank(message = "National ID is required")
        @Pattern(regexp = "^[23]\\d{6}(01|02|03|04|11|12|13|14|15|16|17|18|19|21|22|23|24|25|26|27|28|29|31|32|33|34|35|88)\\d{5}$", message = "Invalid national ID")
        String nationalId,

        @NotBlank(message = "Mobile is required")
        @Pattern(regexp = "^01[0125]\\d{8}$", message = "Invalid mobile")
        String mobile
) {

    public ProfileRequest {
        firstName = transform(firstName);
        fatherName = transform(fatherName);
        grandfatherName = transform(grandfatherName);
        familyName = transform(familyName);
    }

    /*
        *** Illogical national ID checks
     */

    // Invalid DOB

    @SuppressWarnings("unused")
    @AssertTrue(message = "Invalid national ID", groups = ProfileRequest.FinalCheck.class)
    public boolean isValidNationalIdDOB() {
        String dob = nationalId.substring(1, 7);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuMMdd").withResolverStyle(ResolverStyle.STRICT);
        try {
            LocalDate.parse(dob, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    // Invalid checksum

    @SuppressWarnings("unused")
    @AssertTrue(message = "Invalid national ID", groups = ProfileRequest.FinalCheck.class)
    public boolean isValidNationalIdChecksum() {
        final int MODULUS = 11;
        int[] weights = {2, 7, 6, 5, 4, 3, 2, 7, 6, 5, 4, 3, 2};
        int checksum = Character.getNumericValue(nationalId.charAt(nationalId.length() - 1));
        int sum = IntStream.range(0, weights.length).map(i -> Character.getNumericValue(nationalId.charAt(i)) * weights[i]).sum();
        int remainder = sum % MODULUS;
        int calculatedChecksum = switch (remainder) {
            case 0 -> 1;
            case 1 -> 0;
            default -> MODULUS - remainder;
        };
        return checksum == calculatedChecksum;
    }

    public interface FinalCheck {}

}
