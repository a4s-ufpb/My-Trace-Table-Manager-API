package com.ufpb.br.apps4society.my_trace_table_manager.dto.tracetable;

public record TraceTableRequest(
         String exerciseName,
         String [] header,
         Integer numberOfSteps,
         String  shownTraceTable,
         String  expectedTraceTable
) {
}
