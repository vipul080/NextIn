package com.vipul.nextin.service;

import com.vipul.nextin.dto.BookTokenRequest;
import com.vipul.nextin.dto.TokenPositionResponse;
import com.vipul.nextin.dto.TokenResponse;
import com.vipul.nextin.entity.*;
import com.vipul.nextin.exception.DoctorNotFoundException;
import com.vipul.nextin.exception.InvalidTokenTransitionException;
import com.vipul.nextin.exception.ResourceNotFoundException;
import com.vipul.nextin.exception.TokenNotFoundException;
import com.vipul.nextin.repository.DoctorRepository;
import com.vipul.nextin.repository.TokenCounterRepository;
import com.vipul.nextin.repository.TokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
public class TokenService {

    private static final List<TokenStatus> ACTIVE_STATUSES =
            List.of(TokenStatus.WAITING, TokenStatus.IN_CONSULTATION);

    private final TokenRepository tokenRepository;
    private final TokenCounterRepository tokenCounterRepository;
    private final DoctorRepository doctorRepository;
    private final Clock clock;

    public TokenService(TokenRepository tokenRepository,
                        TokenCounterRepository tokenCounterRepository,
                        DoctorRepository doctorRepository,
                        Clock clock) {
        this.tokenRepository = tokenRepository;
        this.tokenCounterRepository = tokenCounterRepository;
        this.doctorRepository = doctorRepository;
        this.clock = clock;
    }

    // Two patients booking the same doctor at the same moment must never get the same number:
    // 1. make sure today's counter row exists,
    // 2. lock it (SELECT ... FOR UPDATE) so concurrent bookings line up one after another,
    // 3. take the next number and insert the token in the same transaction.
    // The lock is released when the transaction commits. UNIQUE(doctor_id, queue_date, token_number)
    // in the database is the safety net if this logic ever breaks.
    @Transactional
    public TokenResponse bookToken(Long doctorId, BookTokenRequest request) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException(doctorId));
        LocalDate today = LocalDate.now(clock);

        tokenCounterRepository.createIfMissing(doctorId, today);
        TokenCounter counter = tokenCounterRepository.findByIdForUpdate(new TokenCounterId(doctorId, today))
                .orElseThrow(() -> new IllegalStateException("Token counter missing for doctor " + doctorId));
        int tokenNumber = counter.next();

        Token token = new Token(doctor, today, tokenNumber, request.patientName(), request.patientPhone());
        Token saved = tokenRepository.save(token);
        return TokenResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<TokenResponse> getTodayQueue(Long doctorId) {
        requireDoctor(doctorId);
        return tokenRepository
                .findByDoctorIdAndQueueDateAndStatusInOrderByTokenNumberAsc(doctorId, today(), ACTIVE_STATUSES)
                .stream()
                .map(TokenResponse::from)
                .toList();
    }

    // If two receptionists press "call next" together, both pick the same WAITING token;
    // @Version on Token makes the second update fail, and the handler returns 409.
    @Transactional
    public TokenResponse callNext(Long doctorId) {
        requireDoctor(doctorId);
        LocalDate today = today();
        if (tokenRepository.existsByDoctorIdAndQueueDateAndStatus(doctorId, today, TokenStatus.IN_CONSULTATION)) {
            throw new InvalidTokenTransitionException(
                    "The doctor is still with a patient. Complete or skip that token first.");
        }
        Token next = tokenRepository
                .findFirstByDoctorIdAndQueueDateAndStatusOrderByTokenNumberAsc(doctorId, today, TokenStatus.WAITING)
                .orElseThrow(() -> new ResourceNotFoundException("No patients are waiting for doctor " + doctorId));
        next.callIn(Instant.now(clock));
        return TokenResponse.from(next);
    }

    // The status changes below need no save() call: the token is loaded inside the transaction,
    // so Hibernate notices the change (dirty checking) and writes it on commit, bumping the version.

    @Transactional
    public TokenResponse complete(Long tokenId) {
        Token token = findToken(tokenId);
        token.complete(Instant.now(clock));
        return TokenResponse.from(token);
    }

    @Transactional
    public TokenResponse skip(Long tokenId) {
        Token token = findToken(tokenId);
        token.skip();
        return TokenResponse.from(token);
    }

    @Transactional
    public TokenResponse requeue(Long tokenId) {
        Token token = findToken(tokenId);
        token.requeue();
        return TokenResponse.from(token);
    }

    @Transactional
    public TokenResponse cancel(Long tokenId) {
        Token token = findToken(tokenId);
        token.cancel();
        return TokenResponse.from(token);
    }

    // Simple estimate: people ahead x the doctor's average consultation time.
    // Counts the patient currently inside as one person ahead.
    @Transactional(readOnly = true)
    public TokenPositionResponse getPosition(Long tokenId) {
        Token token = findToken(tokenId);
        Long doctorId = token.getDoctor().getId();
        LocalDate date = token.getQueueDate();

        Integer nowServing = tokenRepository
                .findFirstByDoctorIdAndQueueDateAndStatusOrderByTokenNumberAsc(doctorId, date, TokenStatus.IN_CONSULTATION)
                .map(Token::getTokenNumber)
                .orElse(null);

        long peopleAhead = 0;
        if (token.getStatus() == TokenStatus.WAITING) {
            peopleAhead = tokenRepository.countByDoctorIdAndQueueDateAndStatusAndTokenNumberLessThan(
                    doctorId, date, TokenStatus.WAITING, token.getTokenNumber());
            if (nowServing != null) {
                peopleAhead++;
            }
        }
        long estimatedWaitMinutes = peopleAhead * token.getDoctor().getAvgConsultMinutes();

        return new TokenPositionResponse(token.getId(), token.getTokenNumber(), token.getStatus(),
                nowServing, peopleAhead, estimatedWaitMinutes);
    }

    @Transactional(readOnly = true)
    public List<TokenResponse> getTodayTokensByPhone(String phone) {
        return tokenRepository.findByPatientPhoneAndQueueDateOrderByTokenNumberAsc(phone, today())
                .stream()
                .map(TokenResponse::from)
                .toList();
    }

    private Token findToken(Long tokenId) {
        return tokenRepository.findById(tokenId)
                .orElseThrow(() -> new TokenNotFoundException(tokenId));
    }

    private void requireDoctor(Long doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new DoctorNotFoundException(doctorId);
        }
    }

    private LocalDate today() {
        return LocalDate.now(clock);
    }
}
