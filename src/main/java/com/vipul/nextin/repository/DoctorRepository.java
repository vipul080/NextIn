package com.vipul.nextin.repository;

import com.vipul.nextin.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    public List<Doctor> findByClinic_Id(Long id);
}
