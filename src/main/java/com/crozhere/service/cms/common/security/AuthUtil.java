package com.crozhere.service.cms.common.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class AuthUtil {

    public static JwtUserPrincipal getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("AUTH OBJECT: {}", auth.toString());
        if (auth != null && auth.getPrincipal() instanceof JwtUserPrincipal principal) {
            return principal;
        }
        throw new IllegalStateException("No authenticated user found");
    }

    public static Long getUserId() {
        return getCurrentUser().getUserId();
    }

    public static Long getRoleBasedId() {
        return getCurrentUser().getRoleBasedId();
    }

    public static String getRole() {
        return getCurrentUser().getRole();
    }
}
