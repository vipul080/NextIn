package com.vipul.nextin.entity;

import jakarta.persistence.*;
import lombok.Getter;

// Last queue token number handed out for one doctor on one day.
// Rows are created by TokenCounterRepository.createIfMissing, so there is no public constructor.
@Getter
@Entity
@Table(name = "token_counters")
public class TokenCounter {

    @EmbeddedId
    private TokenCounterId id;

    @Column(nullable = false)
    private Integer lastNumber;

    protected TokenCounter() {}

    // Only safe while this row is locked (see TokenService.bookToken).
    public int next() {
        lastNumber = lastNumber + 1;
        return lastNumber;
    }
}
