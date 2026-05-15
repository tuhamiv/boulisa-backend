package com.boulisa.dms.auth.internal.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@NotBlank(message = "is required")
@Size(min = 2, max = 30, message = "must be 2-30 characters")
@Pattern(regexp = "^[\\p{L}\\s'-]+$", message = "letters, spaces, ' and - only are allowed")
@Pattern(regexp = "^\\p{L}.*$", message = "must start with a letter")
@Pattern(regexp = "^.*\\p{L}$", message = "must end with a letter")
@Pattern(regexp = "^(?!.*[\\s'-]{2,}).*$", message = "cannot contain consecutive symbols or spaces")
@Constraint(validatedBy = {})
@Target({FIELD})
@Retention(RUNTIME)
public @interface Name {

    String message() default "{com.boulisa.dms.auth.internal.validation.Name.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
