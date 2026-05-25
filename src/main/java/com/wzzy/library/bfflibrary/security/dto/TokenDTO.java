package com.wzzy.library.bfflibrary.security.dto;

import com.wzzy.library.bfflibrary.enums.TokenType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenDTO {
    private String token;
    private TokenType type;
    private long expiresIn;
    private String issuedAt;
}
