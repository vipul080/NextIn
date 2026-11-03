package com.vipul.nextin.config;

import com.vipul.nextin.security.JwtAuthenticationFilter;
import com.vipul.nextin.security.JwtService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
public class SecurityConfig {

    private static final String[] STAFF = {"ADMIN", "RECEPTIONIST", "DOCTOR"};

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtService jwtService,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver) throws Exception {

        http
                // No cookies or server sessions: every request carries its own JWT,
                // so CSRF protection (which defends cookie-based sessions) is not needed.
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error").permitAll()

                        // Public: staff login, browsing clinics and doctors, and the patient side
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/clinics", "/api/clinics/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/doctors/*/tokens").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/tokens", "/api/tokens/*/position").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/tokens/*/cancel").permitAll()

                        // Admin only: set up clinics, doctors and staff accounts
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/clinics", "/api/clinics/*/doctors").hasRole("ADMIN")

                        // Staff: run the queue (ClinicAccessGuard also limits them to their own clinic)
                        .requestMatchers("/api/doctors/*/queue", "/api/doctors/*/queue/**").hasAnyRole(STAFF)
                        .requestMatchers(HttpMethod.POST,
                                "/api/tokens/*/complete", "/api/tokens/*/skip", "/api/tokens/*/requeue").hasAnyRole(STAFF)

                        .anyRequest().authenticated())
                // Send 401/403 through GlobalExceptionHandler, so they use the same ApiError JSON as every other error.
                .exceptionHandling(errors -> errors
                        .authenticationEntryPoint((request, response, ex) ->
                                exceptionResolver.resolveException(request, response, null, ex))
                        .accessDeniedHandler((request, response, ex) ->
                                exceptionResolver.resolveException(request, response, null, ex)))
                .addFilterBefore(new JwtAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // BCrypt hashes passwords with a random salt and is deliberately slow, which makes guessing expensive.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
