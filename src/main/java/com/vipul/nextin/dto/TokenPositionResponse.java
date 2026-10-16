package com.vipul.nextin.dto;

import com.vipul.nextin.entity.TokenStatus;

// What a patient sees on their phone: "You are #12, now serving #9, 3 people ahead, about 30 min."
public record TokenPositionResponse(
        Long tokenId,
        Integer tokenNumber,
        TokenStatus status,
        Integer nowServing,
        long peopleAhead,
        long estimatedWaitMinutes
) {}
