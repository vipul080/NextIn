package com.vipul.nextin.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

// Composite primary key of token_counters: one counter per doctor per day.
@Getter
@Embeddable
public class TokenCounterId implements Serializable {

    private Long doctorId;
    private LocalDate queueDate;

    protected TokenCounterId() {}

    public TokenCounterId(Long doctorId, LocalDate queueDate) {
        this.doctorId = doctorId;
        this.queueDate = queueDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TokenCounterId other)) return false;
        return Objects.equals(doctorId, other.doctorId) && Objects.equals(queueDate, other.queueDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(doctorId, queueDate);
    }
}
