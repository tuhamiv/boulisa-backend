package com.boulisa.dms.auth.internal.domain;

import com.boulisa.dms.auth.internal.dto.ProfileRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "profile")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Profile extends BaseEqualityEntity {

    @Id
    @Column(name = "account_id")
    private Integer id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "father_name", nullable = false)
    private String fatherName;

    @Column(name = "grandfather_name", nullable = false)
    private String grandfatherName;

    @Column(name = "family_name", nullable = false)
    private String familyName;

    @Column(name = "national_id", unique = true, nullable = false)
    private String nationalId;

    @Column(unique = true, nullable = false)
    private String mobile;

    @MapsId
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    public Profile(Account account, ProfileRequest pr) {
        this.account = account;
        this.firstName = pr.firstName();
        this.fatherName = pr.fatherName();
        this.grandfatherName = pr.grandfatherName();
        this.familyName = pr.familyName();
        this.nationalId = pr.nationalId();
        this.mobile = pr.mobile();
    }

}
