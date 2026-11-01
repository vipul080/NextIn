package com.vipul.nextin.repository;

import com.vipul.nextin.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    public List<Doctor> findByClinicId(Long id);
    public Optional<Doctor> findByIdAndClinicId(Long id, Long clinicId);
}
