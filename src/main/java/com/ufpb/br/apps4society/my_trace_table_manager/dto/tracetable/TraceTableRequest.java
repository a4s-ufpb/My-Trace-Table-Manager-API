package com.ufpb.br.apps4society.my_trace_table_manager.dto.tracetable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record TraceTableRequest(
        @NotBlank(message = "Nome do exercício não pode ser vazio")
        @Size(min = 1, max = 30, message = "Número de caracteres do nome do exercício inválido")
         String exerciseName,
         @NotNull(message = "Cabeçalho não pode ser nulo")
         List<String> header,
        @NotNull(message = "Tabela mostrada não pode ser nula")
         List<List<String>> shownTraceTable,
        @NotNull(message = "Tabela esperada não pode ser nula")
         List<List<String>>  expectedTraceTable,
        @NotNull(message = "Tabela de tipos não pode ser nula")
        List<List<String>> typeTable,
        @NotBlank(message = "A linguagem de programação não pode ser vazia")
        String programmingLanguage
) {
}
