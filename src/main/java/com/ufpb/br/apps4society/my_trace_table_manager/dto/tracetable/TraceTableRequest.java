package com.ufpb.br.apps4society.my_trace_table_manager.dto.tracetable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record TraceTableRequest(
        @NotBlank(message = "Campo exerciseName não pode ser vazio")
        @Size(min = 3, max = 30, message = "Número de caracteres inválido")
         String exerciseName,
         @NotNull(message = "O campo header não pode ser nulo")
         String [] header,
        @NotNull(message = "O campo shownTraceTable não pode ser nulo")
         List<List<String>> shownTraceTable,
        @NotNull(message = "O campo expectedTraceTable não pode ser nulo")
         List<List<String>>  expectedTraceTable
) {
}
