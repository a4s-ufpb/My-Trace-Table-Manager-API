package com.ufpb.br.apps4society.my_trace_table_manager.repository;

import com.ufpb.br.apps4society.my_trace_table_manager.entity.Theme;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThemeRepository extends JpaRepository<Theme, Long> {
}
