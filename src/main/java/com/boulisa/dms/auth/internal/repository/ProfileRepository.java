package com.boulisa.dms.auth.internal.repository;

import com.boulisa.dms.auth.internal.domain.Profile;
import org.springframework.data.repository.Repository;

@org.springframework.stereotype.Repository
public interface ProfileRepository extends Repository<Profile, Integer> {

    void save(Profile profile);

    boolean existsByNationalId(String nationalId);

    boolean existsByMobile(String mobile);

}
