package com.ufpb.br.apps4society.my_trace_table_manager.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.ufpb.br.apps4society.my_trace_table_manager.dto.theme.ThemeRequest;
import com.ufpb.br.apps4society.my_trace_table_manager.dto.theme.ThemeResponse;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.Theme;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.TraceTable;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.User;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.role.Role;
import com.ufpb.br.apps4society.my_trace_table_manager.repository.ThemeRepository;
import com.ufpb.br.apps4society.my_trace_table_manager.repository.TraceTableRepository;
import com.ufpb.br.apps4society.my_trace_table_manager.repository.UserRepository;
import com.ufpb.br.apps4society.my_trace_table_manager.service.exception.ThemeNotFoundException;
import com.ufpb.br.apps4society.my_trace_table_manager.service.exception.UserNotFoundException;
import com.ufpb.br.apps4society.my_trace_table_manager.service.exception.UserNotHavePermissionException;

public class ThemeServiceTest {

    @Mock
    private ThemeRepository themeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TraceTableRepository traceTableRepository;

    @Mock
    private MinioService minioService;

    @InjectMocks
    private ThemeService themeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void insertThemeSuccess() {
        Long userId = 1L;
        ThemeRequest themeRequest = new ThemeRequest("Tema de Teste");

        User user = new User();
        user.setId(1L);
        user.setName("Usuário Teste");
        user.setEmail("teste@email.com");
        user.setRole(Role.USER);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        ArgumentCaptor<Theme> themeCaptor = ArgumentCaptor.forClass(Theme.class);
        doAnswer(invocation -> invocation.getArgument(0)).when(themeRepository).save(themeCaptor.capture());

        ThemeResponse result = themeService.insertTheme(themeRequest, userId);

        Theme savedTheme = themeCaptor.getValue();
        assertNotNull(result);
        assertEquals(themeRequest.name(), result.name());
        assertEquals(user.getName(), result.creator().name());
        assertEquals(savedTheme.getName(), result.name());
        assertEquals(savedTheme.getCreator(), user);

        verify(themeRepository, times(1)).save(any(Theme.class));
    }

    @Test
    void insertThemeThrowsUserNotFoundException() {
        Long userId = 1L;
        ThemeRequest mockRequest = mock(ThemeRequest.class);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> themeService.insertTheme(mockRequest, userId));

        assertEquals("Usuário não encontrado", exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(themeRepository);
    }

    public ThemeResponse insertTheme(ThemeRequest themeRequest, Long userId) {
        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        Theme theme = new Theme(themeRequest, creator);

        themeRepository.save(theme);

        return theme.entityToResponse();
    }

    @Test
    void findAllThemesSuccess() {
        Pageable pageable = PageRequest.of(0, 10);

        Theme mockTheme1 = mock(Theme.class);
        Theme mockTheme2 = mock(Theme.class);
        Page<Theme> mockPage = new PageImpl<>(List.of(mockTheme1, mockTheme2), pageable, 2);

        when(themeRepository.findAll(pageable)).thenReturn(mockPage);

        ThemeResponse mockThemeResponse1 = mock(ThemeResponse.class);
        ThemeResponse mockThemeResponse2 = mock(ThemeResponse.class);

        when(mockTheme1.entityToResponse()).thenReturn(mockThemeResponse1);
        when(mockTheme2.entityToResponse()).thenReturn(mockThemeResponse2);

        Page<ThemeResponse> resultPage = themeService.findAllThemes(pageable);

        assertNotNull(resultPage);
        assertEquals(2, resultPage.getTotalElements());
        assertEquals(mockThemeResponse1, resultPage.getContent().get(0));
        assertEquals(mockThemeResponse2, resultPage.getContent().get(1));

        verify(themeRepository, times(1)).findAll(pageable);
        verify(mockTheme1, times(1)).entityToResponse();
        verify(mockTheme2, times(1)).entityToResponse();
    }

    @Test
    void findThemesByUserSuccess() {
        Long userId = 1L;

        User mockUser = mock(User.class);
        Theme mockTheme = mock(Theme.class);
        ThemeResponse mockThemeResponse = mock(ThemeResponse.class);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Theme> mockPage = new PageImpl<>(List.of(mockTheme), pageable, 1);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(themeRepository.findByCreator(pageable, mockUser)).thenReturn(mockPage);
        when(mockTheme.entityToResponse()).thenReturn(mockThemeResponse);

        Page<ThemeResponse> result = themeService.findThemesByUser(pageable, userId);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(mockThemeResponse, result.getContent().get(0));

        verify(userRepository, times(1)).findById(userId);
        verify(themeRepository, times(1)).findByCreator(pageable, mockUser);
        verify(mockTheme, times(1)).entityToResponse();
    }

    @Test
    void findByUserThrowsExceptionWhenUserNotFound() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        Pageable pageable = PageRequest.of(0, 10);

        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class, () -> {
            themeService.findThemesByUser(pageable, userId);
        });

        assertEquals("Usuário não encontrado", userNotFoundException.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(themeRepository);
    }

    @Test
    void findAllThemesByTraceTableSuccess() {
        Long traceTableId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        Theme mockTheme = mock(Theme.class);
        ThemeResponse mockThemeResponse = mock(ThemeResponse.class);

        Page<Theme> mockPage = new PageImpl<>(List.of(mockTheme), pageable, 1);

        when(themeRepository.findByTraceTables_Id(pageable, traceTableId)).thenReturn(mockPage);
        when(mockTheme.entityToResponse()).thenReturn(mockThemeResponse);

        Page<ThemeResponse> result = themeService.findAllThemesByTraceTable(pageable, traceTableId);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(mockThemeResponse, result.getContent().get(0));

        verify(themeRepository, times(1)).findByTraceTables_Id(pageable, traceTableId);
        verify(mockTheme, times(1)).entityToResponse();
    }

    @Test
    void removeThemeSuccess() throws Exception {
        Long userId = 1L;
        Long themeId = 1L;

        User mockUser = mock(User.class);
        Theme mockTheme = mock(Theme.class);
        User mockCreator = mock(User.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(themeRepository.findById(themeId)).thenReturn(Optional.of(mockTheme));

        when(mockTheme.getId()).thenReturn(themeId);
        when(mockTheme.getCreator()).thenReturn(mockCreator);

        when(mockUser.userNotHavePermission(mockCreator)).thenReturn(false);

        List<Theme> themeList = new ArrayList<>();
        themeList.add(mockTheme);

        TraceTable mockTraceTable = spy(TraceTable.class);
        doReturn(themeList).when(mockTraceTable).getThemes();

        Page<TraceTable> mockPage = new PageImpl<>(List.of(mockTraceTable));

        when(traceTableRepository.findByThemes_Id(any(Pageable.class), eq(themeId))).thenReturn(mockPage);

        when(traceTableRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(traceTableRepository).delete(any());
        doNothing().when(minioService).deleteObject(any());
        doNothing().when(themeRepository).delete(mockTheme);

        themeService.removeTheme(themeId, userId);

        verify(userRepository, times(1)).findById(userId);
        verify(themeRepository, times(1)).findById(themeId);
        verify(mockTheme, times(1)).getId();
        verify(mockUser, times(1)).userNotHavePermission(mockCreator);
        verify(traceTableRepository, times(1)).findByThemes_Id(any(Pageable.class), eq(themeId));

        verify(traceTableRepository, times(1)).delete(any());
        verify(minioService, times(1)).deleteObject(any());
        verify(themeRepository, times(1)).delete(mockTheme);
    }

    @Test
    void removeThemeThrowsExceptionWhenUserNotFound() {
        Long userId = 1L;
        Long themeId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class, () -> {
            themeService.removeTheme(themeId, userId);
        });

        assertEquals("Usuário não encontrado", userNotFoundException.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(themeRepository, traceTableRepository, minioService);
    }

    @Test
    void removeThemeThrowsExceptionWhenThemeNotFound() {
        Long userId = 1L;
        Long themeId = 1L;

        User mockUser = mock(User.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(themeRepository.findById(themeId)).thenReturn(Optional.empty());

        ThemeNotFoundException themeNotFoundException = assertThrows(ThemeNotFoundException.class,
                () -> themeService.removeTheme(themeId, userId));

        assertEquals("Tema não encontrado!", themeNotFoundException.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(themeRepository, times(1)).findById(themeId);
        verifyNoInteractions(traceTableRepository, minioService);
    }

    @Test
    void removeThemeThrowsExceptionWhenUserNotHavePermission() {
        Long userId = 1L;
        Long themeId = 1L;

        User mockUser = mock(User.class);
        Theme mockTheme = mock(Theme.class);
        User mockCreator = mock(User.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(themeRepository.findById(themeId)).thenReturn(Optional.of(mockTheme));
        when(mockTheme.getCreator()).thenReturn(mockCreator);

        when(mockUser.userNotHavePermission(mockCreator)).thenReturn(true);

        UserNotHavePermissionException userNotHavePermissionException = assertThrows(
                UserNotHavePermissionException.class, () -> themeService.removeTheme(themeId, userId));

        assertEquals("Esse usuário não tem permissão para remover esse tema!",
                userNotHavePermissionException.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(themeRepository, times(1)).findById(themeId);
        verify(mockUser, times(1)).userNotHavePermission(mockCreator);
        verifyNoInteractions(traceTableRepository, minioService);
    }

    @Test
    void updateThemeSuccess() throws UserNotHavePermissionException {
        Long userId = 1L;
        Long themeId = 1L;

        User mockUser = mock(User.class);
        Theme mockTheme = mock(Theme.class);
        ThemeRequest mockNewTheme = mock(ThemeRequest.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(themeRepository.findById(themeId)).thenReturn(Optional.of(mockTheme));
        when(mockTheme.getCreator()).thenReturn(mockUser);

        when(mockUser.userNotHavePermission(mockUser)).thenReturn(false);
        when(mockNewTheme.name()).thenReturn("Nome Atualizado");

        when(themeRepository.save(mockTheme)).thenReturn(mockTheme);

        ThemeResponse mockResponse = mock(ThemeResponse.class);
        when(mockTheme.entityToResponse()).thenReturn(mockResponse);

        ThemeResponse result = themeService.updateTheme(mockNewTheme, themeId, userId);

        assertEquals(mockResponse, result);
        verify(themeRepository).save(mockTheme);
        verify(mockTheme).setName("Nome Atualizado");
    }

    @Test
    void updateThemeThrowsExceptionWhenUserNotFound() {
        Long userId = 1L;
        Long themeId = 1L;

        ThemeRequest mockNewTheme = mock(ThemeRequest.class);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class,
                () -> themeService.updateTheme(mockNewTheme, themeId, userId));

        assertEquals("Usuário não encontrado", userNotFoundException.getMessage());

        verify(userRepository).findById(userId);
        verifyNoInteractions(themeRepository);
    }

    @Test
    void updateThemeThrowsExceptionWhenThemeNotFound() {
        Long userId = 1L;
        Long themeId = 1L;

        User mockUser = mock(User.class);
        ThemeRequest mockNewTheme = mock(ThemeRequest.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(themeRepository.findById(themeId)).thenReturn(Optional.empty());

        ThemeNotFoundException exception = assertThrows(ThemeNotFoundException.class, () -> {
            themeService.updateTheme(mockNewTheme, themeId, userId);
        });

        assertEquals("Tema não encontrado!", exception.getMessage());

        verify(userRepository).findById(userId);
        verify(themeRepository).findById(themeId);
    }

    @Test
    void updateThemeThrowsExceptionWhenUserNotHavePermission() {
        Long userId = 1L;
        Long themeId = 1L;

        User mockUser = mock(User.class);
        Theme mockTheme = mock(Theme.class);
        ThemeRequest mockNewTheme = mock(ThemeRequest.class);

        User mockCreator = mock(User.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(themeRepository.findById(themeId)).thenReturn(Optional.of(mockTheme));
        when(mockTheme.getCreator()).thenReturn(mockCreator);
        when(mockUser.userNotHavePermission(mockCreator)).thenReturn(true);

        UserNotHavePermissionException exception = assertThrows(UserNotHavePermissionException.class, () -> {
            themeService.updateTheme(mockNewTheme, themeId, userId);
        });

        assertEquals("Esse usuário não tem permissão para remover esse tema!", exception.getMessage());

        verify(userRepository).findById(userId);
        verify(themeRepository).findById(themeId);
        verify(mockUser).userNotHavePermission(mockCreator);
        verify(themeRepository, never()).save(any());
    }
}
