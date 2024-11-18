package com.ufpb.br.apps4society.my_trace_table_manager.controller;

import com.ufpb.br.apps4society.my_trace_table_manager.dto.tracetable.TraceTableRequest;
import com.ufpb.br.apps4society.my_trace_table_manager.dto.tracetable.TraceTableResponse;
import com.ufpb.br.apps4society.my_trace_table_manager.service.TraceTableService;
import com.ufpb.br.apps4society.my_trace_table_manager.service.exception.UserNotHavePermissionException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/v1/trace")
public class TraceTableController {

    private final TraceTableService traceTableService;

    public TraceTableController(TraceTableService traceTableService) {
        this.traceTableService = traceTableService;
    }

    @PostMapping(value = "/{userId}/{themeId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public TraceTableResponse insertTraceTable(
            @RequestBody TraceTableRequest traceTableRequest,
            @PathVariable Long userId,
            @PathVariable Long themeId,
            @RequestPart("imageFile") MultipartFile imageFile) throws IOException {
        return traceTableService.insertTraceTable(traceTableRequest, userId, themeId, imageFile);
    }

    @GetMapping("/user/{userId}")
    public Page<TraceTableResponse> findAllByUser(Pageable pageable, @PathVariable Long userId) {
        return traceTableService.findAllByUser(pageable, userId);
    }

    @GetMapping("/theme/{themeId}")
    public Page<TraceTableResponse> findAllByTheme(Pageable pageable, @PathVariable Long themeId) {
        return traceTableService.findAllByTheme(pageable, themeId);
    }

    @DeleteMapping("/{traceId}")
    public void removeTraceTable(
            @PathVariable Long traceId,
            @RequestParam("userId") Long userId) throws UserNotHavePermissionException {
        traceTableService.removeTraceTable(userId, traceId);
    }

    @PutMapping("/{traceId}/{userId}")
    public TraceTableResponse updateTraceTable(
            @RequestBody TraceTableRequest traceTableRequest,
            @PathVariable Long traceId,
            @PathVariable("userId") Long userId) throws UserNotHavePermissionException {
        return traceTableService.updateTraceTable(traceTableRequest, traceId, userId);
    }
}
