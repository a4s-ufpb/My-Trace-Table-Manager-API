package com.ufpb.br.apps4society.my_trace_table_manager.service;

import com.ufpb.br.apps4society.my_trace_table_manager.dto.tracetable.TraceTableRequest;
import com.ufpb.br.apps4society.my_trace_table_manager.dto.tracetable.TraceTableResponse;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.Theme;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.TraceTable;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.User;
import com.ufpb.br.apps4society.my_trace_table_manager.repository.ThemeRepository;
import com.ufpb.br.apps4society.my_trace_table_manager.repository.TraceTableRepository;
import com.ufpb.br.apps4society.my_trace_table_manager.repository.UserRepository;
import com.ufpb.br.apps4society.my_trace_table_manager.service.exception.*;
import com.ufpb.br.apps4society.my_trace_table_manager.util.TableSerializationUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
public class TraceTableService {
    private final TraceTableRepository traceTableRepository;
    private final UserRepository userRepository;
    private final ThemeRepository themeRepository;
    @Value("${app.img-directory}")
    private String imageDirectory;

    public TraceTableService(TraceTableRepository traceTableRepository, UserRepository userRepository, ThemeRepository themeRepository) {
        this.traceTableRepository = traceTableRepository;
        this.userRepository = userRepository;
        this.themeRepository = themeRepository;
    }

    public TraceTableResponse insertTraceTable(
            TraceTableRequest traceTableRequest,
            MultipartFile image,
            Long userId,
            List<Long> themesIds) throws IOException {

        validateTraceTableRequest(traceTableRequest);

        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));
        List<Theme> themes = themeRepository.findAllById(themesIds);
        if (themes.isEmpty()) {
            throw new ThemeNotFoundException("Nenhum tema encontrado para os IDs fornecidos");
        }

        String imgPath = handleImageUpload(image);

        TraceTable traceTable = new TraceTable(traceTableRequest, creator, themes);
        traceTable.setImgPath(imgPath);

        traceTableRepository.save(traceTable);

        return traceTable.entityToResponse();
    }

    private String handleImageUpload(MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) {
            throw new TraceTableException("Imagem inválida ou vazia");
        }

        File dir = new File(imageDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = image.getOriginalFilename();

        File destination = new File(imageDirectory + fileName);

        image.transferTo(destination);

        return "/assets/" + fileName;
    }



    public Page<TraceTableResponse> findAllByUser(Pageable pageable, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        Page<TraceTable> traceTables = traceTableRepository.findByCreator(pageable, user);

        return traceTables.map(TraceTable::entityToResponse);
    }

    public Page<TraceTableResponse> findAllByTheme(Pageable pageable, Long themeId) {
        themeRepository.findById(themeId)
                .orElseThrow(() -> new UserNotFoundException("Tema não encontrado"));

        Page<TraceTable> traceTables = traceTableRepository.findByThemes_Id(pageable, themeId);

        return traceTables.map(TraceTable::entityToResponse);
    }

    public void removeTraceTable(Long userId, Long traceId) throws UserNotHavePermissionException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        TraceTable traceTable = traceTableRepository.findById(traceId)
                .orElseThrow(() -> new TraceNotFoundException("Exercício não encontrado"));

        if (user.userNotHavePermission(traceTable.getCreator())) {
            throw new UserNotHavePermissionException("Você não tem permissão de remover este exercício!");
        }

        traceTableRepository.delete(traceTable);
    }

    public TraceTableResponse updateTraceTable(TraceTableRequest newTraceTable,  Long traceId, Long userId, List<Long> themesIds) throws UserNotHavePermissionException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        TraceTable traceTable = traceTableRepository.findById(traceId)
                .orElseThrow(() -> new TraceNotFoundException("Exercício não encontrado"));

        if (user.userNotHavePermission(traceTable.getCreator())) {
            throw new UserNotHavePermissionException("Você não tem permissão de remover este exercício!");
        }

        updateData(newTraceTable, traceTable);

        if (themesIds != null && !themesIds.isEmpty()) {
            List<Theme> themes = themeRepository.findAllById(themesIds);
            traceTable.setThemes(themes);
        }

        traceTableRepository.save(traceTable);

        return traceTable.entityToResponse();
    }

    private void updateData(TraceTableRequest newTraceTable, TraceTable traceTable) {
        traceTable.setExerciseName(newTraceTable.exerciseName());
        traceTable.setHeader(TableSerializationUtil.serializeHeader(newTraceTable.header()));
        traceTable.setShownTraceTable(TableSerializationUtil.serializeTable(newTraceTable.shownTraceTable()));
        traceTable.setExpectedTraceTable(TableSerializationUtil.serializeTable(newTraceTable.expectedTraceTable()));
    }

    private void validateTraceTableRequest(TraceTableRequest traceTable) {
        if (Objects.isNull(traceTable.exerciseName()) || traceTable.exerciseName().isBlank()) {
            throw new IllegalArgumentException("Campo exerciseName não pode ser vazio ou nulo");
        }
        if (traceTable.exerciseName().length() < 3 || traceTable.exerciseName().length() > 30) {
            throw new IllegalArgumentException("Campo exerciseName deve ter entre 3 e 30 caracteres");
        }
        if (Objects.isNull(traceTable.header()) || traceTable.header().isEmpty()) {
            throw new IllegalArgumentException("O campo header não pode ser nulo");
        }

        if (Objects.isNull(traceTable.shownTraceTable()) || traceTable.shownTraceTable().isEmpty()) {
            throw new IllegalArgumentException("O campo shownTraceTable não pode ser nulo");
        }

        if (Objects.isNull(traceTable.expectedTraceTable()) || traceTable.expectedTraceTable().isEmpty()) {
            throw new IllegalArgumentException("O campo expectedTraceTable não pode ser nulo");
        }
    }
}
