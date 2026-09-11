package com.ufpb.br.apps4society.my_trace_table_manager.controller;

import com.ufpb.br.apps4society.my_trace_table_manager.dto.code.ResetPasswordDTO;
import com.ufpb.br.apps4society.my_trace_table_manager.dto.code.SendVerificationCodeDTO;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.type.VerificationCodeType;
import com.ufpb.br.apps4society.my_trace_table_manager.service.UserService;
import com.ufpb.br.apps4society.my_trace_table_manager.service.VerificationCodeService;
import com.ufpb.br.apps4society.my_trace_table_manager.service.exception.UserAlreadyExistsException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/verification")
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationCodeService verificationCodeService;
    private final UserService userService;

    @PostMapping("/registration/send")
    public ResponseEntity<Void> enviarCodigoCadastro(@RequestBody @Valid SendVerificationCodeDTO dto)
            throws UserAlreadyExistsException {
        userService.ensureEmailNotRegistered(dto.email());
        verificationCodeService.enviarCodigo(dto.email(), VerificationCodeType.REGISTRATION);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password-reset/send")
    public ResponseEntity<Void> enviarCodigoSenha(@RequestBody @Valid SendVerificationCodeDTO dto) {
        userService.findByEmail(dto.email());
        verificationCodeService.enviarCodigo(dto.email(), VerificationCodeType.PASSWORD_RESET);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password-reset/confirm")
    public ResponseEntity<Void> redefinirSenha(@RequestBody @Valid ResetPasswordDTO dto) {
        verificationCodeService.verificarCodigo(dto.email(), dto.code(), VerificationCodeType.PASSWORD_RESET);
        userService.redefinirSenha(dto.email(), dto.newPassword());
        return ResponseEntity.ok().build();
    }
}