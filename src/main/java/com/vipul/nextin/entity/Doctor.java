package com.vipul.nextin.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;


@Getter
@Entity
@Table(name="doctors")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "clinic_id", nullable = false)
    private Clinic clinic;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String speciality;

    @Column(nullable = false)
    private Integer avgConsultMinutes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Doctor() {}

    @PrePersist
    void onCreate() {createdAt = Instant.now();}

    public Doctor(Clinic clinic, String name, String speciality, Integer avgConsultMinutes) {
        this.clinic = clinic;
        this.name = name;
        this.speciality = speciality;
        this.avgConsultMinutes = avgConsultMinutes;
    }



}
