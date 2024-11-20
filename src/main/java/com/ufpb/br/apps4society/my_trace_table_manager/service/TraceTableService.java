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
            Long themeId) throws IOException {

        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));
        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new ThemeNotFoundException("Tema não encontrado"));

        String imgPath = handleImageUpload(image);

        TraceTable traceTable = new TraceTable(traceTableRequest, creator);
        traceTable.setImgPath(imgPath);
        traceTable.addTheme(theme);
        theme.addTraceTable(traceTable);

        traceTableRepository.save(traceTable);
        themeRepository.save(theme);

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
        String assetsPath = System.getProperty("user.dir").concat(imageDirectory);
        File destination = new File(assetsPath + fileName);

        image.transferTo(destination);
        return assetsPath + fileName;
    }


    public Page<TraceTableResponse> findAllByUser(Pageable pageable, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        Page<TraceTable> traceTables = traceTableRepository.findByCreator(pageable, user);

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

    public TraceTableResponse updateTraceTable(TraceTableRequest newTraceTable,  Long traceId, Long userId) throws UserNotHavePermissionException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        TraceTable traceTable = traceTableRepository.findById(traceId)
                .orElseThrow(() -> new TraceNotFoundException("Exercício não encontrado"));

        if (user.userNotHavePermission(traceTable.getCreator())) {
            throw new UserNotHavePermissionException("Você não tem permissão de remover este exercício!");
        }

        updateData(newTraceTable, traceTable);

        traceTableRepository.save(traceTable);

        return traceTable.entityToResponse();
    }

    private void updateData(TraceTableRequest newTraceTable, TraceTable traceTable) {
        traceTable.setExerciseName(newTraceTable.exerciseName());
        traceTable.setHeader(newTraceTable.header());
        traceTable.setNumberOfSteps(newTraceTable.numberOfSteps());
        traceTable.setShownTraceTable(TableSerializationUtil.serializeTable(newTraceTable.shownTraceTable()));
        traceTable.setExpectedTraceTable(TableSerializationUtil.serializeTable(newTraceTable.expectedTraceTable()));
    }
}
