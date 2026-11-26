package com.ufpb.br.apps4society.my_trace_table_manager.repository;

import com.ufpb.br.apps4society.my_trace_table_manager.entity.Answer;
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

    List<Answer> findByTraceTableIdAndCreatedAtBetweenAndStudentNameIgnoreCase(Long traceTableId, LocalDateTime createdAtAfter, LocalDateTime createdAtBefore, String studentName);

    List<Answer> findByTraceTableIdAndCreatedAtBetween(Long traceTableId, LocalDateTime createdAtAfter, LocalDateTime createdAtBefore);

    List<Answer> findByStudentNameIgnoreCase(String studentName);

    List<Answer> findByStudentId(Long studentId);

    @Modifying
    @Transactional
    @Query("DELETE FROM tb_user_answer a WHERE a.traceTable.id = :traceTableId")
    void deleteByTraceTableId(Long traceTableId);
}