package com.ufpb.br.apps4society.my_trace_table_manager.service.exception;

public class InvalidUserException extends RuntimeException{
    public InvalidUserException(String message) {
        super(message);
    }
}
