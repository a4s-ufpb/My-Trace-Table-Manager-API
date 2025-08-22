package com.ufpb.br.apps4society.my_trace_table_manager.dto.tracetable;

import com.ufpb.br.apps4society.my_trace_table_manager.dto.user.UserResponse;

import java.util.List;

public record TraceTableResponse(
        Long id,
        String exerciseName,
        String imgName,
        String programmingLanguage,
        List<String> header,
        List<List<String>> shownTraceTable,
        List<List<String>>  expectedTraceTable,
        List<List<String>> typeTable,
        UserResponse creator
) {
}
