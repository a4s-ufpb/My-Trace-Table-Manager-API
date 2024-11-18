package com.ufpb.br.apps4society.my_trace_table_manager.service.exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String message) {
        super(message);
    }
}
