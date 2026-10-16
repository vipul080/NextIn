package com.vipul.nextin.entity;

import com.vipul.nextin.exception.InvalidTokenTransitionException;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Entity
@Table(name = "tokens")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(nullable = false)
    private LocalDate queueDate;

    @Column(nullable = false)
    private Integer tokenNumber;

    @Column(nullable = false)
    private String patientName;

    @Column(nullable = false)
    private String patientPhone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TokenStatus status;

    @Version
    @Column(nullable = false)
    private Long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    // empty until the doctor calls the patient in
    private Instant calledAt;

    // empty until the consultation is finished
    private Instant completedAt;

    protected Token() {}

    public Token(Doctor doctor, LocalDate queueDate, Integer tokenNumber, String patientName, String patientPhone) {
        this.doctor = doctor;
        this.queueDate = queueDate;
        this.tokenNumber = tokenNumber;
        this.patientName = patientName;
        this.patientPhone = patientPhone;
        this.status = TokenStatus.WAITING;
    }

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

    // --- Status changes. Any move not listed here is rejected (state machine). ---
    //
    // WAITING --callIn--> IN_CONSULTATION --complete--> DONE
    //    |                     |
    //    |--cancel--> CANCELLED |--skip--> SKIPPED --requeue--> WAITING
    //    |--skip----> SKIPPED

    public void callIn(Instant now) {
        requireStatus(TokenStatus.WAITING, "call in");
        status = TokenStatus.IN_CONSULTATION;
        calledAt = now;
    }

    public void complete(Instant now) {
        requireStatus(TokenStatus.IN_CONSULTATION, "complete");
        status = TokenStatus.DONE;
        completedAt = now;
    }

    public void skip() {
        if (status != TokenStatus.WAITING && status != TokenStatus.IN_CONSULTATION) {
            throw new InvalidTokenTransitionException("Cannot skip a token that is " + status);
        }
        status = TokenStatus.SKIPPED;
    }

    // A late patient goes back into the queue at their original number.
    public void requeue() {
        requireStatus(TokenStatus.SKIPPED, "re-queue");
        status = TokenStatus.WAITING;
        calledAt = null;
    }

    public void cancel() {
        requireStatus(TokenStatus.WAITING, "cancel");
        status = TokenStatus.CANCELLED;
    }

    private void requireStatus(TokenStatus expected, String action) {
        if (status != expected) {
            throw new InvalidTokenTransitionException("Cannot " + action + " a token that is " + status);
        }
    }
}
