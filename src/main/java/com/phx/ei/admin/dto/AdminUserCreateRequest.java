package com.phx.ei.admin.dto;

import lombok.Data;

@Data
public class AdminUserCreateRequest {
    private String username;
    private String password;
}
