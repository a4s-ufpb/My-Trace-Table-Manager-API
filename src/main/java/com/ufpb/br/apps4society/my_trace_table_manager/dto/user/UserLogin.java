package com.ufpb.br.apps4society.my_trace_table_manager.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserLogin(
        @NotBlank(message = "Email não pode ser vazio")
        @Size(max = 100, message = "Número de caracteres do email inválido")
        @Email(message = "Email inválido")
        String email,
        @NotBlank(message = "Senha não pode ser vazia")
        @Size(min = 8, max = 20, message = "Seu senha precisa ter entre 8-20 caracteres")
        String password) {
}
