package com.ufpb.br.apps4society.my_trace_table_manager.service.exception;

import java.util.List;

import com.ufpb.br.apps4society.my_trace_table_manager.dto.tracetable.CellErrorResponse;

public class TraceTableDetailedException extends RuntimeException {

    private final List<CellErrorResponse> ERRORS;

    public TraceTableDetailedException(String message, List<CellErrorResponse> errors) {
        super(message);
        this.ERRORS = errors;
    }

    public List<CellErrorResponse> getErrors() {
        return ERRORS;
    }
}
