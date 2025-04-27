package com.ufpb.br.apps4society.my_trace_table_manager.dto.user;

import javax.management.relation.Role;

public record UserResponse(
        Long id,
        String name,
        String email,
        String role
) {
}

