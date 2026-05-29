package com.boulisa.dms.auth.internal.service;

import com.boulisa.dms.auth.internal.domain.Account;
import com.boulisa.dms.auth.internal.domain.Profile;
import com.boulisa.dms.auth.internal.domain.Role;
import com.boulisa.dms.auth.internal.dto.SignupRequest;
import com.boulisa.dms.auth.internal.exception.CarrierAlreadyExistsException;
import com.boulisa.dms.auth.internal.repository.AccountRepository;
import com.boulisa.dms.auth.internal.repository.ProfileRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final PasswordEncoder encoder;

    private final AccountRepository accountRepository;

    private final ProfileRepository profileRepository;

    public AuthService(PasswordEncoder encoder, AccountRepository accountRepository, ProfileRepository profileRepository) {
        this.encoder = encoder;
        this.accountRepository = accountRepository;
        this.profileRepository = profileRepository;
    }

    @Transactional
    public void createCarrier(SignupRequest request) {
        if (

                accountRepository.existsByUsername(request.account().username()) ||
                        accountRepository.existsByEmail(request.account().email()) ||
                        profileRepository.existsByNationalId(request.profile().nationalId()) ||
                        profileRepository.existsByMobile(request.profile().mobile())

        ) throw new CarrierAlreadyExistsException();

        Account account = new Account(
                request.account().username(),
                request.account().email(),
                encoder.encode(request.account().password()),
                Role.CARRIER
        );

        Account savedAccount = accountRepository.save(account);

        Profile profile = new Profile(savedAccount, request.profile());

        profileRepository.save(profile);
    }

}
