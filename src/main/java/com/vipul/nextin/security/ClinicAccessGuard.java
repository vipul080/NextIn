package com.vipul.nextin.security;

import com.vipul.nextin.entity.Role;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

// Roles say WHAT someone may do (run a queue); this guard checks WHERE (only their own clinic).
// A receptionist at clinic 1 must not be able to call patients at clinic 2.
@Component
public class ClinicAccessGuard {

    public void requireStaffOf(Long clinicId) {
        AuthUser user = currentUser();
        if (user.role() == Role.ADMIN) {
            return;
        }
        if (!clinicId.equals(user.clinicId())) {
            throw new AccessDeniedException("You can only manage queues in your own clinic");
        }
    }

    public AuthUser currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthUser user)) {
            throw new AuthenticationCredentialsNotFoundException("Login required");
        }
        return user;
    }
}
