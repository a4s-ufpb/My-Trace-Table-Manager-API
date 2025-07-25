package com.ufpb.br.apps4society.my_trace_table_manager.service;

import com.ufpb.br.apps4society.my_trace_table_manager.dto.tracetable.CellErrorResponse;
import com.ufpb.br.apps4society.my_trace_table_manager.dto.tracetable.TraceTableRequest;
import com.ufpb.br.apps4society.my_trace_table_manager.dto.tracetable.TraceTableResponse;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.Theme;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.TraceTable;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.User;
import com.ufpb.br.apps4society.my_trace_table_manager.repository.ThemeRepository;
import com.ufpb.br.apps4society.my_trace_table_manager.repository.TraceTableRepository;
import com.ufpb.br.apps4society.my_trace_table_manager.repository.UserRepository;
import com.ufpb.br.apps4society.my_trace_table_manager.service.exception.*;
import com.ufpb.br.apps4society.my_trace_table_manager.service.validation.CellTypeValidator;
import com.ufpb.br.apps4society.my_trace_table_manager.service.validation.CellTypeValidatorFactory;
import com.ufpb.br.apps4society.my_trace_table_manager.util.TableSerializationUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class TraceTableService {
    private static final Logger logger = LoggerFactory.getLogger(MinioService.class);

    private final TraceTableRepository traceTableRepository;
    private final UserRepository userRepository;
    private final ThemeRepository themeRepository;
    private final MinioService minioService;

    public TraceTableService(TraceTableRepository traceTableRepository, UserRepository userRepository,
            ThemeRepository themeRepository, MinioService minioService) {
        this.traceTableRepository = traceTableRepository;
        this.userRepository = userRepository;
        this.themeRepository = themeRepository;
        this.minioService = minioService;
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

        String imgName = handleImageUpload(image);

        TraceTable traceTable = new TraceTable(traceTableRequest, creator, themes);
        traceTable.setImgName(imgName);

        traceTableRepository.save(traceTable);

        return traceTable.entityToResponse(minioService);
    }

    private String handleImageUpload(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new TraceTableException("Imagem inválida ou vazia");
        }

        try {
            return minioService.uploadFile(image);
        } catch (Exception e) {
            throw new TraceTableException("Erro ao enviar imagem para o MinIO");
        }
    }

    public Page<TraceTableResponse> findAllByUser(Pageable pageable, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        Page<TraceTable> traceTables = traceTableRepository.findByCreator(pageable, user);

        return traceTables.map(traceTable -> traceTable.entityToResponse(minioService));
    }

    public Page<TraceTableResponse> findAllByTheme(Pageable pageable, Long themeId) {
        themeRepository.findById(themeId)
                .orElseThrow(() -> new ThemeNotFoundException("Tema não encontrado"));

        Page<TraceTable> traceTables = traceTableRepository.findByThemes_Id(pageable, themeId);

        return traceTables.map(traceTable -> traceTable.entityToResponse(minioService));
    }

    public void removeTraceTable(Long userId, Long traceId) throws UserNotHavePermissionException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        TraceTable traceTable = traceTableRepository.findById(traceId)
                .orElseThrow(() -> new TraceNotFoundException("Exercício não encontrado"));

        if (user.userNotHavePermission(traceTable.getCreator())) {
            throw new UserNotHavePermissionException("Você não tem permissão de remover este exercício!");
        }

        try {
            minioService.deleteObject(traceTable.getImgName());
        } catch (Exception e) {
            logger.warn("Erro ao excluir imagem do MinIO. Prosseguindo com exclusão da TraceTable. Erro: {}",
                    e.getMessage());
        }

        traceTableRepository.delete(traceTable);
    }

    public TraceTableResponse updateTraceTable(TraceTableRequest newTraceTable, Long traceId, Long userId)
            throws UserNotHavePermissionException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        TraceTable traceTable = traceTableRepository.findById(traceId)
                .orElseThrow(() -> new TraceNotFoundException("Exercício não encontrado"));

        if (user.userNotHavePermission(traceTable.getCreator())) {
            throw new UserNotHavePermissionException("Você não tem permissão de remover este exercício!");
        }

        updateData(newTraceTable, traceTable);

        traceTableRepository.save(traceTable);

        return traceTable.entityToResponse(minioService);
    }

    private void updateData(TraceTableRequest newTraceTable, TraceTable traceTable) {
        traceTable.setExerciseName(newTraceTable.exerciseName());
        traceTable.setHeader(TableSerializationUtil.serializeHeader(newTraceTable.header()));
        traceTable.setShownTraceTable(TableSerializationUtil.serializeTable(newTraceTable.shownTraceTable()));
        traceTable.setExpectedTraceTable(TableSerializationUtil.serializeTable(newTraceTable.expectedTraceTable()));
        traceTable.setTypeTable(TableSerializationUtil.serializeTable(newTraceTable.typeTable()));
    }

    public void checkUserAnswer(List<List<String>> userTraceTable, Long traceId) {
        TraceTable traceTable = traceTableRepository.findById(traceId)
                .orElseThrow(() -> new TraceNotFoundException("Exercício não encontrado"));

        List<List<String>> expectedTraceTable = TableSerializationUtil
                .deserializeTable(traceTable.getExpectedTraceTable());
        List<List<String>> typeTable = TableSerializationUtil
                .deserializeTable(traceTable.getTypeTable());

        List<CellErrorResponse> errors = new ArrayList<>();

        for (int i = 0; i < userTraceTable.size(); i++) {
            for (int j = 0; j < userTraceTable.get(i).size(); j++) {
                String userValue = userTraceTable.get(i).get(j);
                String expectedValue = expectedTraceTable.get(i).get(j);
                String cellType = typeTable.get(i).get(j);

                if ("#".equals(expectedValue))
                    continue;

                CellTypeValidator validator;
                try {
                    validator = CellTypeValidatorFactory.getValidator(cellType);
                } catch (IllegalArgumentException e) {
                    throw new TraceTableException("Tipo desconhecido na célula [" + (i + 1) + "][" + (j + 1) + "]");
                }

                if (!validator.isValid(userValue)) {
                    errors.add(new CellErrorResponse(i, j, "Tipo inválido"));
                } else if (!userValue.equals(expectedValue)) {
                    errors.add(new CellErrorResponse(i, j, "Valor incorreto"));
                }
            }
        }

        if (!errors.isEmpty()) {
            throw new TraceTableDetailedException("Célula(s) com erro(s)!", errors);
        }
    }

    private void validateTraceTableRequest(TraceTableRequest traceTable) {
        if (Objects.isNull(traceTable.exerciseName()) || traceTable.exerciseName().isBlank()) {
            throw new IllegalArgumentException("Campo exerciseName não pode ser vazio ou nulo");
        }
        if (traceTable.exerciseName().length() < 1 || traceTable.exerciseName().length() > 30) {
            throw new IllegalArgumentException("Campo exerciseName deve ter entre 3 e 30 caracteres");
        }
        if (Objects.isNull(traceTable.header()) || traceTable.header().isEmpty()) {
            throw new IllegalArgumentException("O campo header não pode ser vazio ou nulo");
        }

        if (Objects.isNull(traceTable.shownTraceTable()) || traceTable.shownTraceTable().isEmpty()) {
            throw new IllegalArgumentException("O campo shownTraceTable não pode ser vazio ou nulo");
        }

        if (Objects.isNull(traceTable.expectedTraceTable()) || traceTable.expectedTraceTable().isEmpty()) {
            throw new IllegalArgumentException("O campo expectedTraceTable não pode ser vazio ou nulo");
        }

        if (Objects.isNull(traceTable.typeTable()) || traceTable.typeTable().isEmpty()) {
            throw new IllegalArgumentException("O campo typeTable não pode ser vazio ou nulo");
        }
    }
}
