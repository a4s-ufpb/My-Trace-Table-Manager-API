package com.ufpb.br.apps4society.my_trace_table_manager.dto.theme;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ThemeRequest(
        @NotBlank(message = "Tema não pode ser vazio")
        @Size(min = 2, max = 30, message = "Número de caracteres do tema inválido")
        String name
) {
}
