package com.crozhere.service.cms.auth.repository;

import com.crozhere.service.cms.auth.repository.entity.OTP;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<OTP, String> {
    Optional<OTP> findByPhone(String phone);
}
