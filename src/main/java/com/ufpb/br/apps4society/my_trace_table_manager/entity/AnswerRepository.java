package com.ufpb.br.apps4society.my_trace_table_manager.entity;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByTraceTableId(Long traceTableId);

    List<Answer> findByTraceTableIdAndCreatedAtBetween(Long traceTableId, LocalDateTime startDate, LocalDateTime endDate);

    List<Answer> findByStudentId(Long studentId);

    @Modifying
    @Transactional
    @Query("DELETE FROM tb_user_answer a WHERE a.traceTable.id = :traceTableId")
    void deleteByTraceTableId(Long traceTableId);
}