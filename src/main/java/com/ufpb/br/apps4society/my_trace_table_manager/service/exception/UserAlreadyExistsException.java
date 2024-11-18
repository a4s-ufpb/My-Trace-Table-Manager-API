package com.ufpb.br.apps4society.my_trace_table_manager.service.exception;

public class UserAlreadyExistsException extends Exception{
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
