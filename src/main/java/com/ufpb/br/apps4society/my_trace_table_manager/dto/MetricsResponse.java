package com.ufpb.br.apps4society.my_trace_table_manager.dto;

public record MetricsResponse(
        int totalSubmissions,
        int correctSubmissions,
        int incorrectSubmissions,
        double accuracy,
        double errorRate
) {
}
