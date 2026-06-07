package com.footballplatform.app.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class LoginRateLimitFilter extends OncePerRequestFilter {

    private final LoginAttemptService loginAttemptService;

    public LoginRateLimitFilter(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!isLoginPostRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String ipAddress = extractClientIp(request);
        String username = request.getParameter("username");

        if (loginAttemptService.isLocked(ipAddress, username)) {
            response.sendRedirect(request.getContextPath() + "/login?locked");
            return;
        }

        if (loginAttemptService.isRateLimited(ipAddress)) {
            response.sendRedirect(request.getContextPath() + "/login?rateLimit");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isLoginPostRequest(HttpServletRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod())
                && "/login".equals(request.getServletPath());
    }

    private String extractClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
