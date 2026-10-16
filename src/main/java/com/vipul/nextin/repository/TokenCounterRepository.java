package com.vipul.nextin.repository;

import com.vipul.nextin.entity.TokenCounter;
import com.vipul.nextin.entity.TokenCounterId;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface TokenCounterRepository extends JpaRepository<TokenCounter, TokenCounterId> {

    // Creates today's counter row if it is missing. ON CONFLICT DO NOTHING makes this safe
    // when two first-of-the-day bookings arrive together: only one insert wins, the other is ignored.
    @Modifying
    @Query(value = """
            INSERT INTO token_counters (doctor_id, queue_date, last_number)
            VALUES (:doctorId, :queueDate, 0)
            ON CONFLICT (doctor_id, queue_date) DO NOTHING
            """, nativeQuery = true)
    void createIfMissing(@Param("doctorId") Long doctorId, @Param("queueDate") LocalDate queueDate);

    // SELECT ... FOR UPDATE: other bookings for the same doctor and day wait here
    // until the current transaction commits.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM TokenCounter c WHERE c.id = :id")
    Optional<TokenCounter> findByIdForUpdate(@Param("id") TokenCounterId id);
}
