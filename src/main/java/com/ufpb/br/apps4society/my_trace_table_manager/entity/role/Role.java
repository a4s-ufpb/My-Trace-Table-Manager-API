package com.ufpb.br.apps4society.my_trace_table_manager.entity.role;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Role {
    ADMIN("admin"),
    USER("user");

    private String role;

    Role(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    @JsonCreator
    public static Role fromString(String value) {
        return Role.valueOf(value.toUpperCase());
    }
}
