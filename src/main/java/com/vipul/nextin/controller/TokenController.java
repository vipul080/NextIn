package com.vipul.nextin.controller;

import com.vipul.nextin.dto.BookTokenRequest;
import com.vipul.nextin.dto.CancelTokenRequest;
import com.vipul.nextin.dto.TokenPositionResponse;
import com.vipul.nextin.dto.TokenResponse;
import com.vipul.nextin.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TokenController {

    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    // --- Patient side ---

    @PostMapping("/doctors/{doctorId}/tokens")
    public ResponseEntity<TokenResponse> bookToken(@PathVariable Long doctorId,
                                                   @Valid @RequestBody BookTokenRequest request) {
        TokenResponse response = tokenService.bookToken(doctorId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/tokens/{tokenId}/position")
    public ResponseEntity<TokenPositionResponse> getPosition(@PathVariable Long tokenId) {
        return ResponseEntity.ok(tokenService.getPosition(tokenId));
    }

    @GetMapping("/tokens")
    public ResponseEntity<List<TokenResponse>> getTodayTokensByPhone(@RequestParam String phone) {
        return ResponseEntity.ok(tokenService.getTodayTokensByPhone(phone));
    }

    @PostMapping("/tokens/{tokenId}/cancel")
    public ResponseEntity<TokenResponse> cancel(@PathVariable Long tokenId,
                                                @Valid @RequestBody CancelTokenRequest request) {
        return ResponseEntity.ok(tokenService.cancel(tokenId, request.patientPhone()));
    }

    // --- Reception / doctor side ---

    @GetMapping("/doctors/{doctorId}/queue")
    public ResponseEntity<List<TokenResponse>> getTodayQueue(@PathVariable Long doctorId) {
        return ResponseEntity.ok(tokenService.getTodayQueue(doctorId));
    }

    @PostMapping("/doctors/{doctorId}/queue/call-next")
    public ResponseEntity<TokenResponse> callNext(@PathVariable Long doctorId) {
        return ResponseEntity.ok(tokenService.callNext(doctorId));
    }

    @PostMapping("/tokens/{tokenId}/complete")
    public ResponseEntity<TokenResponse> complete(@PathVariable Long tokenId) {
        return ResponseEntity.ok(tokenService.complete(tokenId));
    }

    @PostMapping("/tokens/{tokenId}/skip")
    public ResponseEntity<TokenResponse> skip(@PathVariable Long tokenId) {
        return ResponseEntity.ok(tokenService.skip(tokenId));
    }

    @PostMapping("/tokens/{tokenId}/requeue")
    public ResponseEntity<TokenResponse> requeue(@PathVariable Long tokenId) {
        return ResponseEntity.ok(tokenService.requeue(tokenId));
    }
}
