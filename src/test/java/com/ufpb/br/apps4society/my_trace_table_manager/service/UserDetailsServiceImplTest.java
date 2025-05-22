package com.ufpb.br.apps4society.my_trace_table_manager.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.ufpb.br.apps4society.my_trace_table_manager.entity.User;
import com.ufpb.br.apps4society.my_trace_table_manager.repository.UserRepository;

public class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsernameSuccess() {
        String userName = "teste";
        User mockUser = mock(User.class);

        when(userRepository.findByEmail(userName)).thenReturn(mockUser);

        UserDetails userDetails = userDetailsService.loadUserByUsername(userName);

        assertNotNull(userDetails);
        assertEquals(mockUser, userDetails);
        verify(userRepository, times(1)).findByEmail(userName);
    }

    @Test
    void loadUserByUsernameThrowsExceptionWhenUserNotFound() {
        String userName = "nome-invalido";
        
        when(userRepository.findByEmail(userName)).thenReturn(null);

        UsernameNotFoundException usernameNotFoundException = assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(userName));

        assertEquals("Usuário inválido, pode ter sido removido do BD e utilizado o token", usernameNotFoundException.getMessage());
        verify(userRepository, times(1)).findByEmail(userName);
    }

    @Test
    void loadUserByIdSuccess() {
        Long userId = 1L;
        User mockUser = mock(User.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        UserDetails userDetails = userDetailsService.loadUserById(userId);

        assertNotNull(userDetails);
        assertEquals(mockUser, userDetails);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void loadUserByIdThrowsExceptionWhenUserNotFound() {
        Long userId = 1L;
        
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UsernameNotFoundException usernameNotFoundException = assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserById(userId));

        assertEquals("Usuário inválido, pode ter sido removido do BD e utilizado o token",
                usernameNotFoundException.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }
}