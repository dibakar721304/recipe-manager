package com.manage.recipe.service;

import java.util.Map;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public interface JwtService {
    boolean isTokenValid(String token, UserDetails userDetails);

    String extractUsername(String token);

    String generateToken(Map<String, Object> extraClaims, String username);
}
