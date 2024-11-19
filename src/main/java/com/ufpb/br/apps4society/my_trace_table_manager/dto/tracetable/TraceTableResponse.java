package com.ufpb.br.apps4society.my_trace_table_manager.dto.tracetable;

import com.ufpb.br.apps4society.my_trace_table_manager.dto.user.UserResponse;

public record TraceTableResponse(
        Long id,
        String exerciseName,
        String imgPath,
        String [] header,
        Integer numberOfSteps,
        String shownTraceTable,
        String  expectedTraceTable,
        UserResponse creator
) {
}
