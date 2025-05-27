package com.ufpb.br.apps4society.my_trace_table_manager.service.validation;

public class StringTypeValidator implements CellTypeValidator {

    @Override
    public boolean isValid(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public String getType() {
        return "string";
    }
    
}
