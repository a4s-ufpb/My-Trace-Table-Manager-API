package com.ufpb.br.apps4society.my_trace_table_manager.service;

import com.ufpb.br.apps4society.my_trace_table_manager.repository.ThemeRepository;
import org.springframework.stereotype.Service;

@Service
public class ThemeService {
    private final ThemeRepository themeRepository;

    public ThemeService(ThemeRepository themeRepository) {
        this.themeRepository = themeRepository;
    }


}
