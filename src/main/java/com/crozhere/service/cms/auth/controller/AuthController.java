package com.crozhere.service.cms.auth.controller;

import com.crozhere.service.cms.auth.controller.model.request.InitAuthRequest;
import com.crozhere.service.cms.auth.controller.model.request.VerifyAuthRequest;
import com.crozhere.service.cms.auth.controller.model.response.VerifyAuthResponse;
import com.crozhere.service.cms.auth.service.AuthService;
import com.crozhere.service.cms.auth.service.exception.AuthServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<Void> initAuth(@RequestBody InitAuthRequest initAuthRequest) {
        try {
            authService.initAuth(initAuthRequest);
            return ResponseEntity.ok().build();
        } catch (AuthServiceException e) {
            log.error("Exception in InitAuth for request: {}", initAuthRequest, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<VerifyAuthResponse> verifyAuth(@RequestBody VerifyAuthRequest verifyAuthRequest) {
        try {
            VerifyAuthResponse response = authService.verifyAuth(verifyAuthRequest);
            return ResponseEntity.ok(response);
        } catch (AuthServiceException e) {
            log.error("Exception in VerifyAuth for request: {}", verifyAuthRequest, e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
