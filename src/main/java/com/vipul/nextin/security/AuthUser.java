package com.vipul.nextin.security;

import com.vipul.nextin.entity.Role;

// The logged-in staff member, rebuilt from the JWT on every request (no database lookup).
// clinicId is null for ADMIN.
public record AuthUser(Long id, String email, Role role, Long clinicId) {
}
