package com.ufpb.br.apps4society.my_trace_table_manager.service.validation;

import java.util.HashMap;
import java.util.Map;

public class CellTypeValidatorFactory {
    
    private static final Map<String, CellTypeValidator> validators = new HashMap<>();

    static {
        register(new StringTypeValidator());
        register(new IntTypeValidator());
        register(new DoubleTypeValidator());
        register(new FloatTypeValidator());
        register(new BooleanTypeValidator());
    }

    public static void register(CellTypeValidator validator) {
        validators.put(validator.getType().toLowerCase(), validator);
    }

    public static CellTypeValidator getValidator(String type) {
        if (type == null) {
            throw new IllegalArgumentException("Tipo não pode ser nulo");
        }
        CellTypeValidator validator = validators.get(type.toLowerCase());
        if (validator == null) {
            throw new IllegalArgumentException("Tipo desconhecido: " + type);
        }
        return validator;
    }
}
