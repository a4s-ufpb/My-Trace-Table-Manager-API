package com.ufpb.br.apps4society.my_trace_table_manager.entity;

import com.ufpb.br.apps4society.my_trace_table_manager.dto.tracetable.TraceTableRequest;
import com.ufpb.br.apps4society.my_trace_table_manager.dto.tracetable.TraceTableResponse;
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
    private Integer numberOfSteps;
    private String [][] shownTraceTable;
    private String [][] expectedTraceTable;

    @ManyToOne
    private User creator;

    @ManyToMany
    private List<Theme> themes = new ArrayList<>();

    public TraceTable(TraceTableRequest traceTableRequest, User creator) {
        this.exerciseName = traceTableRequest.exerciseName();
        this.imgPath = traceTableRequest.imgPath();
        this.header = traceTableRequest.header();
        this.numberOfSteps = traceTableRequest.numberOfSteps();
        this.shownTraceTable = traceTableRequest.shownTraceTable();
        this.expectedTraceTable = traceTableRequest.expectedTraceTable();
        this.creator = creator;
    }

    public TraceTableResponse entityToResponse() {
        return new TraceTableResponse(id, exerciseName, imgPath, header, numberOfSteps, shownTraceTable, expectedTraceTable, creator.entityToResponse());
    }

    public void addTheme(Theme theme) {
        if (!themes.contains(theme)) {
            themes.add(theme);
        }

    }
}
