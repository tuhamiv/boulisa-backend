package com.boulisa.dms.auth.internal.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = {ValidEnumValidator.class})
@Documented
@Repeatable(ValidEnum.List.class)
public @interface ValidEnum {

    String message() default "{com.boulisa.dms.auth.internal.validation.ValidEnum.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<? extends java.lang.Enum<?>> value();

    @Target({FIELD})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        ValidEnum[] value();
    }

}
