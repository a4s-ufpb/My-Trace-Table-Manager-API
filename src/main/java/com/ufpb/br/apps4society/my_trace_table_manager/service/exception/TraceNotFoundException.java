package com.ufpb.br.apps4society.my_trace_table_manager.service.exception;

public class TraceNotFoundException extends RuntimeException{

    public TraceNotFoundException(String message) {
        super(message);
    }
}
