package com.phx.ei.common.security;

public enum Role {
    ADMIN;

    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}

