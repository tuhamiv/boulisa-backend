package com.boulisa.dms.auth.internal.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@MappedSuperclass
@Getter
public abstract class BaseEqualityEntity {

    @Column(unique = true, nullable = false, updatable = false)
    private final UUID uuid = UUID.randomUUID();

    @Override
    public int hashCode() { return Objects.hash(this.getUuid()); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof BaseEqualityEntity other)) return false;
        if (!this.getClass().isAssignableFrom(obj.getClass()) && !obj.getClass().isAssignableFrom(this.getClass())) return false;
        return Objects.equals(this.getUuid(), other.getUuid());
    }

}
