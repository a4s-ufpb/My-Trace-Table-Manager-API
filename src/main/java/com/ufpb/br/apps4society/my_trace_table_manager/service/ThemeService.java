package com.ufpb.br.apps4society.my_trace_table_manager.service;

import com.ufpb.br.apps4society.my_trace_table_manager.dto.theme.ThemeRequest;
import com.ufpb.br.apps4society.my_trace_table_manager.dto.theme.ThemeResponse;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.Theme;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.User;
import com.ufpb.br.apps4society.my_trace_table_manager.repository.ThemeRepository;
import com.ufpb.br.apps4society.my_trace_table_manager.repository.UserRepository;
import com.ufpb.br.apps4society.my_trace_table_manager.service.exception.ThemeNotFoundException;
import com.ufpb.br.apps4society.my_trace_table_manager.service.exception.UserNotFoundException;
import com.ufpb.br.apps4society.my_trace_table_manager.service.exception.UserNotHavePermissionException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class ThemeService {
    private final ThemeRepository themeRepository;
    private final UserRepository userRepository;

    public ThemeService(ThemeRepository themeRepository, UserRepository userRepository) {
        this.themeRepository = themeRepository;
        this.userRepository = userRepository;
    }

    public ThemeResponse insertTheme(ThemeRequest themeRequest, Long userId) {
        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        Theme theme = new Theme(themeRequest, creator);

        themeRepository.save(theme);

        return theme.entityToResponse();
    }

    public Page<ThemeResponse> findAllThemes(Pageable pageable) {
        Page<Theme> themes = themeRepository.findAll(pageable);
        return themes.map(Theme::entityToResponse);
    }

    public Page<ThemeResponse> findThemesByUser(Pageable pageable, Long userId) {
        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        Page<Theme> themes = themeRepository.findByCreator(pageable, creator);
        return themes.map(Theme::entityToResponse);
    }

    public Page<ThemeResponse> findAllThemesByTraceTable(Pageable pageable, Long traceTableId) {
        Page<Theme> themes = themeRepository.findByTraceTables_Id(pageable, traceTableId);
        return themes.map(Theme::entityToResponse);
    }

    public void removeTheme(Long themeId, Long userId) throws UserNotHavePermissionException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new ThemeNotFoundException("Tema não encontrado!"));

        if (user.userNotHavePermission(theme.getCreator())) {
            throw new UserNotHavePermissionException("Esse usuário não tem permissão para remover esse tema!");
        }

        themeRepository.delete(theme);
    }

    public ThemeResponse updateTheme(ThemeRequest newTheme, Long themeId, Long userId) throws UserNotHavePermissionException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new ThemeNotFoundException("Tema não encontrado!"));

        if (user.userNotHavePermission(theme.getCreator())) {
            throw new UserNotHavePermissionException("Esse usuário não tem permissão para remover esse tema!");
        }

        updateData(newTheme, theme);

        themeRepository.save(theme);

        return theme.entityToResponse();
    }

    private void updateData(ThemeRequest newTheme, Theme theme) {
        theme.setName(newTheme.name());
    }
}
