package com.ufpb.br.apps4society.my_trace_table_manager.repository;

import com.ufpb.br.apps4society.my_trace_table_manager.entity.Theme;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ThemeRepository extends JpaRepository<Theme, Long> {
    Page<Theme> findByCreator(Pageable pageable, User creator);
}
