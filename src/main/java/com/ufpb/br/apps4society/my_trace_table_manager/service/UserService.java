package com.ufpb.br.apps4society.my_trace_table_manager.service;

import com.ufpb.br.apps4society.my_trace_table_manager.dto.user.*;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.TraceTable;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.User;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.role.Role;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.type.VerificationCodeType;
import com.ufpb.br.apps4society.my_trace_table_manager.repository.UserRepository;
import com.ufpb.br.apps4society.my_trace_table_manager.security.TokenProvider;
import com.ufpb.br.apps4society.my_trace_table_manager.service.exception.InvalidUserException;
import com.ufpb.br.apps4society.my_trace_table_manager.service.exception.UserAlreadyExistsException;
import com.ufpb.br.apps4society.my_trace_table_manager.service.exception.UserNotFoundException;
import com.ufpb.br.apps4society.my_trace_table_manager.service.exception.UserNotHavePermissionException;

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
    private TraceTableService traceTableService;
    private VerificationCodeService verificationCodeService;

    @Autowired
    public UserService(UserRepository userRepository, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, TokenProvider tokenProvider, TraceTableService traceTableService, VerificationCodeService verificationCodeService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.traceTableService = traceTableService;
        this.verificationCodeService = verificationCodeService;
    }

    public UserResponse registerUserWithCode(UserRequest userRequest, String code) throws UserAlreadyExistsException {

        if (userRepository.findUserByEmail(userRequest.email()).isPresent()) {
            throw new UserAlreadyExistsException("Tente se registrar com outro email");
        }

        verificationCodeService.verificarCodigo(
                userRequest.email(), code, VerificationCodeType.REGISTRATION);

        User saveUser = new User(userRequest);
        saveUser.setPassword(passwordEncoder.encode(userRequest.password()));

        userRepository.save(saveUser);

        return saveUser.entityToResponse();
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

        for (TraceTable traceTable : removeUser.getTraceTables()) {
            traceTableService.removeTraceTable(id, traceTable.getId());
        }

        userRepository.delete(removeUser);
    }

    public Page<UserResponse> findAllUsers(Pageable pageable, String name)  {
        Page<User> users;

        if (name.isBlank()) {
            users = userRepository.findAllProfessors(pageable);
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

        User user = userRepository.findById(Long.valueOf(userId))
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

    public void redefinirSenha(String email, String novaSenha) {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado."));
        user.setPassword(passwordEncoder.encode(novaSenha));
        userRepository.save(user);
    }

    public void findByEmail(String email) {
        userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado."));
    }

    public void ensureEmailNotRegistered(String email) throws UserAlreadyExistsException {
        if (userRepository.findUserByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("Este email já está cadastrado.");
        }
    }
}