package com.vipul.nextin.dto;

import com.vipul.nextin.entity.Token;
import com.vipul.nextin.entity.TokenStatus;

import java.time.Instant;
import java.time.LocalDate;

// The patient's phone number is deliberately left out: the queue list is shown to staff screens.
public record TokenResponse(
        Long id,
        Long doctorId,
        String doctorName,
        LocalDate queueDate,
        Integer tokenNumber,
        String patientName,
        TokenStatus status,
        Instant createdAt,
        Instant calledAt,
        Instant completedAt
) {
    public static TokenResponse from(Token token) {
        return new TokenResponse(
                token.getId(),
                token.getDoctor().getId(),
                token.getDoctor().getName(),
                token.getQueueDate(),
                token.getTokenNumber(),
                token.getPatientName(),
                token.getStatus(),
                token.getCreatedAt(),
                token.getCalledAt(),
                token.getCompletedAt());
    }
}
