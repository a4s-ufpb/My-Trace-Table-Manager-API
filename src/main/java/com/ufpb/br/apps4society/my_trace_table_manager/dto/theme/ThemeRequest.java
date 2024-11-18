package com.ufpb.br.apps4society.my_trace_table_manager.dto.theme;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ThemeRequest(
        @NotBlank(message = "Campo name não pode ser vazio")
        @Size(min = 3, max = 30, message = "Número de caracteres inválido")
        String name
) {
}
