package com.ufpb.br.apps4society.my_trace_table_manager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void enviarCodigoVerificacao(String destinatario, String codigo, String tipo) {
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setTo(destinatario);

        if ("REGISTRATION".equals(tipo)) {
            mensagem.setSubject("My Trace Table — Código de verificação");
            mensagem.setText(
                    "Olá!\n\n" +
                            "Seu código de verificação para criar a conta no My Trace Table é:\n\n" +
                            "  " + codigo + "\n\n" +
                            "O código expira em 15 minutos.\n" +
                            "Se você não solicitou isso, ignore este email."
            );
        } else {
            mensagem.setSubject("My Trace Table — Redefinição de senha");
            mensagem.setText(
                    "Olá!\n\n" +
                            "Seu código para redefinir a senha no My Trace Table é:\n\n" +
                            "  " + codigo + "\n\n" +
                            "O código expira em 15 minutos.\n" +
                            "Se você não solicitou isso, ignore este email."
            );
        }

        mailSender.send(mensagem);
    }
}