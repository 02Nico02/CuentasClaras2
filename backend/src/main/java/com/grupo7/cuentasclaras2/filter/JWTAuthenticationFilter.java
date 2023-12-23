package com.grupo7.cuentasclaras2.filter;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupo7.cuentasclaras2.exception.UnauthorizedException;
import com.grupo7.cuentasclaras2.services.TokenBlacklist;
import com.grupo7.cuentasclaras2.services.TokenServices;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTAuthenticationFilter implements Filter {

    private static final String REGISTER_ENDPOINT = "/api/users/register";
    private static final String AUTH_ENDPOINT = "/api/users/auth";
    private static final String LOGOUT_ENDPOINT = "/api/users/logout";
    private static final String OPTIONS_METHOD = "OPTIONS";

    private static final Logger logger = LoggerFactory.getLogger(JWTAuthenticationFilter.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        if (isPublicEndpoint(req.getRequestURI()) || OPTIONS_METHOD.equals(req.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        String token = req.getHeader(HttpHeaders.AUTHORIZATION);

        try {
            if (token == null || TokenBlacklist.isTokenBlacklisted(token) || !TokenServices.validateToken(token)) {
                throw new UnauthorizedException("Token inválido o expirado");
            }

            if (LOGOUT_ENDPOINT.equals(req.getRequestURI())) {
                TokenBlacklist.blacklistToken(token);
                chain.doFilter(request, response);
                return;
            }

            String username = TokenServices.getUsernameFromToken(token);
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        } catch (UnauthorizedException e) {
            logger.error("Error de autorización: {}", e.getMessage());
            res.setStatus(HttpStatus.FORBIDDEN.value());

            res.setContentType("application/json");

            objectMapper.writeValue(res.getWriter(), Map.of("error", "Acceso denegado: " + e.getMessage()));

            return;
        }
    }

    private boolean isPublicEndpoint(String uri) {
        return REGISTER_ENDPOINT.equals(uri) || AUTH_ENDPOINT.equals(uri) || LOGOUT_ENDPOINT.equals(uri);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
