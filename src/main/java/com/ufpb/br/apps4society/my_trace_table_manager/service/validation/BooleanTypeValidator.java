package com.ufpb.br.apps4society.my_trace_table_manager.service.validation;

public class BooleanTypeValidator implements CellTypeValidator {

    @Override
    public boolean isValid(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
    }

    @Override
    public String getType() {
        return "boolean";
    }
    
}
