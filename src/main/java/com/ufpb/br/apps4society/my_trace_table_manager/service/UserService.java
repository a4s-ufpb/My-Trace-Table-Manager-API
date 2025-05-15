package com.ufpb.br.apps4society.my_trace_table_manager.service;

import com.ufpb.br.apps4society.my_trace_table_manager.dto.user.*;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.User;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.role.Role;
import com.ufpb.br.apps4society.my_trace_table_manager.repository.UserRepository;
import com.ufpb.br.apps4society.my_trace_table_manager.security.TokenProvider;
import com.ufpb.br.apps4society.my_trace_table_manager.service.exception.InvalidUserException;
import com.ufpb.br.apps4society.my_trace_table_manager.service.exception.UserAlreadyExistsException;
import com.ufpb.br.apps4society.my_trace_table_manager.service.exception.UserNotFoundException;
import com.ufpb.br.apps4society.my_trace_table_manager.service.exception.UserNotHavePermissionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private UserRepository userRepository;
    private AuthenticationManager authenticationManager;
    private PasswordEncoder passwordEncoder;
    private TokenProvider tokenProvider;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepository userRepository, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, TokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public UserResponse registerUser(UserRequest userRequest) throws UserAlreadyExistsException {
        User user = (User) userRepository.findByEmail(userRequest.email());

        if (user != null){
            throw new UserAlreadyExistsException("Tente se registrar com outro email");
        }

        User saveUser = new User(userRequest);
        saveUser.setPassword(passwordEncoder.encode(userRequest.password()));

        userRepository.save(saveUser);

        return saveUser.entityToResponse();
    }

    public TokenResponse loginUser(UserLogin userLogin){
        UsernamePasswordAuthenticationToken user = new UsernamePasswordAuthenticationToken(userLogin.email(), userLogin.password());
        Authentication auth = authenticationManager.authenticate(user);
        String token = tokenProvider.generateToken((User) auth.getPrincipal());
        long expiresIn = tokenProvider.getExpirationTimeInSeconds();
        return new TokenResponse(token, expiresIn);
    }

    public UserResponse findUser(String token) {
        return findUserByToken(token).entityToResponse();
    }

    public void removeUser(Long id, String token) throws UserNotHavePermissionException {
        User loggedUser = findUserByToken(token);

        User removeUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado!"));

        if (loggedUser.userNotHavePermission(removeUser)){
            throw new UserNotHavePermissionException("Você não tem permissão para remover esse usuário");
        }

        userRepository.delete(removeUser);
    }

    public Page<UserResponse> findAllUsers(Pageable pageable, String name)  {
        Page<User> users;

        if (name.isBlank()) {
            users = userRepository.findAll(pageable);
        } else {
            users = userRepository.findByNameStartsWithIgnoreCase(name, pageable);
        }

        return users.map(User::entityToResponse);
    }

    public UserResponse updateUser(Long id, UserUpdate userUpdate, String token) throws UserNotHavePermissionException {
        User loggedUser = findUserByToken(token);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado!"));

        if (loggedUser.userNotHavePermission(user)){
            throw new UserNotHavePermissionException("Você não tem permissão para atualizar esse usuário");
        }

        updateData(user, userUpdate);
        userRepository.save(user);

        return user.entityToResponse();
    }

    private void updateData(User user, UserUpdate userUpdate) {
        user.setName(userUpdate.name());
        user.setEmail(userUpdate.email());
        user.setRole(userUpdate.role());
        if (userUpdate.password() != null && !userUpdate.password().isBlank()){
            user.setPassword(passwordEncoder.encode(userUpdate.password()));
        }
    }

    public User findUserByToken(String token) {
        if (token != null && token.startsWith("Bearer ")){
            token = token.substring("Bearer ".length());
        }
        String userId = tokenProvider.getSubjectByToken(token);

        User user = (User) userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new InvalidUserException("Usuário inválido, pode ter sido removido do BD e utilizado o token"));

        return user;
    }

    public AdminResponse validateIfUserIsAdmin(String token, Long id) throws UserNotHavePermissionException {
        User loggedUser = findUserByToken(token);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado!"));

        if (loggedUser.userNotHavePermission(user)){
            throw new UserNotHavePermissionException("Você não tem permissão para realizar essa funcionalidade");
        }

        return new AdminResponse(
                loggedUser
                        .getRole()
                        .equals(Role.ADMIN));
    }
}