package com.wzzy.library.bfflibrary.security.dto;

import com.wzzy.library.bfflibrary.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticatedUserDTO {
    private String id;
    private String email;
    private String username;
    private Set<Role> roles;
    private boolean active;
    private String authenticatedAt;
}
