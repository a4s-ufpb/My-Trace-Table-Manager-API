package com.ufpb.br.apps4society.my_trace_table_manager.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "tb_trace_table")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TraceTable implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String exerciseName;
    private String imgPath;
    private String [] header;
    private String [][] shownTraceTable;
    private String [][] expectedTraceTable;

    @ManyToOne
    private User creator;

    @ManyToMany
    private List<Theme> themes = new ArrayList<>();

}
