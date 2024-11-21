package com.ufpb.br.apps4society.my_trace_table_manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufpb.br.apps4society.my_trace_table_manager.dto.tracetable.TraceTableRequest;
import com.ufpb.br.apps4society.my_trace_table_manager.dto.tracetable.TraceTableResponse;
import com.ufpb.br.apps4society.my_trace_table_manager.service.TraceTableService;
import com.ufpb.br.apps4society.my_trace_table_manager.service.exception.UserNotHavePermissionException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;

@RestController
@RequestMapping("/v1/trace")
@Tag(name = "Trace", description = "Trace Table")
public class TraceTableController {

    private final TraceTableService traceTableService;

    public TraceTableController(TraceTableService traceTableService) {
        this.traceTableService = traceTableService;
    }

    @Operation(tags = "Trace", summary = "Register Trace Table", responses ={
            @ApiResponse(description = "Success", responseCode = "201", content = @Content(schema = @Schema(implementation = TraceTableResponse.class))),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content()),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content()),
            @ApiResponse(description = "Unauthorized", responseCode = "403", content = @Content())
    } )
    @PostMapping(value = "/{userId}/{themeId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public TraceTableResponse insertTraceTable(
            @RequestPart("traceTableRequest") String traceTableRequestJson,
            @RequestPart("image") MultipartFile image,
            @PathVariable Long userId,
            @PathVariable Long themeId) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        TraceTableRequest traceTableRequest = objectMapper.readValue(traceTableRequestJson, TraceTableRequest.class);

        return traceTableService.insertTraceTable(traceTableRequest, image, userId, themeId);
    }

    @Operation(tags = "Trace", summary = "Find All Trace Tables By User", responses ={
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = TraceTableResponse.class))),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content()),
            @ApiResponse(description = "Unauthorized", responseCode = "403", content = @Content())
    } )
    @GetMapping(value = "/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<TraceTableResponse> findAllByUser(Pageable pageable, @PathVariable Long userId) {
        return traceTableService.findAllByUser(pageable, userId);
    }

    @Operation(tags = "Trace", summary = "Delete Trace Table", responses ={
            @ApiResponse(description = "No Content", responseCode = "204", content = @Content()),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content()),
            @ApiResponse(description = "Unauthorized", responseCode = "403", content = @Content()),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content())
    } )
    @DeleteMapping("/{traceId}")
    public void removeTraceTable(
            @PathVariable Long traceId,
            @RequestParam("userId") Long userId) throws UserNotHavePermissionException {
        traceTableService.removeTraceTable(userId, traceId);
    }

    @Operation(tags = "Trace", summary = "Update Trace Table", responses ={
            @ApiResponse(description = "Success", responseCode = "200", content = @Content()),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content()),
            @ApiResponse(description = "Unauthorized", responseCode = "403", content = @Content()),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content()),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content())
    } )
    @PutMapping(value = "/{traceId}/{userId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public TraceTableResponse updateTraceTable(
            @RequestBody TraceTableRequest traceTableRequest,
            @PathVariable Long traceId,
            @PathVariable("userId") Long userId) throws UserNotHavePermissionException {
        return traceTableService.updateTraceTable(traceTableRequest, traceId, userId);
    }
}
