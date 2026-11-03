package com.vipul.nextin.dto;

import com.vipul.nextin.entity.Role;

// The client sends accessToken back as "Authorization: Bearer <accessToken>" on staff requests.
public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds,
        Long userId,
        String name,
        Role role,
        Long clinicId
) {}
