package com.ufpb.br.apps4society.my_trace_table_manager.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.ufpb.br.apps4society.my_trace_table_manager.dto.tracetable.TraceTableRequest;
import com.ufpb.br.apps4society.my_trace_table_manager.dto.tracetable.TraceTableResponse;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.Theme;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.TraceTable;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.User;
import com.ufpb.br.apps4society.my_trace_table_manager.repository.ThemeRepository;
import com.ufpb.br.apps4society.my_trace_table_manager.repository.TraceTableRepository;
import com.ufpb.br.apps4society.my_trace_table_manager.repository.UserRepository;
import com.ufpb.br.apps4society.my_trace_table_manager.service.exception.ThemeNotFoundException;
import com.ufpb.br.apps4society.my_trace_table_manager.service.exception.TraceNotFoundException;
import com.ufpb.br.apps4society.my_trace_table_manager.service.exception.TraceTableException;
import com.ufpb.br.apps4society.my_trace_table_manager.service.exception.UserNotFoundException;
import com.ufpb.br.apps4society.my_trace_table_manager.service.exception.UserNotHavePermissionException;
import com.ufpb.br.apps4society.my_trace_table_manager.util.TableSerializationUtil;

public class TraceTableServiceTest {

    @Mock
    private TraceTableRepository traceTableRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ThemeRepository themeRepository;

    @Mock
    private MinioService minioService;

    @InjectMocks
    private TraceTableService traceTableService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void insertTraceTableSuccess() throws Exception {
        Long userId = 1L;
        List<Long> themesIds = List.of(10L, 20L);

        User mockUser = mock(User.class);
        Theme mockTheme1 = mock(Theme.class);
        Theme mockTheme2 = mock(Theme.class);
        List<Theme> mockThemes = List.of(mockTheme1, mockTheme2);

        TraceTableRequest mockTraceTableRequest = mock(TraceTableRequest.class);
        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockTraceTableRequest.exerciseName()).thenReturn("Exercício 1");
        when(mockTraceTableRequest.header()).thenReturn(List.of("cabeçalho"));
        when(mockTraceTableRequest.shownTraceTable()).thenReturn(List.of(List.of("valor")));
        when(mockTraceTableRequest.expectedTraceTable()).thenReturn(List.of(List.of("valor")));
        when(mockTraceTableRequest.typeTable()).thenReturn(List.of(List.of("string")));

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(themeRepository.findAllById(themesIds)).thenReturn(mockThemes);
        when(mockFile.isEmpty()).thenReturn(false);
        when(minioService.uploadFile(mockFile)).thenReturn("imageName.png");

        when(minioService.getObjectUrl("imageName.png")).thenReturn("http://mocked-url/imageName.png");

        when(traceTableRepository.save(any(TraceTable.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TraceTableResponse result = traceTableService.insertTraceTable(mockTraceTableRequest, mockFile, userId,
                themesIds);

        assertNotNull(result);
        assertEquals("Exercício 1", result.exerciseName());
        assertEquals("http://mocked-url/imageName.png", result.imgName());
        assertEquals(List.of("cabeçalho"), result.header());
        assertEquals(List.of(List.of("valor")), result.shownTraceTable());
        assertEquals(List.of(List.of("valor")), result.expectedTraceTable());
        assertEquals(List.of(List.of("string")), result.typeTable());
        assertEquals(mockUser.entityToResponse(), result.creator());

        verify(userRepository, times(1)).findById(userId);
        verify(themeRepository, times(1)).findAllById(themesIds);
        verify(minioService, times(1)).uploadFile(mockFile);
        verify(traceTableRepository, times(1)).save(any(TraceTable.class));
    }

    @Test
    void insertTraceTableThrowsExceptionWhenUserNotFound() {
        Long userId = 1L;
        List<Long> themesIds = List.of(10L);

        TraceTableRequest mockTraceTableRequest = mock(TraceTableRequest.class);
        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockTraceTableRequest.exerciseName()).thenReturn("Exercício válido");
        when(mockTraceTableRequest.header()).thenReturn(List.of("header"));
        when(mockTraceTableRequest.shownTraceTable()).thenReturn(List.of(List.of("valor")));
        when(mockTraceTableRequest.expectedTraceTable()).thenReturn(List.of(List.of("valor")));
        when(mockTraceTableRequest.typeTable()).thenReturn(List.of(List.of("string")));

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class,
                () -> traceTableService.insertTraceTable(mockTraceTableRequest, mockFile, userId, themesIds));

        assertEquals("Usuário não encontrado", userNotFoundException.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(themeRepository, minioService, traceTableRepository);
    }

    @Test
    void insertTraceTableThrowsExceptionWhenThemeNotFound() {
        Long userId = 1L;
        List<Long> themesIds = List.of(10L);

        User mockUser = mock(User.class);
        TraceTableRequest mockTraceTableRequest = mock(TraceTableRequest.class);
        MultipartFile mockFile = mock(MultipartFile.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(themeRepository.findAllById(themesIds)).thenReturn(List.of());

        when(mockTraceTableRequest.exerciseName()).thenReturn("Exercício válido");
        when(mockTraceTableRequest.header()).thenReturn(List.of("header"));
        when(mockTraceTableRequest.shownTraceTable()).thenReturn(List.of(List.of("valor")));
        when(mockTraceTableRequest.expectedTraceTable()).thenReturn(List.of(List.of("valor")));
        when(mockTraceTableRequest.typeTable()).thenReturn(List.of(List.of("string")));

        ThemeNotFoundException themeNotFoundException = assertThrows(ThemeNotFoundException.class,
                () -> traceTableService.insertTraceTable(mockTraceTableRequest, mockFile, userId, themesIds));

        assertEquals("Nenhum tema encontrado para os IDs fornecidos", themeNotFoundException.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(themeRepository, times(1)).findAllById(themesIds);
        verifyNoInteractions(minioService, traceTableRepository);
    }

    @Test
    void insertTraceTableThrowsExceptionWhenImageEmpty() {
        Long userId = 1L;
        List<Long> themesIds = List.of(10L);

        User mockUser = mock(User.class);
        TraceTableRequest mockTraceTableRequest = mock(TraceTableRequest.class);
        MultipartFile mockFile = mock(MultipartFile.class);
        Theme mockTheme = mock(Theme.class);
        List<Theme> mockThemes = List.of(mockTheme);

        when(mockTraceTableRequest.exerciseName()).thenReturn("Exercício válido");
        when(mockTraceTableRequest.header()).thenReturn(List.of("header"));
        when(mockTraceTableRequest.shownTraceTable()).thenReturn(List.of(List.of("valor")));
        when(mockTraceTableRequest.expectedTraceTable()).thenReturn(List.of(List.of("valor")));
        when(mockTraceTableRequest.typeTable()).thenReturn(List.of(List.of("string")));

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(themeRepository.findAllById(themesIds)).thenReturn(mockThemes);
        when(mockFile.isEmpty()).thenReturn(true);

        TraceTableException traceTableException = assertThrows(TraceTableException.class,
                () -> traceTableService.insertTraceTable(mockTraceTableRequest, mockFile, userId, themesIds));

        assertEquals("Imagem inválida ou vazia", traceTableException.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(themeRepository, times(1)).findAllById(themesIds);
        verifyNoInteractions(minioService, traceTableRepository);
    }

    @Test
    void insertTraceTableThrowsExceptionWhenUploadFails() throws Exception {
        Long userId = 1L;
        List<Long> themesIds = List.of(10L);

        User mockUser = mock(User.class);
        TraceTableRequest mockTraceTableRequest = mock(TraceTableRequest.class);
        MultipartFile mockFile = mock(MultipartFile.class);
        Theme mockTheme = mock(Theme.class);
        List<Theme> mockThemes = List.of(mockTheme);

        when(mockTraceTableRequest.exerciseName()).thenReturn("Exercício válido");
        when(mockTraceTableRequest.header()).thenReturn(List.of("header"));
        when(mockTraceTableRequest.shownTraceTable()).thenReturn(List.of(List.of("valor")));
        when(mockTraceTableRequest.expectedTraceTable()).thenReturn(List.of(List.of("valor")));
        when(mockTraceTableRequest.typeTable()).thenReturn(List.of(List.of("string")));

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(themeRepository.findAllById(themesIds)).thenReturn(mockThemes);
        when(mockFile.isEmpty()).thenReturn(false);

        doThrow(new RuntimeException("Erro MinIO")).when(minioService).uploadFile(mockFile);

        TraceTableException traceTableException = assertThrows(TraceTableException.class,
                () -> traceTableService.insertTraceTable(mockTraceTableRequest, mockFile, userId, themesIds));

        assertEquals("Erro ao enviar imagem para o MinIO", traceTableException.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(themeRepository, times(1)).findAllById(themesIds);
        verify(minioService, times(1)).uploadFile(mockFile);
        verifyNoInteractions(traceTableRepository);
    }

    @Test
    void insertTraceTableThrowsExceptionWhenExerciseNameIsNull() {
        Long userId = 1L;
        List<Long> themesIds = List.of(10L);

        User mockUser = mock(User.class);
        Theme mockTheme = mock(Theme.class);
        List<Theme> mockThemes = List.of(mockTheme);

        TraceTableRequest mockRequest = mock(TraceTableRequest.class);
        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockRequest.exerciseName()).thenReturn(null);
        when(mockRequest.header()).thenReturn(List.of("cabeçalho"));
        when(mockRequest.shownTraceTable()).thenReturn(List.of(List.of("valor")));
        when(mockRequest.expectedTraceTable()).thenReturn(List.of(List.of("valor")));
        when(mockRequest.typeTable()).thenReturn(List.of(List.of("string")));

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(themeRepository.findAllById(themesIds)).thenReturn(mockThemes);
        when(mockFile.isEmpty()).thenReturn(false);

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> traceTableService.insertTraceTable(mockRequest, mockFile, userId, themesIds));

        assertEquals("Campo exerciseName não pode ser vazio ou nulo", illegalArgumentException.getMessage());

        verifyNoInteractions(minioService, traceTableRepository);
    }

    @Test
    void insertTraceTableThrowsExceptionWhenExerciseNameTooShort() {
        Long userId = 1L;
        List<Long> themesIds = List.of(10L);

        User mockUser = mock(User.class);
        Theme mockTheme = mock(Theme.class);
        List<Theme> mockThemes = List.of(mockTheme);

        TraceTableRequest mockRequest = mock(TraceTableRequest.class);
        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockRequest.exerciseName()).thenReturn("ab");
        when(mockRequest.header()).thenReturn(List.of("cabeçalho"));
        when(mockRequest.shownTraceTable()).thenReturn(List.of(List.of("valor")));
        when(mockRequest.expectedTraceTable()).thenReturn(List.of(List.of("valor")));
        when(mockRequest.typeTable()).thenReturn(List.of(List.of("string")));

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(themeRepository.findAllById(themesIds)).thenReturn(mockThemes);
        when(mockFile.isEmpty()).thenReturn(false);

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> traceTableService.insertTraceTable(mockRequest, mockFile, userId, themesIds));

        assertEquals("Campo exerciseName deve ter entre 3 e 30 caracteres", illegalArgumentException.getMessage());

        verifyNoInteractions(minioService, traceTableRepository);
    }

    @Test
    void insertTraceTableThrowsExceptionWhenExerciseNameTooLong() {
        Long userId = 1L;
        List<Long> themesIds = List.of(10L);

        User mockUser = mock(User.class);
        Theme mockTheme = mock(Theme.class);
        List<Theme> mockThemes = List.of(mockTheme);

        TraceTableRequest mockRequest = mock(TraceTableRequest.class);
        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockRequest.exerciseName()).thenReturn("abcdefghijklmnopqrstuvwxyzaeiou");
        when(mockRequest.header()).thenReturn(List.of("cabeçalho"));
        when(mockRequest.shownTraceTable()).thenReturn(List.of(List.of("valor")));
        when(mockRequest.expectedTraceTable()).thenReturn(List.of(List.of("valor")));
        when(mockRequest.typeTable()).thenReturn(List.of(List.of("string")));

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(themeRepository.findAllById(themesIds)).thenReturn(mockThemes);
        when(mockFile.isEmpty()).thenReturn(false);

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> traceTableService.insertTraceTable(mockRequest, mockFile, userId, themesIds));

        assertEquals("Campo exerciseName deve ter entre 3 e 30 caracteres", illegalArgumentException.getMessage());

        verifyNoInteractions(minioService, traceTableRepository);
    }

    @Test
    void insertTraceTableThrowsExceptionWhenHeaderIsNull() {
        Long userId = 1L;
        List<Long> themesIds = List.of(10L);

        User mockUser = mock(User.class);
        Theme mockTheme = mock(Theme.class);
        List<Theme> mockThemes = List.of(mockTheme);

        TraceTableRequest mockRequest = mock(TraceTableRequest.class);
        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockRequest.exerciseName()).thenReturn("Exercício válido");
        when(mockRequest.header()).thenReturn(null);
        when(mockRequest.shownTraceTable()).thenReturn(List.of(List.of("valor")));
        when(mockRequest.expectedTraceTable()).thenReturn(List.of(List.of("valor")));
        when(mockRequest.typeTable()).thenReturn(List.of(List.of("string")));

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(themeRepository.findAllById(themesIds)).thenReturn(mockThemes);
        when(mockFile.isEmpty()).thenReturn(false);

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> traceTableService.insertTraceTable(mockRequest, mockFile, userId, themesIds));

        assertEquals("O campo header não pode ser vazio ou nulo", illegalArgumentException.getMessage());

        verifyNoInteractions(minioService, traceTableRepository);
    }

     @Test
    void insertTraceTableThrowsExceptionWhenHeaderIsEmpty() {
        Long userId = 1L;
        List<Long> themesIds = List.of(10L);

        User mockUser = mock(User.class);
        Theme mockTheme = mock(Theme.class);
        List<Theme> mockThemes = List.of(mockTheme);

        TraceTableRequest mockRequest = mock(TraceTableRequest.class);
        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockRequest.exerciseName()).thenReturn("Exercício válido");
        when(mockRequest.header()).thenReturn(List.of());
        when(mockRequest.shownTraceTable()).thenReturn(List.of(List.of("valor")));
        when(mockRequest.expectedTraceTable()).thenReturn(List.of(List.of("valor")));
        when(mockRequest.typeTable()).thenReturn(List.of(List.of("string")));

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(themeRepository.findAllById(themesIds)).thenReturn(mockThemes);
        when(mockFile.isEmpty()).thenReturn(false);

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> traceTableService.insertTraceTable(mockRequest, mockFile, userId, themesIds));

        assertEquals("O campo header não pode ser vazio ou nulo", illegalArgumentException.getMessage());

        verifyNoInteractions(minioService, traceTableRepository);
    }

    @Test
    void insertTraceTableThrowsExceptionWhenShownTraceTableIsNull() {
        Long userId = 1L;
        List<Long> themesIds = List.of(10L);

        User mockUser = mock(User.class);
        Theme mockTheme = mock(Theme.class);
        List<Theme> mockThemes = List.of(mockTheme);

        TraceTableRequest mockRequest = mock(TraceTableRequest.class);
        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockRequest.exerciseName()).thenReturn("Exercício válido");
        when(mockRequest.header()).thenReturn(List.of("cabeçalho"));
        when(mockRequest.shownTraceTable()).thenReturn(null);
        when(mockRequest.expectedTraceTable()).thenReturn(List.of(List.of("valor")));
        when(mockRequest.typeTable()).thenReturn(List.of(List.of("string")));

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(themeRepository.findAllById(themesIds)).thenReturn(mockThemes);
        when(mockFile.isEmpty()).thenReturn(false);

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> traceTableService.insertTraceTable(mockRequest, mockFile, userId, themesIds));

        assertEquals("O campo shownTraceTable não pode ser vazio ou nulo", illegalArgumentException.getMessage());

        verifyNoInteractions(minioService, traceTableRepository);
    }

    @Test
    void insertTraceTableThrowsExceptionWhenShownTraceTableIsEmpty() {
        Long userId = 1L;
        List<Long> themesIds = List.of(10L);

        User mockUser = mock(User.class);
        Theme mockTheme = mock(Theme.class);
        List<Theme> mockThemes = List.of(mockTheme);

        TraceTableRequest mockRequest = mock(TraceTableRequest.class);
        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockRequest.exerciseName()).thenReturn("Exercício válido");
        when(mockRequest.header()).thenReturn(List.of("cabeçalho"));
        when(mockRequest.shownTraceTable()).thenReturn(List.of());
        when(mockRequest.expectedTraceTable()).thenReturn(List.of(List.of("valor")));
        when(mockRequest.typeTable()).thenReturn(List.of(List.of("string")));

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(themeRepository.findAllById(themesIds)).thenReturn(mockThemes);
        when(mockFile.isEmpty()).thenReturn(false);

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> traceTableService.insertTraceTable(mockRequest, mockFile, userId, themesIds));

        assertEquals("O campo shownTraceTable não pode ser vazio ou nulo", illegalArgumentException.getMessage());

        verifyNoInteractions(minioService, traceTableRepository);
    }

    @Test
    void insertTraceTableThrowsExceptionWhenExpectedTraceTableIsNull() {
        Long userId = 1L;
        List<Long> themesIds = List.of(10L);

        User mockUser = mock(User.class);
        Theme mockTheme = mock(Theme.class);
        List<Theme> mockThemes = List.of(mockTheme);

        TraceTableRequest mockRequest = mock(TraceTableRequest.class);
        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockRequest.exerciseName()).thenReturn("Exercício válido");
        when(mockRequest.header()).thenReturn(List.of("cabeçalho"));
        when(mockRequest.shownTraceTable()).thenReturn(List.of(List.of("valor")));
        when(mockRequest.expectedTraceTable()).thenReturn(null);
        when(mockRequest.typeTable()).thenReturn(List.of(List.of("string")));

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(themeRepository.findAllById(themesIds)).thenReturn(mockThemes);
        when(mockFile.isEmpty()).thenReturn(false);

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> traceTableService.insertTraceTable(mockRequest, mockFile, userId, themesIds));

        assertEquals("O campo expectedTraceTable não pode ser vazio ou nulo", illegalArgumentException.getMessage());

        verifyNoInteractions(minioService, traceTableRepository);
    }

    @Test
    void insertTraceTableThrowsExceptionWhenExpectedTraceTableIsEmpty() {
        Long userId = 1L;
        List<Long> themesIds = List.of(10L);

        User mockUser = mock(User.class);
        Theme mockTheme = mock(Theme.class);
        List<Theme> mockThemes = List.of(mockTheme);

        TraceTableRequest mockRequest = mock(TraceTableRequest.class);
        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockRequest.exerciseName()).thenReturn("Exercício válido");
        when(mockRequest.header()).thenReturn(List.of("cabeçalho"));
        when(mockRequest.shownTraceTable()).thenReturn(List.of(List.of("valor")));
        when(mockRequest.expectedTraceTable()).thenReturn(List.of());
        when(mockRequest.typeTable()).thenReturn(List.of(List.of("string")));

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(themeRepository.findAllById(themesIds)).thenReturn(mockThemes);
        when(mockFile.isEmpty()).thenReturn(false);

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> traceTableService.insertTraceTable(mockRequest, mockFile, userId, themesIds));

        assertEquals("O campo expectedTraceTable não pode ser vazio ou nulo", illegalArgumentException.getMessage());

        verifyNoInteractions(minioService, traceTableRepository);
    }

    @Test
    void insertTraceTableThrowsExceptionWhenTypeTableIsNull() {
        Long userId = 1L;
        List<Long> themesIds = List.of(10L);

        User mockUser = mock(User.class);
        Theme mockTheme = mock(Theme.class);
        List<Theme> mockThemes = List.of(mockTheme);

        TraceTableRequest mockRequest = mock(TraceTableRequest.class);
        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockRequest.exerciseName()).thenReturn("Exercício válido");
        when(mockRequest.header()).thenReturn(List.of("cabeçalho"));
        when(mockRequest.shownTraceTable()).thenReturn(List.of(List.of("valor")));
        when(mockRequest.expectedTraceTable()).thenReturn(List.of(List.of("valor")));
        when(mockRequest.typeTable()).thenReturn(null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(themeRepository.findAllById(themesIds)).thenReturn(mockThemes);
        when(mockFile.isEmpty()).thenReturn(false);

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> traceTableService.insertTraceTable(mockRequest, mockFile, userId, themesIds));

        assertEquals("O campo typeTable não pode ser vazio ou nulo", illegalArgumentException.getMessage());

        verifyNoInteractions(minioService, traceTableRepository);
    }

    @Test
    void insertTraceTableThrowsExceptionWhenTypeTableIsEmpty() {
        Long userId = 1L;
        List<Long> themesIds = List.of(10L);

        User mockUser = mock(User.class);
        Theme mockTheme = mock(Theme.class);
        List<Theme> mockThemes = List.of(mockTheme);

        TraceTableRequest mockRequest = mock(TraceTableRequest.class);
        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockRequest.exerciseName()).thenReturn("Exercício válido");
        when(mockRequest.header()).thenReturn(List.of("cabeçalho"));
        when(mockRequest.shownTraceTable()).thenReturn(List.of(List.of("valor")));
        when(mockRequest.expectedTraceTable()).thenReturn(List.of(List.of("valor")));
        when(mockRequest.typeTable()).thenReturn(List.of());

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(themeRepository.findAllById(themesIds)).thenReturn(mockThemes);
        when(mockFile.isEmpty()).thenReturn(false);

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> traceTableService.insertTraceTable(mockRequest, mockFile, userId, themesIds));

        assertEquals("O campo typeTable não pode ser vazio ou nulo", illegalArgumentException.getMessage());

        verifyNoInteractions(minioService, traceTableRepository);
    }

    @Test
    void checkUserAnswerSuccess() {
        Long traceId = 1L;

        TraceTable mockTraceTable = mock(TraceTable.class);

        String expectedSerialized = TableSerializationUtil.serializeTable(List.of(List.of("10")));
        String typeSerialized = TableSerializationUtil.serializeTable(List.of(List.of("int")));

        when(traceTableRepository.findById(traceId)).thenReturn(Optional.of(mockTraceTable));
        when(mockTraceTable.getExpectedTraceTable()).thenReturn(expectedSerialized);
        when(mockTraceTable.getTypeTable()).thenReturn(typeSerialized);

        List<List<String>> userAnswer = List.of(List.of("10"));

        assertDoesNotThrow(() -> traceTableService.checkUserAnswer(userAnswer, traceId));
    }

    @Test
    void checkUserAnswerThrowsOnWrongType() {
        Long traceId = 1L;

        TraceTable mockTraceTable = mock(TraceTable.class);

        String expectedSerialized = TableSerializationUtil.serializeTable(List.of(List.of("10")));
        String typeSerialized = TableSerializationUtil.serializeTable(List.of(List.of("int")));

        when(traceTableRepository.findById(traceId)).thenReturn(Optional.of(mockTraceTable));
        when(mockTraceTable.getExpectedTraceTable()).thenReturn(expectedSerialized);
        when(mockTraceTable.getTypeTable()).thenReturn(typeSerialized);

        List<List<String>> userAnswer = List.of(List.of("abc"));

        TraceTableException exception = assertThrows(TraceTableException.class,
                () -> traceTableService.checkUserAnswer(userAnswer, traceId));

        assertTrue(exception.getMessage().contains("Erro de tipo"));
    }

    @Test
    void checkUserAnswerThrowsOnWrongValue() {
        Long traceId = 1L;

        TraceTable mockTraceTable = mock(TraceTable.class);

        String expectedSerialized = TableSerializationUtil.serializeTable(List.of(List.of("10")));
        String typeSerialized = TableSerializationUtil.serializeTable(List.of(List.of("int")));

        when(traceTableRepository.findById(traceId)).thenReturn(Optional.of(mockTraceTable));
        when(mockTraceTable.getExpectedTraceTable()).thenReturn(expectedSerialized);
        when(mockTraceTable.getTypeTable()).thenReturn(typeSerialized);

        List<List<String>> userAnswer = List.of(List.of("20"));

        TraceTableException exception = assertThrows(TraceTableException.class,
                () -> traceTableService.checkUserAnswer(userAnswer, traceId));

        assertTrue(exception.getMessage().contains("Valor incorreto"));
    }

    @Test
    void checkUserAnswerThrowsWhenTraceNotFound() {
        Long traceId = 1L;

        when(traceTableRepository.findById(traceId)).thenReturn(Optional.empty());

        List<List<String>> userAnswer = List.of(List.of("10"));

        TraceNotFoundException traceNotFoundException = assertThrows(TraceNotFoundException.class,
                () -> traceTableService.checkUserAnswer(userAnswer, traceId));

        assertEquals("Exercício não encontrado", traceNotFoundException.getMessage());
    }

    @Test
    void findAllByUserSuccess() {
        Long userId = 1L;

        User mockUser = mock(User.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        TraceTable mockTraceTable = mock(TraceTable.class);
        TraceTableResponse mockTraceTableResponse = mock(TraceTableResponse.class);

        Pageable pageable = PageRequest.of(0, 10);
        Page<TraceTable> mockPage = new PageImpl<>(List.of(mockTraceTable), pageable, 1);

        when(traceTableRepository.findByCreator(pageable, mockUser)).thenReturn(mockPage);

        when(mockTraceTable.entityToResponse(minioService)).thenReturn(mockTraceTableResponse);

        Page<TraceTableResponse> resultPage = traceTableService.findAllByUser(pageable, userId);

        assertNotNull(resultPage);
        assertEquals(1, resultPage.getTotalElements());
        assertEquals(mockTraceTableResponse, resultPage.getContent().get(0));

        verify(userRepository, times(1)).findById(userId);
        verify(traceTableRepository, times(1)).findByCreator(pageable, mockUser);
        verify(mockTraceTable, times(1)).entityToResponse(minioService);
    }

    @Test
    void findAllByUserThrowsExceptionWhenUserNotFound() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Pageable pageable = PageRequest.of(0, 10);
        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class,
                () -> traceTableService.findAllByUser(pageable, userId));

        assertEquals("Usuário não encontrado", userNotFoundException.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void findAllByThemeSuccess() {
        Long themeId = 1L;

        Theme mockTheme = mock(Theme.class);
        when(themeRepository.findById(themeId)).thenReturn(Optional.of(mockTheme));

        TraceTable mockTraceTable = mock(TraceTable.class);
        TraceTableResponse mockTraceTableResponse = mock(TraceTableResponse.class);
        Pageable pageable = PageRequest.of(0, 10);
        Page<TraceTable> mockPage = new PageImpl<>(List.of(mockTraceTable), pageable, 1);

        when(traceTableRepository.findByThemes_Id(pageable, themeId)).thenReturn(mockPage);

        when(mockTraceTable.entityToResponse(minioService)).thenReturn(mockTraceTableResponse);

        Page<TraceTableResponse> resultPage = traceTableService.findAllByTheme(pageable, themeId);

        assertNotNull(resultPage);
        assertEquals(1, resultPage.getTotalElements());
        assertEquals(mockTraceTableResponse, resultPage.getContent().get(0));

        verify(themeRepository, times(1)).findById(themeId);
        verify(traceTableRepository, times(1)).findByThemes_Id(pageable, themeId);
        verify(mockTraceTable, times(1)).entityToResponse(minioService);
    }

    @Test
    void findAllByThemeThrowsExceptionWhenThemeNotFound() {
        Long themeId = 1L;

        when(themeRepository.findById(themeId)).thenReturn(Optional.empty());
        Pageable pageable = PageRequest.of(0, 10);

        ThemeNotFoundException themeNotFoundException = assertThrows(ThemeNotFoundException.class,
                () -> traceTableService.findAllByTheme(pageable, themeId));

        assertEquals("Tema não encontrado", themeNotFoundException.getMessage());

        verify(themeRepository, times(1)).findById(themeId);
    }

    @Test
    void removeTraceTableSuccess() throws Exception {
        Long userId = 1L;
        Long traceId = 1L;

        User mockUser = mock(User.class);
        TraceTable mockTraceTable = mock(TraceTable.class);
        User mockCreator = mock(User.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(traceTableRepository.findById(traceId)).thenReturn(Optional.of(mockTraceTable));
        when(mockTraceTable.getCreator()).thenReturn(mockCreator);

        when(mockUser.userNotHavePermission(mockCreator)).thenReturn(false);

        when(mockTraceTable.getImgName()).thenReturn("imagem.png");

        doNothing().when(minioService).deleteObject("imagem.png");
        doNothing().when(traceTableRepository).delete(mockTraceTable);

        traceTableService.removeTraceTable(userId, traceId);

        verify(userRepository, times(1)).findById(userId);
        verify(traceTableRepository, times(1)).findById(traceId);
        verify(minioService, times(1)).deleteObject("imagem.png");
        verify(traceTableRepository, times(1)).delete(mockTraceTable);
    }

    @Test
    void removeTraceTableThrowsExceptionWhenUserNotFound() {
        Long userId = 1L;
        Long traceId = 2L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class,
                () -> traceTableService.removeTraceTable(userId, traceId));

        assertEquals("Usuário não encontrado", userNotFoundException.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(traceTableRepository, minioService);
    }

    @Test
    void removeTraceTableThrowsExceptionWhenTraceTableNotFound() {
        Long userId = 1L;
        Long traceId = 2L;

        User mockUser = mock(User.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(traceTableRepository.findById(traceId)).thenReturn(Optional.empty());

        TraceNotFoundException traceNotFoundException = assertThrows(TraceNotFoundException.class,
                () -> traceTableService.removeTraceTable(userId, traceId));

        assertEquals("Exercício não encontrado", traceNotFoundException.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(traceTableRepository, times(1)).findById(traceId);
        verifyNoInteractions(minioService);
    }

    @Test
    void removeTraceTableThrowsExceptionWhenUserNotHavePermission() {
        Long userId = 1L;
        Long traceId = 2L;

        User mockUser = mock(User.class);
        TraceTable mockTraceTable = mock(TraceTable.class);
        User mockCreator = mock(User.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(traceTableRepository.findById(traceId)).thenReturn(Optional.of(mockTraceTable));
        when(mockTraceTable.getCreator()).thenReturn(mockCreator);

        when(mockUser.userNotHavePermission(mockCreator)).thenReturn(true);

        UserNotHavePermissionException userNotHavePermissionException = assertThrows(
                UserNotHavePermissionException.class, () -> traceTableService.removeTraceTable(userId, traceId));

        assertEquals("Você não tem permissão de remover este exercício!", userNotHavePermissionException.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(traceTableRepository, times(1)).findById(traceId);
        verifyNoInteractions(minioService);
    }

    @Test
    void removeTraceTableThrowsExceptionWhenMinioServiceFails() throws Exception {
        Long userId = 1L;
        Long traceId = 2L;

        User mockUser = mock(User.class);
        TraceTable mockTraceTable = mock(TraceTable.class);
        User mockCreator = mock(User.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(traceTableRepository.findById(traceId)).thenReturn(Optional.of(mockTraceTable));
        when(mockTraceTable.getCreator()).thenReturn(mockCreator);

        when(mockUser.userNotHavePermission(mockCreator)).thenReturn(false);

        when(mockTraceTable.getImgName()).thenReturn("imagem.png");

        doThrow(new RuntimeException("Erro MinIO")).when(minioService).deleteObject("imagem.png");

        TraceTableException traceTableException = assertThrows(TraceTableException.class,
                () -> traceTableService.removeTraceTable(userId, traceId));

        assertEquals("Erro ao remover imagem do MinIO", traceTableException.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(traceTableRepository, times(1)).findById(traceId);
        verify(minioService, times(1)).deleteObject("imagem.png");
        verify(traceTableRepository, never()).delete(any());
    }

    @Test
    void updateTraceTableSuccess() throws UserNotHavePermissionException {
        Long userId = 1L;
        Long traceId = 2L;

        User mockUser = mock(User.class);
        TraceTable mockTracetable = mock(TraceTable.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(traceTableRepository.findById(traceId)).thenReturn(Optional.of(mockTracetable));

        when(mockUser.userNotHavePermission(any())).thenReturn(false);

        TraceTableRequest mockTraceTableRequest = mock(TraceTableRequest.class);
        TraceTableResponse mockTraceTableResponse = mock(TraceTableResponse.class);

        when(mockTracetable.entityToResponse(minioService)).thenReturn(mockTraceTableResponse);

        TraceTableResponse result = traceTableService.updateTraceTable(mockTraceTableRequest, traceId, userId);

        assertEquals(mockTraceTableResponse, result);
        verify(userRepository, times(1)).findById(userId);
        verify(traceTableRepository, times(1)).findById(traceId);
        verify(mockTracetable, times(1)).entityToResponse(minioService);
        verify(traceTableRepository, times(1)).save(mockTracetable);
    }

    @Test
    void updateTraceTableThrowsExceptionWhenUserNotFound() {
        Long userId = 1L;
        Long traceId = 2L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        TraceTableRequest mockTraceTableRequest = mock(TraceTableRequest.class);

        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class,
                () -> traceTableService.updateTraceTable(mockTraceTableRequest, traceId, userId));

        assertEquals("Usuário não encontrado", userNotFoundException.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(traceTableRepository, minioService);
    }

    @Test
    void updateTraceTableThrowsExceptionWhenTraceNotFound() {
        Long userId = 1L;
        Long traceId = 2L;

        User mockUser = mock(User.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        when(traceTableRepository.findById(traceId)).thenReturn(Optional.empty());

        TraceTableRequest mockTraceTableRequest = mock(TraceTableRequest.class);

        TraceNotFoundException traceNotFoundException = assertThrows(TraceNotFoundException.class,
                () -> traceTableService.updateTraceTable(mockTraceTableRequest, traceId, userId));

        assertEquals("Exercício não encontrado", traceNotFoundException.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(traceTableRepository, times(1)).findById(traceId);
        verifyNoInteractions(minioService);
    }

    @Test
    void updateTraceTableThrowsExceptionWhenUserNotHavePermission() {
        Long userId = 1L;
        Long traceId = 2L;

        User mockUser = mock(User.class);
        TraceTable mockTraceTable = mock(TraceTable.class);
        TraceTableRequest mockTraceTableRequest = mock(TraceTableRequest.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(traceTableRepository.findById(traceId)).thenReturn(Optional.of(mockTraceTable));

        when(mockUser.userNotHavePermission(any())).thenReturn(true);

        UserNotHavePermissionException userNotHavePermissionException = assertThrows(
                UserNotHavePermissionException.class,
                () -> traceTableService.updateTraceTable(mockTraceTableRequest, traceId, userId));

        assertEquals("Você não tem permissão de remover este exercício!", userNotHavePermissionException.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(traceTableRepository, times(1)).findById(traceId);
        verifyNoInteractions(minioService);
    }
}
