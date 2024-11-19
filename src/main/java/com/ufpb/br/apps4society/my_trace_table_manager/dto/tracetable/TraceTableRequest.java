package com.ufpb.br.apps4society.my_trace_table_manager.dto.tracetable;

import java.util.List;

public record TraceTableRequest(
         String exerciseName,
         String [] header,
         Integer numberOfSteps,
         List<List<String>> shownTraceTable,
         List<List<String>>  expectedTraceTable
) {
}
