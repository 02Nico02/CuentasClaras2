package com.grupo7.cuentasclaras2.filter;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // El login y register del usuarios son públicos
        if ("/api/users/register".equals(req.getRequestURI()) || "/api/users/auth".equals(req.getRequestURI())
                || "OPTIONS".equals(req.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        String token = req.getHeader(HttpHeaders.AUTHORIZATION);

        try {
            if (token == null || TokenBlacklist.isTokenBlacklisted(token) || !TokenServices.validateToken(token)) {
                throw new UnauthorizedException("Token inválido o expirado");
            }

            // Si es el logout, poner el token en la blacklist
            if ("/api/users/logout".equals(req.getRequestURI()) || "OPTIONS".equals(req.getMethod())) {
                TokenBlacklist.blacklistToken(token);
                chain.doFilter(request, response);
                return;
            }

            String username = TokenServices.getUsernameFromToken(token);
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        } catch (UnauthorizedException e) {
            res.setStatus(HttpStatus.FORBIDDEN.value());
            res.getWriter().write("Error de autorización: " + e.getMessage());
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
