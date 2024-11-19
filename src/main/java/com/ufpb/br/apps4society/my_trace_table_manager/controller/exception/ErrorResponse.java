package com.ufpb.br.apps4society.my_trace_table_manager.controller.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ErrorResponse {
    private Instant timestamp;
    private Integer code;
    private String message;
    private String path;
}
