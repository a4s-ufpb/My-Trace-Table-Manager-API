package com.ufpb.br.apps4society.my_trace_table_manager.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class TableSerializationUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String serializeTable(List<List<String>> table) {
        try {
            return objectMapper.writeValueAsString(table);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao serializar a tabela", e);
        }
    }

    public static List<List<String>> deserializeTable(String table) {
        try {
            return objectMapper.readValue(table, List.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao desserializar a tabela", e);
        }
    }
}

