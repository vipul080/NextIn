package com.vipul.nextin.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@Entity
@Table(name = "clinics")
public class Clinic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String phone;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Clinic() {
    }

    // runs automatically just before the first INSERT
    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

    public Long getId()          { return id; }
    public String getName()      { return name; }
    public String getCity()      { return city; }
    public String getAddress()   { return address; }
    public String getPhone()     { return phone; }
    public Instant getCreatedAt() { return createdAt; }
}