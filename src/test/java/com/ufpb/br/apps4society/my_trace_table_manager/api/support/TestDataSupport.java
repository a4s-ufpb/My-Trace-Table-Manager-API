package com.ufpb.br.apps4society.my_trace_table_manager.api.support;

import com.ufpb.br.apps4society.my_trace_table_manager.dto.theme.ThemeRequest;
import com.ufpb.br.apps4society.my_trace_table_manager.dto.tracetable.TraceTableRequest;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.Answer;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.Theme;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.TraceTable;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.User;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.VerificationCode;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.role.Role;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.type.VerificationCodeType;
import com.ufpb.br.apps4society.my_trace_table_manager.repository.ThemeRepository;
import com.ufpb.br.apps4society.my_trace_table_manager.repository.AnswerRepository;
import com.ufpb.br.apps4society.my_trace_table_manager.repository.TraceTableRepository;
import com.ufpb.br.apps4society.my_trace_table_manager.repository.UserRepository;
import com.ufpb.br.apps4society.my_trace_table_manager.repository.VerificationCodeRepository;
import com.ufpb.br.apps4society.my_trace_table_manager.util.TableSerializationUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class TestDataSupport {

    private final AnswerRepository answerRepository;
    private final ThemeRepository themeRepository;
    private final UserRepository userRepository;
    private final TraceTableRepository traceTableRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final PasswordEncoder passwordEncoder;

    public TestDataSupport(
            AnswerRepository answerRepository,
            ThemeRepository themeRepository,
            UserRepository userRepository,
            TraceTableRepository traceTableRepository, VerificationCodeRepository verificationCodeRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.answerRepository = answerRepository;
        this.themeRepository = themeRepository;
        this.userRepository = userRepository;
        this.traceTableRepository = traceTableRepository;
        this.verificationCodeRepository = verificationCodeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Theme prepararTema(String themeTitle, String professorEmail) {
        User professor = prepararProfessor(professorEmail);

        ThemeRequest request = new ThemeRequest(themeTitle);
        Theme theme = new Theme(request, professor);

        return themeRepository.save(theme);
    }

    public User prepararProfessor(String email) {
        User professor = userRepository.findUserByEmail(email)
                .orElseGet(User::new);

        professor.setName("Professor de Teste");
        professor.setEmail(email);
        professor.setPassword(passwordEncoder.encode("password"));
        professor.setRole(Role.PROFESSOR);

        return userRepository.save(professor);
    }

    public User prepararUsuario(String email) {
        User aluno = userRepository.findUserByEmail(email)
                .orElseGet(User::new);

        aluno.setName("Aluno de Teste");
        aluno.setEmail(email);
        aluno.setPassword(passwordEncoder.encode("password"));
        aluno.setRole(Role.ALUNO);

        return userRepository.save(aluno);
    }

    public VerificationCode prepararCodigo(String email, String code) {
        User user = userRepository.findUserByEmail(email).get();

        VerificationCode vc = VerificationCode.builder()
                .email(user.getEmail())
                .code(code)
                .type(VerificationCodeType.PASSWORD_RESET)
                .used(false)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();

        return verificationCodeRepository.save(vc);
    }

    public VerificationCode prepararCodigoExpirado(String email, String code) {
        User user = userRepository.findUserByEmail(email).get();

        VerificationCode vc = VerificationCode.builder()
                .email(user.getEmail())
                .code(code)
                .type(VerificationCodeType.PASSWORD_RESET)
                .used(false)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().minusMinutes(1))
                .build();

        return verificationCodeRepository.save(vc);
    }

    public VerificationCode prepararCodigoUsado(String email, String code) {
        User user = userRepository.findUserByEmail(email).get();

        VerificationCode vc = VerificationCode.builder()
                .email(user.getEmail())
                .code(code)
                .type(VerificationCodeType.PASSWORD_RESET)
                .used(true)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();

        return verificationCodeRepository.save(vc);
    }

    public VerificationCode prepararCodigoRegistro(String email, String code) {
        VerificationCode vc = VerificationCode.builder()
                .email(email)
                .code(code)
                .type(VerificationCodeType.REGISTRATION)
                .used(false)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();

        return verificationCodeRepository.save(vc);
    }

    public VerificationCode prepararCodigoRegistroExpirado(String email, String code) {
        VerificationCode vc = VerificationCode.builder()
                .email(email)
                .code(code)
                .type(VerificationCodeType.REGISTRATION)
                .used(false)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().minusMinutes(1))
                .build();

        return verificationCodeRepository.save(vc);
    }

    public TraceTable prepararTraceTable(String exerciseName, Long userId, List<Long> themesIds) {
        TraceTableRequest request = new TraceTableRequest(
                exerciseName,
                List.of("Variável", "Valor"),
                List.of(List.of("x", "10")),
                List.of(List.of("x", "20")),
                List.of(List.of("int", "int")),
                "Java"
        );

        return prepararTraceTable(request, userId, themesIds);
    }

    public TraceTable prepararTraceTable(String exerciseName, String professorEmail, String themeTitle) {
        Theme theme = prepararTema(themeTitle, professorEmail);
        return prepararTraceTable(exerciseName, theme.getCreator().getId(), List.of(theme.getId()));
    }

    public TraceTable prepararTraceTable(
            TraceTableRequest request,
            String professorEmail,
            String themeTitle
    ) {
        Theme theme = prepararTema(themeTitle, professorEmail);
        return prepararTraceTable(request, theme.getCreator().getId(), List.of(theme.getId()));
    }

    public TraceTable prepararTraceTable(TraceTableRequest request, Long userId, List<Long> themesIds) {

        User creator = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado")
                );

        List<Theme> themes = themeRepository.findAllById(themesIds);

        if (themes.isEmpty()) {
            throw new RuntimeException(
                    "Nenhum tema encontrado para os IDs fornecidos"
            );
        }

        TraceTable traceTable = new TraceTable(request, creator, themes);

        traceTable.setImgName("test-image.png");

        return traceTableRepository.save(traceTable);
    }

    public Answer prepararResposta(
            TraceTable traceTable,
            User student,
            List<List<String>> answerData
    ) {
        Answer answer = new Answer();
        answer.setStudent(student);
        answer.setTraceTable(traceTable);
        answer.setAnswerData(TableSerializationUtil.serializeTable(answerData));
        answer.setCreatedAt(LocalDateTime.now().minusHours(3));
        return answerRepository.save(answer);
    }

    public void removerTemas() {
        themeRepository.deleteAll();
    }
}
