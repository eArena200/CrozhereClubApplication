package com.crozhere.service.cms.common.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Data
@AllArgsConstructor
public class JwtUserPrincipal {
    private Long userId;
    private String role;
    private Long roleBasedId;
    private List<? extends GrantedAuthority> authorities;
}

