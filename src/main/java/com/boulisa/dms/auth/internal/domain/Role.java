package com.boulisa.dms.auth.internal.domain;

import jakarta.persistence.EnumeratedValue;

public enum Role {

    CARRIER(100);

    @EnumeratedValue
    private final int code;

    Role(int code) {
        this.code = code;
    }

}
