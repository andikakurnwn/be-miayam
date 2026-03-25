package com.example.miayam.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    @Value("${app.security.api-key}")
    private String configuredApiKey;

    @Value("${app.security.api-secret}")
    private String configuredApiSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestApiKey = request.getHeader("X-API-KEY");
        String requestTimestamp = request.getHeader("X-TIMESTAMP");
        String requestSignature = request.getHeader("X-SIGNATURE");

        boolean isValid = false;

        if (requestApiKey != null && requestTimestamp != null && requestSignature != null) {
            if (configuredApiKey.equals(requestApiKey)) {
                try {
                    long timestamp = Long.parseLong(requestTimestamp);
                    long currentTime = System.currentTimeMillis() / 1000L;
                    long timeDifference = Math.abs(currentTime - timestamp);

                    // Allow 5 minutes (300 seconds) time window
                    if (timeDifference <= 300) {
                        String stringToSign = requestApiKey + ":" + requestTimestamp;
                        String expectedSignature = generateHmacSHA256(stringToSign, configuredApiSecret);
                        
                        if (expectedSignature.equalsIgnoreCase(requestSignature)) {
                            isValid = true;
                        }
                    }
                } catch (NumberFormatException ignored) {
                    // Invalid timestamp format
                }
            }
        }

        if (isValid) {
            // Authentication successful
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(requestApiKey, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(auth);
            filterChain.doFilter(request, response);
        } else {
            // Authentication failed
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            String jsonError = "{\"success\": false, \"message\": \"Access Denied: Invalid X-API-KEY, X-TIMESTAMP, or X-SIGNATURE\", \"data\": null}";
            response.getWriter().write(jsonError);
        }
    }

    private String generateHmacSHA256(String data, String key) {
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            javax.crypto.spec.SecretKeySpec secretKeySpec = new javax.crypto.spec.SecretKeySpec(
                    key.getBytes(java.nio.charset.StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(data.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate HMAC-SHA256", e);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !path.startsWith("/api/");
    }
}
