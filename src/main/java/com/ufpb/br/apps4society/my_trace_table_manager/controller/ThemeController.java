package com.ufpb.br.apps4society.my_trace_table_manager.controller;


import com.ufpb.br.apps4society.my_trace_table_manager.dto.theme.ThemeRequest;
import com.ufpb.br.apps4society.my_trace_table_manager.dto.theme.ThemeResponse;
import com.ufpb.br.apps4society.my_trace_table_manager.service.ThemeService;
import com.ufpb.br.apps4society.my_trace_table_manager.service.exception.UserNotHavePermissionException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/theme")
public class ThemeController {

    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @PostMapping("/{userId}")
    public ThemeResponse insertTheme(
            @RequestBody ThemeRequest themeRequest,
            @PathVariable Long userId) {
        return themeService.insertTheme(themeRequest, userId);
    }

    @GetMapping
    public Page<ThemeResponse> findAllThemes(Pageable pageable) {
        return themeService.findAllThemes(pageable);
    }

    @GetMapping("/user/{userId}")
    public Page<ThemeResponse> findThemesByUser(
            Pageable pageable,
            @PathVariable Long userId) {
        return themeService.findThemesByUser(pageable, userId);
    }

    @DeleteMapping("/{themeId}")
    public void removeTheme(
            @PathVariable Long themeId,
            @RequestParam Long userId) throws UserNotHavePermissionException {
        themeService.removeTheme(themeId, userId);
    }

    @PutMapping("/{themeId}/{userId}")
    public ThemeResponse updateTheme(
            @RequestBody ThemeRequest themeRequest,
            @PathVariable Long themeId,
            @PathVariable Long userId) throws UserNotHavePermissionException {
        return themeService.updateTheme(themeRequest, themeId, userId);
    }
}

