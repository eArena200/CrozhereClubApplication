package com.crozhere.service.cms.auth.controller;

import com.crozhere.service.cms.auth.controller.model.request.InitAuthRequest;
import com.crozhere.service.cms.auth.controller.model.request.VerifyAuthRequest;
import com.crozhere.service.cms.auth.controller.model.response.VerifyAuthResponse;
import com.crozhere.service.cms.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/init")
    public ResponseEntity<Void> initAuth(
            @Valid @RequestBody InitAuthRequest initAuthRequest) {
        authService.initAuth(initAuthRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify")
    public ResponseEntity<VerifyAuthResponse> verifyAuth(
            @Valid @RequestBody VerifyAuthRequest verifyAuthRequest) {
        VerifyAuthResponse response = authService.verifyAuth(verifyAuthRequest);
        return ResponseEntity.ok(response);
    }
}
