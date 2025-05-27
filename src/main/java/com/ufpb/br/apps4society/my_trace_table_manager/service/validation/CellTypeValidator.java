package com.ufpb.br.apps4society.my_trace_table_manager.service.validation;

public interface CellTypeValidator {
    boolean isValid(String value);
    String getType();
}
