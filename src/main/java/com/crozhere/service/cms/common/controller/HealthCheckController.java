package com.crozhere.service.cms.common.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/monitor")
@Slf4j
public class HealthCheckController {

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        log.info("HealthCheck Called");
        return ResponseEntity.ok("OK");
    }
}

