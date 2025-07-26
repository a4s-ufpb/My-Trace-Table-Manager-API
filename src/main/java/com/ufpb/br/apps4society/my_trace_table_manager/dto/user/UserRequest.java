package com.ufpb.br.apps4society.my_trace_table_manager.dto.user;

import com.ufpb.br.apps4society.my_trace_table_manager.entity.role.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotBlank(message = "Nome não pode ser vazio")
        @Size(min = 3, max = 30, message = "Número de caracteres do nome inválido")
        String name,
        @NotBlank(message = "Email não pode ser vazio")
        @Size(max = 100, message = "Número de caracteres do email inválido")
        @Email(message = "Email inválido")
        String email,
        @NotBlank(message = "Senha não pode ser vazia")
        @Size(min = 8, max = 20, message = "Sua senha precisa ter entre 8-20 caracteres")
        String password,
        @NotNull(message = "Papel do usuário não pode ser nulo")
        Role role
) {
}
