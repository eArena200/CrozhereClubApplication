package com.crozhere.service.cms.auth.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    public static final String ROLE_CLAIM_KEY = "role";
    public static final String ROLE_BASED_ID = "role_based_id";


    private static final Integer TOKEN_EXPIRATION_HR = 10;
    private static final String SECRET_KEY = "a61981291d124d2b82ad68a738c1d36323df5sf8df98bdf9d5a7g9";

    public String generateToken(Long userId, Map<String, Object> claims) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .addClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(TOKEN_EXPIRATION_HR, ChronoUnit.HOURS)))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}
