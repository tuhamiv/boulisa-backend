package com.boulisa.dms.auth.internal.exception;

public class CarrierAlreadyExistsException extends RuntimeException {
    public CarrierAlreadyExistsException() {
        super("Carrier Already Exists");
    }
}
