package com.ufpb.br.apps4society.my_trace_table_manager.dto.answer;

import java.util.List;

public record AnswerResponse(
        Long id,
        String studentName,
        String exerciseName,
        List<List<String>> tableData,
        String date
) {}