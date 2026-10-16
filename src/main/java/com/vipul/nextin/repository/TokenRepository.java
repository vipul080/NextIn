package com.vipul.nextin.repository;

import com.vipul.nextin.entity.Token;
import com.vipul.nextin.entity.TokenStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    // Today's queue for one doctor, in token order.
    List<Token> findByDoctorIdAndQueueDateAndStatusInOrderByTokenNumberAsc(
            Long doctorId, LocalDate queueDate, Collection<TokenStatus> statuses);

    // The lowest-numbered token in a given status, e.g. the next WAITING patient.
    Optional<Token> findFirstByDoctorIdAndQueueDateAndStatusOrderByTokenNumberAsc(
            Long doctorId, LocalDate queueDate, TokenStatus status);

    boolean existsByDoctorIdAndQueueDateAndStatus(Long doctorId, LocalDate queueDate, TokenStatus status);

    // How many WAITING patients are ahead of a given token number.
    long countByDoctorIdAndQueueDateAndStatusAndTokenNumberLessThan(
            Long doctorId, LocalDate queueDate, TokenStatus status, Integer tokenNumber);

    // A patient's tokens for today. The tokens can belong to different doctors, so the
    // doctor is loaded in the same query (EntityGraph) instead of one query per token (N+1).
    @EntityGraph(attributePaths = "doctor")
    List<Token> findByPatientPhoneAndQueueDateOrderByTokenNumberAsc(String patientPhone, LocalDate queueDate);
}
