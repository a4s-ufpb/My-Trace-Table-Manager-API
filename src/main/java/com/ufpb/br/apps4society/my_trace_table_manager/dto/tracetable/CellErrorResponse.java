package com.ufpb.br.apps4society.my_trace_table_manager.dto.tracetable;

public record CellErrorResponse(
    int row,
    int column,
    String errorMessage
) {
    
}
