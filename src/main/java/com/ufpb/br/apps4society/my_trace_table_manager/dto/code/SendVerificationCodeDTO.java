package com.ufpb.br.apps4society.my_trace_table_manager.dto.code;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SendVerificationCodeDTO(
        @NotBlank @Email String email
) {}