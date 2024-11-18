package com.ufpb.br.apps4society.my_trace_table_manager.dto.theme;

import com.ufpb.br.apps4society.my_trace_table_manager.dto.user.UserResponse;

public record ThemeResponse(
        Long id,
        String name,
        UserResponse creator
) {
}
