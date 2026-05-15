package com.boulisa.dms.auth.internal.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record SignupRequest(
        @Valid @NotNull(message = "Account data is missing") AccountRequest account,
        @Valid @NotNull(message = "Profile data is missing") ProfileRequest profile,
        @Valid @NotNull(message = "Billing data is missing") BillingRequest billing
) {

}
