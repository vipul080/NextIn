package com.vipul.nextin.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;

// A staff account (admin, receptionist or doctor). Patients do not have accounts.
@Getter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    // BCrypt hash, never the plain password
    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // null for ADMIN
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinic_id")
    private Clinic clinic;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected User() {}

    public User(String name, String email, String passwordHash, Role role, Clinic clinic) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.clinic = clinic;
    }

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

    // Reading the id from the lazy clinic does not run a query.
    public Long getClinicId() {
        return clinic == null ? null : clinic.getId();
    }
}
