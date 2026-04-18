package com.ufpb.br.apps4society.my_trace_table_manager.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

@Entity(name = "tb_user_answer")
@Data
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User student;

    @ManyToOne
    @JoinColumn(name = "trace_table_id")
    private TraceTable traceTable;

    @Column(columnDefinition = "TEXT")
    private String answerData;

    private Instant createdAt = Instant.now();
}