package com.ufpb.br.apps4society.my_trace_table_manager.controller.exception;

import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ValidationError extends ErrorResponse{
    private final List<FieldMessage> fieldMessageList = new ArrayList<>();

    public ValidationError(Instant timestamp, Integer code, String message, String path) {
        super(timestamp, code, message, path);
    }

    public void addErro(String field, String message){
        this.fieldMessageList.add(new FieldMessage(field, message));
    }

}
