package com.boulisa.dms.auth;

import com.boulisa.dms.auth.internal.dto.SignupRequest;
import com.boulisa.dms.auth.internal.dto.SignupResponse;
import com.boulisa.dms.auth.internal.exception.CarrierAlreadyExistsException;
import com.boulisa.dms.auth.internal.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/v1/auth/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        authService.createCarrier(signupRequest);
        return new ResponseEntity<>(new SignupResponse("Carrier created successfully"), HttpStatus.CREATED);
    }

    @ExceptionHandler(CarrierAlreadyExistsException.class)
    public ResponseEntity<String> handleCarrierAlreadyExistsException(CarrierAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @GetMapping("/v1/auth/login")
    public String login() {
        return "login-endpoint";
    }

}
