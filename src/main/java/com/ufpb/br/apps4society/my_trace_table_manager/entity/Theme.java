package com.ufpb.br.apps4society.my_trace_table_manager.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "tb_theme")
@AllArgsConstructor
@NoArgsConstructor@Data
public class Theme implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToOne
    private User creator;

    @ManyToMany
    private List<TraceTable> traceTables = new ArrayList<>();
}
