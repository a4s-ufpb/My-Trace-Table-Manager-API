package com.ufpb.br.apps4society.my_trace_table_manager.entity;

import com.ufpb.br.apps4society.my_trace_table_manager.dto.theme.ThemeRequest;
import com.ufpb.br.apps4society.my_trace_table_manager.dto.theme.ThemeResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "tb_theme")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Theme implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToOne(cascade = CascadeType.PERSIST)
    private User creator;

    @OneToMany(mappedBy = "theme", cascade = CascadeType.REMOVE)
    private List<TraceTable> traceTables = new ArrayList<>();

    public Theme(ThemeRequest themeRequest, User creator) {
        this.name = themeRequest.name();
        this.creator = creator;
    }

    public ThemeResponse entityToResponse() {
        return new ThemeResponse(id, name, creator.entityToResponse());
    }

    public void addTraceTable(TraceTable traceTable) {
        this.traceTables.add(traceTable);
    }
}
