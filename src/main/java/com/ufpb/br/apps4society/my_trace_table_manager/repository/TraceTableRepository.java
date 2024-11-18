package com.ufpb.br.apps4society.my_trace_table_manager.repository;

import com.ufpb.br.apps4society.my_trace_table_manager.entity.Theme;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.TraceTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TraceTableRepository extends JpaRepository<TraceTable, Long> {
}
