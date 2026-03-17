package com.phx.ei.admin.service;

import com.phx.ei.admin.dto.LoginRequest;
import com.phx.ei.admin.dto.RegisterRequest;

public interface AuthService {
    String register(RegisterRequest request);
    String login(LoginRequest request);
}
