package com.ufpb.br.apps4society.my_trace_table_manager.repository;

import com.ufpb.br.apps4society.my_trace_table_manager.entity.VerificationCode;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.type.VerificationCodeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, UUID> {

    Optional<VerificationCode> findTopByEmailAndTypeAndUsedFalseOrderByCreatedAtDesc(
            String email, VerificationCodeType type);

    @Modifying
    @Transactional
    @Query("DELETE FROM VerificationCode v WHERE v.email = :email AND v.type = :type")
    void deleteAllByEmailAndType(String email, VerificationCodeType type);
}