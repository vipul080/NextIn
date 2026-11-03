package com.vipul.nextin.repository;

import com.vipul.nextin.entity.Role;
import com.vipul.nextin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByRole(Role role);

    // The underscore means "clinic, then its id". Without it, Spring Data would pick up
    // User.getClinicId() and look for a clinicId field that Hibernate does not know.
    List<User> findByClinic_IdOrderByNameAsc(Long clinicId);
}
