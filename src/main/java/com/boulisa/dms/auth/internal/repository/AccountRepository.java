package com.boulisa.dms.auth.internal.repository;

import com.boulisa.dms.auth.internal.domain.Account;
import org.springframework.data.repository.Repository;

@org.springframework.stereotype.Repository
public interface AccountRepository extends Repository<Account, Integer> {

    Account save(Account account);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

}
