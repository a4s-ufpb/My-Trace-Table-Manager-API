package com.ufpb.br.apps4society.my_trace_table_manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufpb.br.apps4society.my_trace_table_manager.dto.tracetable.CellErrorResponse;
import com.ufpb.br.apps4society.my_trace_table_manager.dto.tracetable.TraceTableRequest;
import com.ufpb.br.apps4society.my_trace_table_manager.dto.tracetable.TraceTableResponse;
import com.ufpb.br.apps4society.my_trace_table_manager.service.TraceTableService;
import com.ufpb.br.apps4society.my_trace_table_manager.service.exception.UserNotHavePermissionException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/v1/trace")
@Tag(name = "Trace", description = "Trace Table")
public class TraceTableController {

        private final TraceTableService traceTableService;

        public TraceTableController(TraceTableService traceTableService) {
                this.traceTableService = traceTableService;
        }

        @Operation(tags = "Trace", summary = "Register Trace Table", responses = {
                        @ApiResponse(description = "Success", responseCode = "201", content = @Content(schema = @Schema(implementation = TraceTableResponse.class))),
                        @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content()),
                        @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content()),
                        @ApiResponse(description = "Unauthorized", responseCode = "403", content = @Content())
        })
        @PostMapping(value = "/{userId}")
        public ResponseEntity<TraceTableResponse> insertTraceTable(
                        @RequestPart("traceTableRequest") String traceTableRequestJson,
                        @RequestPart("image") MultipartFile image,
                        @PathVariable Long userId,
                        @RequestParam List<Long> themesIds) throws IOException {

                ObjectMapper objectMapper = new ObjectMapper();
                TraceTableRequest traceTableRequest = objectMapper.readValue(traceTableRequestJson,
                                TraceTableRequest.class);

                return ResponseEntity.status(201)
                                .body(traceTableService.insertTraceTable(traceTableRequest, image, userId, themesIds));
        }

        @Operation(tags = "Trace", summary = "Find All Trace Tables By User", responses = {
                        @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = TraceTableResponse.class))),
                        @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content()),
                        @ApiResponse(description = "Unauthorized", responseCode = "403", content = @Content())
        })
        @GetMapping(value = "/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<Page<TraceTableResponse>> findAllByUser(
                        @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                        @PathVariable Long userId) {
                return ResponseEntity.ok(traceTableService.findAllByUser(pageable, userId));
        }

        @Operation(tags = "Trace", summary = "Find All Trace Tables By Theme", responses = {
                        @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = TraceTableResponse.class))),
                        @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content()),
                        @ApiResponse(description = "Unauthorized", responseCode = "403", content = @Content())
        })
        @GetMapping(value = "/theme/{themeId}", produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<Page<TraceTableResponse>> findAllByTheme(
                        @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                        @PathVariable Long themeId) {
                return ResponseEntity.ok(traceTableService.findAllByTheme(pageable, themeId));
        }

        @Operation(tags = "Trace", summary = "Delete Trace Table", responses = {
                        @ApiResponse(description = "No Content", responseCode = "204", content = @Content()),
                        @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content()),
                        @ApiResponse(description = "Unauthorized", responseCode = "403", content = @Content()),
                        @ApiResponse(description = "Not Found", responseCode = "404", content = @Content())
        })
        @DeleteMapping("/{traceId}/{userId}")
        public ResponseEntity<Void> removeTraceTable(
                        @PathVariable Long traceId,
                        @PathVariable Long userId) throws UserNotHavePermissionException {
                traceTableService.removeTraceTable(userId, traceId);
                return ResponseEntity.noContent().build();
        }

        @Operation(tags = "Trace", summary = "Update Trace Table", responses = {
                        @ApiResponse(description = "Success", responseCode = "200", content = @Content()),
                        @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content()),
                        @ApiResponse(description = "Unauthorized", responseCode = "403", content = @Content()),
                        @ApiResponse(description = "Not Found", responseCode = "404", content = @Content()),
                        @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content())
        })
        @PutMapping(value = "/{traceId}/{userId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<TraceTableResponse> updateTraceTable(
                        @RequestBody @Valid TraceTableRequest traceTableRequest,
                        @PathVariable Long traceId,
                        @PathVariable Long userId) throws UserNotHavePermissionException {
                return ResponseEntity.ok(traceTableService.updateTraceTable(traceTableRequest, traceId, userId));
        }

        @Operation(tags = "Trace", summary = "Check User Answer", responses = {
                        @ApiResponse(description = "No Content", responseCode = "200", content = @Content()),
                        @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content()),
                        @ApiResponse(description = "Erro nas células da tabela respondida", responseCode = "400", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CellErrorResponse.class))))
        })
        @PostMapping(value = "/check/{traceId}", produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<Void> checkUserAnswer(
                        @PathVariable Long traceId,
                        @RequestBody List<List<String>> userTraceTable) {
                traceTableService.checkUserAnswer(userTraceTable, traceId);
                return ResponseEntity.ok().build();
        }
}
