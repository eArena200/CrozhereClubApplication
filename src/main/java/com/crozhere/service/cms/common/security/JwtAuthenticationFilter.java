package com.crozhere.service.cms.common.security;

import com.crozhere.service.cms.auth.service.impl.JwtService;
import com.crozhere.service.cms.common.security.model.AuthErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    private static final List<String> PUBLIC_PATHS = List.of(
            "/auth/",
            "/swagger-ui/",
            "/api-docs/",
            "/clubs/",
            "/booking/availability/"
    );


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();
        if (isPublicPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = extractToken(request);
        log.info("Received TOKEN: {}", token);
        try {
            Claims claims = jwtService.parseToken(token);

            String role = (String) claims.get(JwtService.ROLE_CLAIM_KEY);
            Long userId = Long.parseLong(claims.getSubject());
            Object rawRoleBasedId = claims.get(JwtService.ROLE_BASED_ID);
            Long roleBasedId = rawRoleBasedId instanceof Number ? ((Number) rawRoleBasedId).longValue() : null;

            var authority = new SimpleGrantedAuthority("ROLE_" + role);
            JwtUserPrincipal principal = new JwtUserPrincipal(userId, role, roleBasedId, List.of(authority));

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (ExpiredJwtException e) {
            writeErrorResponse(response, "Token has expired",
                    HttpServletResponse.SC_UNAUTHORIZED, request);
            return;
        } catch (MalformedJwtException e) {
            writeErrorResponse(response, "Malformed token",
                    HttpServletResponse.SC_UNAUTHORIZED, request);
            return;
        } catch (SignatureException | IllegalArgumentException e) {
            log.error("ERROR in JWT FILTER: {}", e.getMessage());
            writeErrorResponse(response, "Invalid token",
                    HttpServletResponse.SC_UNAUTHORIZED, request);
            return;
        } catch (Exception e) {
            log.error("Unexpected JWTFilter error: {}", e.getMessage(), e);
            writeErrorResponse(response, "Authentication failed",
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR, request);
            return;
        }

        filterChain.doFilter(request, response);
    }


    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    private void writeErrorResponse(
            HttpServletResponse response,
            String message,
            int status,
            HttpServletRequest request
    ) throws IOException {
        response.setContentType("application/json");
        response.setStatus(status);

        AuthErrorResponse error = AuthErrorResponse.builder()
                .status(status)
                .message(message)
                .path(request.getRequestURI())
                .timestamp(Date.from(Instant.now()))
                .build();

        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(error));
    }

}
