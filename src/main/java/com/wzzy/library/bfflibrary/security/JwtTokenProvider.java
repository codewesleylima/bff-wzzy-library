package com.wzzy.library.bfflibrary.security;

import com.wzzy.library.bfflibrary.enums.TokenType;
import com.wzzy.library.bfflibrary.security.dto.TokenDTO;

public interface JwtTokenProvider {
    TokenDTO generateToken(String userId, String email);
    TokenDTO generateAccessToken(String userId, String email);
    TokenDTO generateRefreshToken(String userId);
    boolean validateToken(String token);
    String extractUserId(String token);
    String extractEmail(String token);
    long getExpirationTime(String token);
}
