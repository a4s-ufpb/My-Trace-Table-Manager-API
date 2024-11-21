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

    private String[] header;

    @Lob
    private String shownTraceTable;

    @Lob
    private String expectedTraceTable;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private User creator;

    @ManyToMany(mappedBy = "traceTables", cascade = CascadeType.ALL)
    private List<Theme> themes = new ArrayList<>();

    public TraceTable(TraceTableRequest traceTableRequest, User creator) {
        this.exerciseName = traceTableRequest.exerciseName();
        this.header = traceTableRequest.header();
        this.shownTraceTable = TableSerializationUtil.serializeTable(traceTableRequest.shownTraceTable());
        this.expectedTraceTable = TableSerializationUtil.serializeTable(traceTableRequest.expectedTraceTable());
        this.creator = creator;
    }

    public TraceTableResponse entityToResponse() {
        return new TraceTableResponse(
                id,
                exerciseName,
                imgPath,
                header,
                TableSerializationUtil.deserializeTable(shownTraceTable),
                TableSerializationUtil.deserializeTable(expectedTraceTable),
                creator.entityToResponse()
        );
    }

    public void addTheme(Theme theme) {
        if (!themes.contains(theme)) {
            themes.add(theme);
        }
    }
}
