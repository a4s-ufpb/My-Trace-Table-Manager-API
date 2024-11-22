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
import com.ufpb.br.apps4society.my_trace_table_manager.util.TableSerializationUtil;

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

    @Column(columnDefinition = "TEXT")
    private String header;

    @Column(columnDefinition = "TEXT")
    private String shownTraceTable;

    @Column(columnDefinition = "TEXT")
    private String expectedTraceTable;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private User creator;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Theme theme;

    public TraceTable(TraceTableRequest traceTableRequest, User creator, Theme theme) {
        this.exerciseName = traceTableRequest.exerciseName();
        this.header = TableSerializationUtil.serializeHeader(traceTableRequest.header());
        this.shownTraceTable = TableSerializationUtil.serializeTable(traceTableRequest.shownTraceTable());
        this.expectedTraceTable = TableSerializationUtil.serializeTable(traceTableRequest.expectedTraceTable());
        this.creator = creator;
        this.theme = theme;
    }

    public TraceTableResponse entityToResponse() {
        return new TraceTableResponse(
                id,
                exerciseName,
                imgPath,
                TableSerializationUtil.deserializeHeader(header),
                TableSerializationUtil.deserializeTable(shownTraceTable),
                TableSerializationUtil.deserializeTable(expectedTraceTable),
                creator.entityToResponse()
        );
    }
}
