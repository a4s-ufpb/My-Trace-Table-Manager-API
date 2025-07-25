package com.ufpb.br.apps4society.my_trace_table_manager.service.validation;

public class IntTypeValidator implements CellTypeValidator {

    @Override
    public boolean isValid(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String getType() {
        return "int";
    }
    
}
