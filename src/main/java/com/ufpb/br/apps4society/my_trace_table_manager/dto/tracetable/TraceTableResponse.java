package com.ufpb.br.apps4society.my_trace_table_manager.dto.tracetable;

import com.ufpb.br.apps4society.my_trace_table_manager.dto.user.UserResponse;

import java.util.List;

public record TraceTableResponse(
        Long id,
        String exerciseName,
        String imgPath,
        List<String> header,
        List<List<String>> shownTraceTable,
        List<List<String>>  expectedTraceTable,
        UserResponse creator
) {
}
