package com.ufpb.br.apps4society.my_trace_table_manager.controller;


import com.ufpb.br.apps4society.my_trace_table_manager.dto.theme.ThemeRequest;
import com.ufpb.br.apps4society.my_trace_table_manager.dto.theme.ThemeResponse;
import com.ufpb.br.apps4society.my_trace_table_manager.dto.user.UserResponse;
import com.ufpb.br.apps4society.my_trace_table_manager.service.ThemeService;
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

@RestController
@RequestMapping("/v1/theme")
@Tag(name = "Theme", description = "Theme of Trace Table")
public class ThemeController {

    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @Operation(tags = "Theme", summary = "Register Theme", responses ={
            @ApiResponse(description = "Success", responseCode = "201", content = @Content(schema = @Schema(implementation = ThemeResponse.class))),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content()),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content()),
            @ApiResponse(description = "Unauthorized", responseCode = "403", content = @Content())
    } )
    @PostMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ThemeResponse insertTheme(
            @RequestBody ThemeRequest themeRequest,
            @PathVariable Long userId) {
        return themeService.insertTheme(themeRequest, userId);
    }

    @Operation(tags = "Theme", summary = "Find All Themes", responses ={
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = ThemeResponse.class))),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content()),
            @ApiResponse(description = "Unauthorized", responseCode = "403", content = @Content())
    } )
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<ThemeResponse> findAllThemes(Pageable pageable) {
        return themeService.findAllThemes(pageable);
    }

    @Operation(tags = "Theme", summary = "Find Themes By User", responses ={
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = ThemeResponse.class))),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content()),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content())
    } )
    @GetMapping(value = "/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<ThemeResponse> findThemesByUser(
            Pageable pageable,
            @PathVariable Long userId) {
        return themeService.findThemesByUser(pageable, userId);
    }

    @Operation(tags = "Theme", summary = "Delete Theme", responses ={
            @ApiResponse(description = "No Content", responseCode = "204", content = @Content()),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content()),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content()),
            @ApiResponse(description = "Unauthorized", responseCode = "403", content = @Content())
    } )
    @DeleteMapping("/{themeId}")
    public void removeTheme(
            @PathVariable Long themeId,
            @RequestParam Long userId) throws UserNotHavePermissionException {
        themeService.removeTheme(themeId, userId);
    }

    @Operation(tags = "Theme", summary = "Update Theme", responses ={
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = ThemeResponse.class))),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content()),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content()),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content()),
            @ApiResponse(description = "Unauthorized", responseCode = "403", content = @Content())
    } )
    @PutMapping(value = "/{themeId}/{userId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ThemeResponse updateTheme(
            @RequestBody ThemeRequest themeRequest,
            @PathVariable Long themeId,
            @PathVariable Long userId) throws UserNotHavePermissionException {
        return themeService.updateTheme(themeRequest, themeId, userId);
    }
}

