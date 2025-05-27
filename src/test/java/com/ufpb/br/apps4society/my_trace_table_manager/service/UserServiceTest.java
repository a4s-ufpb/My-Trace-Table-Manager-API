package com.ufpb.br.apps4society.my_trace_table_manager.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ufpb.br.apps4society.my_trace_table_manager.dto.user.AdminResponse;
import com.ufpb.br.apps4society.my_trace_table_manager.dto.user.TokenResponse;
import com.ufpb.br.apps4society.my_trace_table_manager.dto.user.UserLogin;
import com.ufpb.br.apps4society.my_trace_table_manager.dto.user.UserRequest;
import com.ufpb.br.apps4society.my_trace_table_manager.dto.user.UserResponse;
import com.ufpb.br.apps4society.my_trace_table_manager.dto.user.UserUpdate;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.User;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.role.Role;
import com.ufpb.br.apps4society.my_trace_table_manager.repository.UserRepository;
import com.ufpb.br.apps4society.my_trace_table_manager.security.TokenProvider;
import com.ufpb.br.apps4society.my_trace_table_manager.service.exception.UserAlreadyExistsException;
import com.ufpb.br.apps4society.my_trace_table_manager.service.exception.UserNotFoundException;
import com.ufpb.br.apps4society.my_trace_table_manager.service.exception.UserNotHavePermissionException;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenProvider tokenProvider;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUserSuccess() throws UserAlreadyExistsException {
        UserRequest mockRequest = mock(UserRequest.class);

        when(mockRequest.email()).thenReturn("email@test.com");
        when(mockRequest.password()).thenReturn("12345678");
        when(mockRequest.name()).thenReturn("Teste");
        when(mockRequest.role()).thenReturn(Role.USER);

        when(userRepository.findByEmail("email@test.com")).thenReturn(null);

        when(passwordEncoder.encode("12345678")).thenReturn("encodedPassword");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse result = userService.registerUser(mockRequest);

        assertNotNull(result);
        assertEquals("Teste", result.name());
        assertEquals("email@test.com", result.email());
        assertEquals(Role.USER.getRole(), result.role());

        verify(userRepository, times(1)).findByEmail("email@test.com");
        verify(passwordEncoder, times(1)).encode("12345678");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUserThrowsUserAlreadyExists() {
        UserRequest mockRequest = mock(UserRequest.class);

        when(mockRequest.email()).thenReturn("email@test.com");

        User existingUser = mock(User.class);

        when(userRepository.findByEmail("email@test.com")).thenReturn(existingUser);

        UserAlreadyExistsException userAlreadyExistsException = assertThrows(UserAlreadyExistsException.class,
                () -> userService.registerUser(mockRequest));

        assertEquals("Tente se registrar com outro email", userAlreadyExistsException.getMessage());

        verify(userRepository, times(1)).findByEmail("email@test.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void loginUserSuccess() {
        UserLogin mockLogin = mock(UserLogin.class);
        User mockUser = mock(User.class);
        Authentication mockAuth = mock(Authentication.class);

        when(mockLogin.email()).thenReturn("email@test.com");
        when(mockLogin.password()).thenReturn("password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuth);
        when(mockAuth.getPrincipal()).thenReturn(mockUser);
        when(tokenProvider.generateToken(mockUser)).thenReturn("token123");
        when(tokenProvider.getExpirationTimeInSeconds()).thenReturn(86400L);

        TokenResponse tokenResponse = userService.loginUser(mockLogin);

        assertNotNull(tokenResponse);
        assertEquals("token123", tokenResponse.token());
        assertEquals(86400L, tokenResponse.expiresIn());

        verify(authenticationManager, times(1)).authenticate(any());
        verify(tokenProvider, times(1)).generateToken(mockUser);
    }

    @Test
    void findUserSuccess() {
        String token = "Bearer token123";

        User mockUser = mock(User.class);
        UserResponse mockResponse = mock(UserResponse.class);

        when(tokenProvider.getSubjectByToken("token123")).thenReturn("1");
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(mockUser.entityToResponse()).thenReturn(mockResponse);

        UserResponse result = userService.findUser(token);

        assertEquals(mockResponse, result);

        verify(tokenProvider, times(1)).getSubjectByToken("token123");
        verify(userRepository, times(1)).findById(1L);
        verify(mockUser, times(1)).entityToResponse();
    }

    @Test
    void removeUserSuccess() throws Exception {
        Long userIdToRemove = 2L;
        String token = "Bearer 123";

        User loggedUser = mock(User.class);
        User userToRemove = mock(User.class);

        when(tokenProvider.getSubjectByToken("123")).thenReturn("123");

        when(userRepository.findById(123L)).thenReturn(Optional.of(loggedUser));
        when(userRepository.findById(userIdToRemove)).thenReturn(Optional.of(userToRemove));

        when(loggedUser.userNotHavePermission(userToRemove)).thenReturn(false);

        doNothing().when(userRepository).delete(userToRemove);

        userService.removeUser(userIdToRemove, token);

        verify(userRepository, times(1)).findById(userIdToRemove);
        verify(userRepository, times(1)).delete(userToRemove);
    }

    @Test
    void removeUserThrowsUserNotHavePermissionException() {
        Long userIdToRemove = 2L;
        String token = "Bearer 123";

        User loggedUser = mock(User.class);
        User userToRemove = mock(User.class);

        when(tokenProvider.getSubjectByToken("123")).thenReturn("123");

        when(userRepository.findById(123L)).thenReturn(Optional.of(loggedUser));
        when(userRepository.findById(userIdToRemove)).thenReturn(Optional.of(userToRemove));

        when(loggedUser.userNotHavePermission(userToRemove)).thenReturn(true);

        UserNotHavePermissionException exception = assertThrows(
                UserNotHavePermissionException.class,
                () -> userService.removeUser(userIdToRemove, token));

        assertEquals("Você não tem permissão para remover esse usuário", exception.getMessage());

        verify(userRepository, times(1)).findById(userIdToRemove);
        verify(userRepository, never()).delete(any());
    }

    @Test
    void findAllUsersCallsFindAllWhenNameIsBlank() {
        Pageable pageable = PageRequest.of(0, 10);
        String name = "";

        User mockUser = mock(User.class);
        UserResponse mockUserResponse = mock(UserResponse.class);

        Page<User> mockPage = new PageImpl<>(List.of(mockUser), pageable, 1);
        when(userRepository.findAll(pageable)).thenReturn(mockPage);
        when(mockUser.entityToResponse()).thenReturn(mockUserResponse);

        Page<UserResponse> result = userService.findAllUsers(pageable, name);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(mockUserResponse, result.getContent().get(0));

        verify(userRepository, times(1)).findAll(pageable);
        verify(userRepository, never()).findByNameStartsWithIgnoreCase(anyString(), any());
    }

    @Test
    void findAllUsersCallsFindByNameWhenNameIsNotBlank() {
        Pageable pageable = PageRequest.of(0, 10);
        String name = "John";

        User mockUser = mock(User.class);
        UserResponse mockUserResponse = mock(UserResponse.class);

        Page<User> mockPage = new PageImpl<>(List.of(mockUser), pageable, 1);
        when(userRepository.findByNameStartsWithIgnoreCase(eq(name), eq(pageable))).thenReturn(mockPage);
        when(mockUser.entityToResponse()).thenReturn(mockUserResponse);

        Page<UserResponse> result = userService.findAllUsers(pageable, name);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(mockUserResponse, result.getContent().get(0));

        verify(userRepository, never()).findAll(pageable);
        verify(userRepository, times(1)).findByNameStartsWithIgnoreCase(eq(name), eq(pageable));
    }

    @Test
    void updateUserSuccess() throws Exception {
        Long userId = 1L;
        String token = "Bearer token123";

        User loggedUser = mock(User.class);
        User userToUpdate = mock(User.class);
        UserUpdate userUpdate = mock(UserUpdate.class);

        UserService spyUserService = Mockito.spy(userService);

        doReturn(loggedUser).when(spyUserService).findUserByToken(token);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userToUpdate));
        when(loggedUser.userNotHavePermission(userToUpdate)).thenReturn(false);

        when(userUpdate.name()).thenReturn("Novo Nome");
        when(userUpdate.email()).thenReturn("novoemail@example.com");
        when(userUpdate.role()).thenReturn(Role.USER);
        when(userUpdate.password()).thenReturn("novaSenha123");

        when(passwordEncoder.encode("novaSenha123")).thenReturn("senhaCodificada");
        when(userRepository.save(userToUpdate)).thenReturn(userToUpdate);

        UserResponse userResponse = mock(UserResponse.class);
        when(userToUpdate.entityToResponse()).thenReturn(userResponse);

        UserResponse result = spyUserService.updateUser(userId, userUpdate, token);

        assertNotNull(result);
        assertEquals(userResponse, result);

        verify(userToUpdate).setName("Novo Nome");
        verify(userToUpdate).setEmail("novoemail@example.com");
        verify(userToUpdate).setRole(Role.USER);
        verify(userToUpdate).setPassword("senhaCodificada");

        verify(userRepository).save(userToUpdate);
        verify(spyUserService).findUserByToken(token);
        verify(userRepository).findById(userId);
    }

    @Test
    void updateUserThrowsUserNotFoundException() {
        Long userId = 1L;
        String token = "Bearer token123";
        UserUpdate userUpdate = mock(UserUpdate.class);

        UserService spyUserService = Mockito.spy(userService);

        doReturn(mock(User.class)).when(spyUserService).findUserByToken(token);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> spyUserService.updateUser(userId, userUpdate, token));

        assertEquals("Usuário não encontrado!", exception.getMessage());

        verify(spyUserService).findUserByToken(token);
        verify(userRepository).findById(userId);
    }

    @Test
    void updateUserThrowsUserNotHavePermissionException() {
        Long userId = 1L;
        String token = "Bearer token123";
        UserUpdate userUpdate = mock(UserUpdate.class);

        User loggedUser = mock(User.class);
        User userToUpdate = mock(User.class);

        UserService spyUserService = Mockito.spy(userService);

        doReturn(loggedUser).when(spyUserService).findUserByToken(token);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userToUpdate));

        when(loggedUser.userNotHavePermission(userToUpdate)).thenReturn(true);

        UserNotHavePermissionException exception = assertThrows(
                UserNotHavePermissionException.class,
                () -> spyUserService.updateUser(userId, userUpdate, token));

        assertEquals("Você não tem permissão para atualizar esse usuário", exception.getMessage());

        verify(spyUserService).findUserByToken(token);
        verify(userRepository).findById(userId);
        verify(loggedUser).userNotHavePermission(userToUpdate);

        verify(userRepository, never()).save(any());
    }

    @Test
    void findUserByTokenSuccess() {
        String token = "Bearer token123";
        User mockUser = mock(User.class);

        when(tokenProvider.getSubjectByToken("token123")).thenReturn("1");
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        User result = userService.findUserByToken(token);

        assertEquals(mockUser, result);
    }

    @Test
    void findUserByTokenThrowsInvalidUserException() {
        String token = "Bearer token123";

        when(tokenProvider.getSubjectByToken("token123")).thenReturn("1");
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(com.ufpb.br.apps4society.my_trace_table_manager.service.exception.InvalidUserException.class,
                () -> userService.findUserByToken(token));
    }

    @Test
    void validateIfUserIsAdminSuccess() throws UserNotHavePermissionException {
        String token = "Bearer token123";
        Long userId = 1L;

        User loggedUser = mock(User.class);
        User user = mock(User.class);

        UserService spyUserService = Mockito.spy(userService);

        doReturn(loggedUser).when(spyUserService).findUserByToken(token);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(loggedUser.userNotHavePermission(user)).thenReturn(false);

        when(loggedUser.getRole()).thenReturn(Role.ADMIN);

        AdminResponse response = spyUserService.validateIfUserIsAdmin(token, userId);

        assertNotNull(response);
        assertTrue(response.isAdmin());

        verify(spyUserService).findUserByToken(token);
        verify(userRepository).findById(userId);
        verify(loggedUser).userNotHavePermission(user);
        verify(loggedUser).getRole();
    }

    @Test
    void validateIfUserIsAdminThrowsUserNotHavePermissionException() {
        String token = "Bearer token123";
        Long userId = 1L;

        User loggedUser = mock(User.class);
        User user = mock(User.class);

        UserService spyUserService = Mockito.spy(userService);

        doReturn(loggedUser).when(spyUserService).findUserByToken(token);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(loggedUser.userNotHavePermission(user)).thenReturn(true);

        UserNotHavePermissionException exception = assertThrows(
                UserNotHavePermissionException.class,
                () -> spyUserService.validateIfUserIsAdmin(token, userId));

        assertEquals("Você não tem permissão para realizar essa funcionalidade", exception.getMessage());

        verify(spyUserService).findUserByToken(token);
        verify(userRepository).findById(userId);
        verify(loggedUser).userNotHavePermission(user);
    }

}