package com.ufpb.br.apps4society.my_trace_table_manager.controller.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FieldMessage {
    private String field;
    private String message;
}
