package com.phx.ei.admin.dto;

import com.phx.ei.common.security.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class AdminUserResponse {
    private UUID id;
    private String username;
    private Role role;
}
