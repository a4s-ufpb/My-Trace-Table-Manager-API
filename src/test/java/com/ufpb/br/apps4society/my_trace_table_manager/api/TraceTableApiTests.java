package com.ufpb.br.apps4society.my_trace_table_manager.api;

import com.ufpb.br.apps4society.my_trace_table_manager.api.support.ApiTestSupport;
import com.ufpb.br.apps4society.my_trace_table_manager.api.support.TestDataSupport;
import com.ufpb.br.apps4society.my_trace_table_manager.dto.tracetable.TraceTableRequest;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.Answer;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.Theme;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.TraceTable;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.User;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.role.Role;
import com.ufpb.br.apps4society.my_trace_table_manager.repository.AnswerRepository;
import com.ufpb.br.apps4society.my_trace_table_manager.service.MinioService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TraceTableApiTests {

    private static final String BASE_PATH = "/v1/trace";
    private static final String TRACE_JSON = """
            {
                "exerciseName": "Exercício de teste",
                "header": ["Variável", "Valor"],
                "shownTraceTable": [["x", "10"]],
                "expectedTraceTable": [["x", "20"]],
                "typeTable": [["string", "int"]],
                "programmingLanguage": "Java"
            }
            """;

    @Autowired
    private TestDataSupport testDataSupport;

    @Autowired
    private AnswerRepository answerRepository;

    @MockBean
    private MinioService minioService;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() throws Exception {
        RestAssured.port = port;
        when(minioService.uploadFile(any(MultipartFile.class))).thenReturn("test-image.png");
        when(minioService.getObjectUrl(any(String.class))).thenReturn("http://localhost:9000/test/test-image.png");
    }

    @Nested
    class insertTraceTable {

        @Test
        @DisplayName("CT-TRT-001: Criar trace table com JSON, imagem e temas válidos")
        void CT_TRT_001_insertTraceTableSuccessfully_shouldReturn201() {
            User professor = testDataSupport.prepararProfessor("trt-professor-001@test.com");
            Theme theme = testDataSupport.prepararTema("Tema TRT 001", professor.getEmail());

            given()
                    .header("Authorization", "Bearer " + ApiTestSupport.obterToken(professor.getEmail(), "password"))
                    .multiPart("traceTableRequest", TRACE_JSON, MediaType.APPLICATION_JSON_VALUE)
                    .multiPart("image", "test.png", "test-image".getBytes(StandardCharsets.UTF_8), MediaType.IMAGE_PNG_VALUE)
                    .queryParam("themesIds", theme.getId())
            .when()
                    .post(BASE_PATH + "/{userId}", professor.getId())
            .then()
                    .statusCode(201)
                    .body("id", notNullValue())
                    .body("programmingLanguage", equalTo("Java"));
        }

        @Test
        @DisplayName("CT-TRT-002: Criar trace table com parte obrigatória ausente, JSON malformado ou payload inválido")
        void CT_TRT_002_insertTraceTableError_shouldRejectInvalidMultipart() {
            User professor = testDataSupport.prepararProfessor("trt-professor-002@test.com");
            Theme theme = testDataSupport.prepararTema("Tema TRT 002", professor.getEmail());

            given()
                    .header("Authorization", "Bearer " + ApiTestSupport.obterTokenAdmin())
                    .multiPart("traceTableRequest", "{malformed", MediaType.APPLICATION_JSON_VALUE)
                    .queryParam("themesIds", theme.getId())
                    .when()
                    .post(BASE_PATH + "/{userId}", professor.getId())
                    .then()
                    .statusCode(anyOf(is(400), is(403)));
        }

        @Test
        @DisplayName("CT-TRT-003: Criar trace table com usuário inexistente ou ID inválido")
        void CT_TRT_003_insertTraceTableError_shouldReturn404ForUnknownUser() {
            User professor = testDataSupport.prepararProfessor("trt-professor-003@test.com");
            Theme theme = testDataSupport.prepararTema("Tema TRT 003", professor.getEmail());

            given()
                    .header("Authorization", "Bearer " + ApiTestSupport.obterTokenAdmin())
                    .multiPart("traceTableRequest", TRACE_JSON, MediaType.APPLICATION_JSON_VALUE)
                    .multiPart("image", "test.png", "test-image".getBytes(StandardCharsets.UTF_8), MediaType.IMAGE_PNG_VALUE)
                    .queryParam("themesIds", theme.getId())
            .when()
                    .post(BASE_PATH + "/{userId}", 999999L)
            .then()
                    .statusCode(404);
        }

        @Test
        @DisplayName("CT-TRT-004: Criar trace table com temas inexistentes")
        void CT_TRT_004_insertTraceTableError_shouldReturn404ForUnknownTheme() {
            User professor = testDataSupport.prepararProfessor("trt-professor-004@test.com");

            given()
                    .header("Authorization", "Bearer " + ApiTestSupport.obterTokenAdmin())
                    .multiPart("traceTableRequest", TRACE_JSON, MediaType.APPLICATION_JSON_VALUE)
                    .multiPart("image", "test.png", "test-image".getBytes(StandardCharsets.UTF_8), MediaType.IMAGE_PNG_VALUE)
                    .queryParam("themesIds", 999999L)
            .when()
                    .post(BASE_PATH + "/{userId}", professor.getId())
            .then()
                    .statusCode(404);
        }

        @Test
        @DisplayName("CT-TRT-005: Criar trace table com imagem vazia, maior que 1 MB ou falha de upload")
        void CT_TRT_005_insertTraceTableError_shouldReturn400ForEmptyImage() {
            User professor = testDataSupport.prepararProfessor("trt-professor-005@test.com");
            Theme theme = testDataSupport.prepararTema("Tema TRT 005", professor.getEmail());

            given()
                    .header("Authorization", "Bearer " + ApiTestSupport.obterToken(professor.getEmail(), "password"))
                    .multiPart("traceTableRequest", TRACE_JSON, MediaType.APPLICATION_JSON_VALUE)
                    .multiPart("image", "empty.png", new byte[0], MediaType.IMAGE_PNG_VALUE)
                    .queryParam("themesIds", theme.getId())
            .when()
                    .post(BASE_PATH + "/{userId}", professor.getId())
            .then()
                    .log().all()
                    .statusCode(400);
        }

        @Test
        @DisplayName("CT-TRT-006: Criar trace table sem token ou com token inválido")
        void CT_TRT_006_insertTraceTableError_shouldReturn403WithoutToken() {
            User professor = testDataSupport.prepararProfessor("trt-professor-006@test.com");
            Theme theme = testDataSupport.prepararTema("Tema TRT 006", professor.getEmail());

            given()
                    .multiPart("traceTableRequest", TRACE_JSON, MediaType.APPLICATION_JSON_VALUE)
                    .multiPart("image", "test.png", "image".getBytes(StandardCharsets.UTF_8), MediaType.IMAGE_PNG_VALUE)
                    .queryParam("themesIds", theme.getId())
            .when()
                    .post(BASE_PATH + "/{userId}", professor.getId())
            .then()
                    .statusCode(403);
        }

        @Test
        @DisplayName("CT-TRT-007: Usuário criar trace table usando userId de outra pessoa")
        void CT_TRT_007_insertTraceTable_shouldRegisterCurrentAuthorizationBehavior() {
            User professor = testDataSupport.prepararProfessor("trt-professor-007@test.com");
            User student = testDataSupport.prepararUsuario("trt-student-007@test.com");
            Theme theme = testDataSupport.prepararTema("Tema TRT 007", professor.getEmail());

            given()
                    .header("Authorization", "Bearer " + ApiTestSupport.obterToken(student.getEmail(), "password"))
                    .multiPart("traceTableRequest", TRACE_JSON, MediaType.APPLICATION_JSON_VALUE)
                    .multiPart("image", "test.png", "test-image".getBytes(StandardCharsets.UTF_8), MediaType.IMAGE_PNG_VALUE)
                    .queryParam("themesIds", theme.getId())
            .when()
                    .post(BASE_PATH + "/{userId}", professor.getId())
            .then()
                    .statusCode(403);
        }

        @Test
        @DisplayName("CT-TRT-008: Criar trace table com Content-Type diferente de multipart/form-data")
        void CT_TRT_008_insertTraceTableError_shouldRejectNonMultipartRequest() {
            User professor = testDataSupport.prepararProfessor("trt-professor-008@test.com");

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + ApiTestSupport.obterTokenAdmin())
                    .body(TRACE_JSON)
            .when()
                    .post(BASE_PATH + "/{userId}", professor.getId())
            .then()
                    .statusCode(400);
        }
    }

    @Nested
    class findTraceTable {

        @Test
        @DisplayName("CT-TRT-009: Buscar trace table existente por ID")
        void CT_TRT_009_findByIdSuccessfully_shouldReturn200() {
            TraceTable trace = testDataSupport.prepararTraceTable("Exercício 009", "trt-professor-009@test.com", "Tema TRT 009");

            given()
                    .accept(ContentType.JSON)
            .when()
                    .get(BASE_PATH + "/{traceId}", trace.getId())
            .then()
                    .statusCode(200)
                    .body("id", equalTo(trace.getId().intValue()))
                    .body("exerciseName", equalTo(trace.getExerciseName()));
        }

        @Test
        @DisplayName("CT-TRT-010: Buscar trace table inexistente ou com ID inválido")
        void CT_TRT_010_findByIdError_shouldReturn404() {
            given()
                    .accept(ContentType.JSON)
            .when()
                    .get(BASE_PATH + "/{traceId}", 999999L)
            .then()
                    .statusCode(404);
        }

        @Test
        @DisplayName("CT-TRT-011: Listar trace tables de usuário existente")
        void CT_TRT_011_findAllByUserSuccessfully_shouldReturnPage() {
            TraceTable trace = testDataSupport.prepararTraceTable("Exercício 011", "trt-professor-011@test.com", "Tema TRT 011");

            given()
                    .accept(ContentType.JSON)
            .when()
                    .get(BASE_PATH + "/user/{userId}", trace.getCreator().getId())
            .then()
                    .statusCode(200)
                    .body("content", not(empty()))
                    .body("content[0].id", notNullValue());
        }

        @Test
        @DisplayName("CT-TRT-012: Listar trace tables de usuário sem resultados")
        void CT_TRT_012_findAllByUserSuccessfully_shouldReturnEmptyPage() {
            User professor = testDataSupport.prepararProfessor("trt-professor-012@test.com");

            given()
                    .accept(ContentType.JSON)
            .when()
                    .get(BASE_PATH + "/user/{userId}", professor.getId())
            .then()
                    .statusCode(200)
                    .body("content", empty());
        }

        @Test
        @DisplayName("CT-TRT-013: Listar trace tables com usuário inexistente ou ID inválido")
        void CT_TRT_013_findAllByUserError_shouldReturn404() {
            given()
                    .accept(ContentType.JSON)
            .when()
                    .get(BASE_PATH + "/user/{userId}", 999999L)
            .then()
                    .statusCode(404);
        }

        @Test
        @DisplayName("CT-TRT-014: Listar trace tables de usuário sem autenticação")
        void CT_TRT_014_findAllByUser_shouldBePublic() {
            TraceTable trace = testDataSupport.prepararTraceTable("Exercício 015", "trt-professor-015@test.com", "Tema TRT 015");

            given()
                    .accept(ContentType.JSON)
            .when()
                    .get(BASE_PATH + "/user/{userId}", trace.getCreator().getId())
            .then()
                    .statusCode(200);
        }
    }

    @Nested
    class findTraceTableByTheme {

        @Test
        @DisplayName("CT-TRT-015: Buscar trace tables por tema e professor com filtros válidos")
        void CT_TRT_015_findAllByThemeNameSuccessfully_shouldReturnMatchingPage() {
            TraceTable trace = testDataSupport.prepararTraceTable("Exercício 016", "trt-professor-016@test.com", "Tema TRT 016");
            String themeName = trace.getThemes().get(0).getName();

            given()
                    .accept(ContentType.JSON)
                    .queryParam("prof", trace.getCreator().getId())
                    .when()
                    .get(BASE_PATH + "/theme/name/{themeName}", themeName)
                    .then()
                    .statusCode(200)
                    .body("content", not(empty()))
                    .body("content[0].id", equalTo(trace.getId().intValue()));
        }

        @Test
        @DisplayName("CT-TRT-016: Buscar trace tables por tema inexistente")
        void CT_TRT_016_findAllByThemeNameError_shouldReturn404() {
            given()
                    .accept(ContentType.JSON)
                    .queryParam("prof", 1)
                    .when()
                    .get(BASE_PATH + "/theme/name/{themeName}", "Tema inexistente TRT")
                    .then()
                    .statusCode(404);
        }

        @Test
        @DisplayName("CT-TRT-017: Omitir ou informar incorretamente prof")
        void CT_TRT_017_findAllByThemeNameError_shouldReturn400WithoutProfessor() {
            TraceTable trace = testDataSupport.prepararTraceTable("Exercício 018", "trt-professor-018@test.com", "Tema TRT 018");

            given()
                    .accept(ContentType.JSON)
                    .queryParam("prof", "invalid")
                    .when()
                    .get(BASE_PATH + "/theme/name/{themeName}", trace.getThemes().get(0).getName())
                    .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("CT-TRT-018: Listar trace tables por tema existente")
        void CT_TRT_018_findAllByThemeSuccessfully_shouldReturnMatchingPage() {
            TraceTable trace = testDataSupport.prepararTraceTable("Exercício 020", "trt-professor-020@test.com", "Tema TRT 020");

            given()
                    .accept(ContentType.JSON)
                    .when()
                    .get(BASE_PATH + "/theme/{themeId}", trace.getThemes().get(0).getId())
                    .then()
                    .statusCode(200)
                    .body("content", not(empty()))
                    .body("content[0].id", equalTo(trace.getId().intValue()));
        }

        @Test
        @DisplayName("CT-TRT-019: Tema sem trace tables")
        void CT_TRT_019_findAllByThemeSuccessfully_shouldReturnEmptyPage() {
            User professor = testDataSupport.prepararProfessor("trt-professor-021@test.com");
            Theme theme = testDataSupport.prepararTema("Tema TRT 021", professor.getEmail());

            given()
                    .accept(ContentType.JSON)
                    .when()
                    .get(BASE_PATH + "/theme/{themeId}", theme.getId())
                    .then()
                    .statusCode(200)
                    .body("content", empty());
        }

        @Test
        @DisplayName("CT-TRT-020: Informar tema inexistente ou ID inválido")
        void CT_TRT_020_findAllByThemeError_shouldReturn404() {
            given()
                    .accept(ContentType.JSON)
                    .when()
                    .get(BASE_PATH + "/theme/{themeId}", 999999L)
                    .then()
                    .statusCode(404);
        }
    }

    @Nested
    class removeTraceTable {

        @Test
        @DisplayName("CT-TRT-021: Criador remover a própria trace table")
        void CT_TRT_021_removeTraceTableSuccessfully_shouldReturn204() {
            TraceTable trace = testDataSupport.prepararTraceTable("Exercício 024", "trt-professor-024@test.com", "Tema TRT 024");

            given()
                    .header("Authorization", "Bearer " + ApiTestSupport.obterToken("trt-professor-024@test.com", "password"))
            .when()
                    .delete(BASE_PATH + "/{traceId}/{userId}", trace.getId(), trace.getCreator().getId())
            .then()
                    .statusCode(204);
        }

        @Test
        @DisplayName("CT-TRT-022: Remover trace table com IDs inexistentes/inválidos")
        void CT_TRT_022_removeTraceTableError_shouldReturn404() {
            User professor = testDataSupport.prepararProfessor("trt-professor-025@test.com");

            given()
                    .header("Authorization", "Bearer " + ApiTestSupport.obterTokenAdmin())
                    .when()
                    .delete(BASE_PATH + "/{traceId}/{userId}", 999999L, professor.getId())
                    .then()
                    .statusCode(404);
        }

        @Test
        @DisplayName("CT-TRT-023: Usuário comum remover trace table de outro criador")
        void CT_TRT_023_removeTraceTableError_shouldRejectAnotherCreator() {
            TraceTable trace = testDataSupport.prepararTraceTable("Exercício 026", "trt-professor-026@test.com", "Tema TRT 026");
            User student = testDataSupport.prepararUsuario("trt-student-026@test.com");

            given()
                    .header("Authorization", "Bearer " + ApiTestSupport.obterToken(student.getEmail(), "password"))
                    .when()
                    .delete(BASE_PATH + "/{traceId}/{userId}", trace.getId(), student.getId())
                    .then()
                    .statusCode(403);
        }

        @Test
        @DisplayName("CT-TRT-024: Remover trace table sem token ou com token inválido")
        void CT_TRT_024_removeTraceTableError_shouldReturn403WithoutToken() {
            TraceTable trace = testDataSupport.prepararTraceTable("Exercício 027", "trt-professor-027@test.com", "Tema TRT 027");

            given()
                    .when()
                    .delete(BASE_PATH + "/{traceId}/{userId}", trace.getId(), trace.getCreator().getId())
                    .then()
                    .statusCode(403);
        }

        @Test
        @DisplayName("CT-TRT-025: Usuário não proprietário usar userId do proprietário na remoção")
        void CT_TRT_025_removeTraceTable_shouldRegisterCurrentAuthorizationBehavior() {
            TraceTable trace = testDataSupport.prepararTraceTable("Exercício 028", "trt-professor-028@test.com", "Tema TRT 028");
            User student = testDataSupport.prepararUsuario("trt-student-028@test.com");

            given()
                    .header("Authorization", "Bearer " + ApiTestSupport.obterToken(student.getEmail(), "password"))
            .when()
                    .delete(BASE_PATH + "/{traceId}/{userId}", trace.getId(), trace.getCreator().getId())
            .then()
                    .statusCode(403);
        }
    }

    @Nested
    class updateTraceTable {

        @Test
        @DisplayName("CT-TRT-026: Atualizar trace table com payload e temas existentes")
        void CT_TRT_026_updateTraceTableSuccessfully_shouldReturn200() {
            TraceTable trace = testDataSupport.prepararTraceTable("Exercício 029", "trt-professor-029@test.com", "Tema TRT 029");

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + ApiTestSupport.obterTokenAdmin())
                    .body(TRACE_JSON.replace("Exercício de teste", "Exercício atualizado"))
                    .queryParam("themesIds", trace.getThemes().get(0).getId())
                    .when()
                    .put(BASE_PATH + "/{traceId}/{userId}", trace.getId(), trace.getCreator().getId())
                    .then()
                    .statusCode(200)
                    .body("exerciseName", equalTo("Exercício atualizado"));
        }

        @Test
        @DisplayName("CT-TRT-027: Atualizar trace table informando lista vazia de temas")
        void CT_TRT_027_updateTraceTableError_shouldRejectEmptyThemeParameter() {
            TraceTable trace = testDataSupport.prepararTraceTable("Exercício 030", "trt-professor-030@test.com", "Tema TRT 030");

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + ApiTestSupport.obterTokenAdmin())
                    .body(TRACE_JSON)
                    .when()
                    .put(BASE_PATH + "/{traceId}/{userId}", trace.getId(), trace.getCreator().getId())
                    .then()
                    .statusCode(anyOf(is(400), is(403)));
        }

        @Test
        @DisplayName("CT-TRT-028: Atualizar trace table com themesIds inexistentes ou omitidos")
        void CT_TRT_028_updateTraceTableError_shouldReturn404ForUnknownTheme() {
            TraceTable trace = testDataSupport.prepararTraceTable("Exercício 031", "trt-professor-031@test.com", "Tema TRT 031");

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + ApiTestSupport.obterTokenAdmin())
                    .body(TRACE_JSON)
                    .queryParam("themesIds", 999999L)
                    .when()
                    .put(BASE_PATH + "/{traceId}/{userId}", trace.getId(), trace.getCreator().getId())
                    .then()
                    .statusCode(404);
        }

        @Test
        @DisplayName("CT-TRT-029: Atualizar trace table com body malformado ou campos inválidos")
        void CT_TRT_029_updateTraceTableError_shouldReturn400ForInvalidBody() {
            TraceTable trace = testDataSupport.prepararTraceTable("Exercício 032", "trt-professor-032@test.com", "Tema TRT 032");

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + ApiTestSupport.obterTokenAdmin())
                    .body("{malformed")
                    .queryParam("themesIds", trace.getThemes().get(0).getId())
                    .when()
                    .put(BASE_PATH + "/{traceId}/{userId}", trace.getId(), trace.getCreator().getId())
                    .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("CT-TRT-030: Atualizar trace table com trace table, usuário ou IDs inexistentes/inválidos")
        void CT_TRT_030_updateTraceTableError_shouldReturn404ForUnknownTrace() {
            User professor = testDataSupport.prepararProfessor("trt-professor-033@test.com");
            Theme theme = testDataSupport.prepararTema("Tema TRT 033", professor.getEmail());

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + ApiTestSupport.obterTokenAdmin())
                    .body(TRACE_JSON)
                    .queryParam("themesIds", theme.getId())
                    .when()
                    .put(BASE_PATH + "/{traceId}/{userId}", 999999L, professor.getId())
                    .then()
                    .statusCode(404);
        }

        @Test
        @DisplayName("CT-TRT-031: Atualizar trace table sem token ou com token inválido")
        void CT_TRT_031_updateTraceTableError_shouldReturn403WithoutToken() {
            TraceTable trace = testDataSupport.prepararTraceTable("Exercício 034", "trt-professor-034@test.com", "Tema TRT 034");

            given()
                    .contentType(ContentType.JSON)
                    .body(TRACE_JSON)
                    .queryParam("themesIds", trace.getThemes().get(0).getId())
                    .when()
                    .put(BASE_PATH + "/{traceId}/{userId}", trace.getId(), trace.getCreator().getId())
                    .then()
                    .statusCode(403);
        }

        @Test
        @DisplayName("CT-TRT-032: Usuário comum atualizar trace table de outro criador")
        void CT_TRT_032_updateTraceTableError_shouldRejectAnotherCreator() {
            TraceTable trace = testDataSupport.prepararTraceTable("Exercício 035", "trt-professor-035@test.com", "Tema TRT 035");
            User student = testDataSupport.prepararUsuario("trt-student-035@test.com");

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + ApiTestSupport.obterToken(student.getEmail(), "password"))
                    .body(TRACE_JSON)
                    .queryParam("themesIds", trace.getThemes().get(0).getId())
                    .when()
                    .put(BASE_PATH + "/{traceId}/{userId}", trace.getId(), student.getId())
                    .then()
                    .statusCode(403);
        }

        @Test
        @DisplayName("CT-TRT-033: Usuário não proprietário usar userId do proprietário na atualização")
        void CT_TRT_033_updateTraceTable_shouldRegisterCurrentAuthorizationBehavior() {
            TraceTable trace = testDataSupport.prepararTraceTable("Exercício 036", "trt-professor-036@test.com", "Tema TRT 036");
            User student = testDataSupport.prepararUsuario("trt-student-036@test.com");

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + ApiTestSupport.obterToken(student.getEmail(), "password"))
                    .body(TRACE_JSON.replace("Exercício de teste", "Atualização por bypass"))
                    .queryParam("themesIds", trace.getThemes().get(0).getId())
            .when()
                    .put(BASE_PATH + "/{traceId}/{userId}", trace.getId(), trace.getCreator().getId())
            .then()
                    .statusCode(403);
        }
    }

    @Nested
    class checkUserAnswer {

        @Test
        @DisplayName("CT-TRT-034: Validar resposta com tabela completamente correta")
        void CT_TRT_034_checkUserAnswerSuccessfully_shouldReturn200() {
            TraceTableRequest request = new TraceTableRequest("Exercício 037", List.of("Valor"), List.of(List.of("20")), List.of(List.of("20")), List.of(List.of("int")), "Java");
            TraceTable trace = testDataSupport.prepararTraceTable(request, "trt-professor-037@test.com", "Tema TRT 037");

            given()
                    .contentType(ContentType.JSON)
                    .body(List.of(List.of("20")))
                    .when()
                    .post(BASE_PATH + "/check/{traceId}", trace.getId())
                    .then()
                    .statusCode(200);
        }

        @Test
        @DisplayName("CT-TRT-035: Enviar valor diferente em célula cujo esperado é #")
        void CT_TRT_035_checkUserAnswer_shouldIgnoreWildcardCell() {
            TraceTableRequest request = new TraceTableRequest("Exercício 038", List.of("Valor", "Outro"), List.of(List.of("x", "20")), List.of(List.of("#", "20")), List.of(List.of("string", "int")), "Java");
            TraceTable trace = testDataSupport.prepararTraceTable(request, "trt-professor-038@test.com", "Tema TRT 038");

            given()
                    .contentType(ContentType.JSON)
                    .body(List.of(List.of("qualquer valor", "20")))
                    .when()
                    .post(BASE_PATH + "/check/{traceId}", trace.getId())
                    .then()
                    .statusCode(200);
        }

        @Test
        @DisplayName("CT-TRT-036: Enviar valor incorreto ou tipo inválido")
        void CT_TRT_036_checkUserAnswerError_shouldReturnCellErrors() {
            TraceTableRequest request = new TraceTableRequest("Exercício 039", List.of("Valor"), List.of(List.of("20")), List.of(List.of("20")), List.of(List.of("int")), "Java");
            TraceTable trace = testDataSupport.prepararTraceTable(request, "trt-professor-039@test.com", "Tema TRT 039");

            given()
                    .contentType(ContentType.JSON)
                    .body(List.of(List.of("valor inválido")))
                    .when()
                    .post(BASE_PATH + "/check/{traceId}", trace.getId())
                    .then()
                    .statusCode(400)
                    .body("[0].row", equalTo(0))
                    .body("[0].column", equalTo(0))
                    .body("[0].errorMessage", equalTo("Tipo inválido"));
        }

        @Test
        @DisplayName("CT-TRT-037: Validar representantes de string, int, double, float e boolean")
        void CT_TRT_037_checkUserAnswerSuccessfully_shouldValidateSupportedTypes() {
            List<String> values = List.of("texto", "10", "10.5", "10.5", "true");
            TraceTableRequest request = new TraceTableRequest("Exercício 040", values, List.of(values), List.of(values), List.of(List.of("string", "int", "double", "float", "boolean")), "Java");
            TraceTable trace = testDataSupport.prepararTraceTable(request, "trt-professor-040@test.com", "Tema TRT 040");

            given()
                    .contentType(ContentType.JSON)
                    .body(List.of(values))
                    .when()
                    .post(BASE_PATH + "/check/{traceId}", trace.getId())
                    .then()
                    .statusCode(200);
        }

        @Test
        @DisplayName("CT-TRT-038: Validar trace table inexistente ou tipo de célula desconhecido")
        void CT_TRT_038_checkUserAnswerError_shouldReturnAppropriateError() {
            given()
                    .contentType(ContentType.JSON)
                    .body(List.of(List.of("20")))
                    .when()
                    .post(BASE_PATH + "/check/{traceId}", 999999L)
                    .then()
                    .statusCode(404);
        }

        @Test
        @DisplayName("CT-TRT-039: Validar body malformado, nulo ou com dimensões incompatíveis")
        void CT_TRT_039_checkUserAnswerError_shouldRejectIncompatibleDimensions() {
            TraceTable trace = testDataSupport.prepararTraceTable("Exercício 042", "trt-professor-042@test.com", "Tema TRT 042");

            given()
                    .contentType(ContentType.JSON)
                    .body(List.of(List.of("20")))
                    .when()
                    .post(BASE_PATH + "/check/{traceId}", trace.getId())
                    .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("CT-TRT-040: Validar formato da lista de erros")
        void CT_TRT_040_checkUserAnswerError_shouldReturnRowColumnAndMessage() {
            TraceTableRequest request = new TraceTableRequest("Exercício 043", List.of("Valor"), List.of(List.of("20")), List.of(List.of("20")), List.of(List.of("int")), "Java");
            TraceTable trace = testDataSupport.prepararTraceTable(request, "trt-professor-043@test.com", "Tema TRT 043");

            given()
                    .contentType(ContentType.JSON)
                    .body(List.of(List.of("21")))
                    .when()
                    .post(BASE_PATH + "/check/{traceId}", trace.getId())
                    .then()
                    .statusCode(400)
                    .body("[0]", allOf(hasKey("row"), hasKey("column"), hasKey("errorMessage")));
        }

        @Test
        @DisplayName("CT-TRT-041: Validar resposta sem autenticação")
        void CT_TRT_041_checkUserAnswer_shouldBePublic() {
            TraceTableRequest request = new TraceTableRequest("Exercício 044", List.of("Valor", "Resultado"), List.of(List.of("x", "20")), List.of(List.of("x", "20")), List.of(List.of("string", "int")), "Java");
            TraceTable trace = testDataSupport.prepararTraceTable(request, "trt-professor-044@test.com", "Tema TRT 044");

            given()
                    .contentType(ContentType.JSON)
                    .body(List.of(List.of("x", "20")))
                    .when()
                    .post(BASE_PATH + "/check/{traceId}", trace.getId())
                    .then()
                    .statusCode(200);
        }
    }

    @Nested
    class submitAnswer {

        @Test
        @DisplayName("CT-TRT-042: Submeter resposta para trace table existente")
        void CT_TRT_042_submitAnswerSuccessfully_shouldReturn200() {
            TraceTable trace = testDataSupport.prepararTraceTable("Exercício 045", "trt-professor-045@test.com", "Tema TRT 045");
            User student = testDataSupport.prepararUsuario("trt-student-045@test.com");

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + ApiTestSupport.obterToken(student.getEmail(), "password"))
                    .body(List.of(List.of("20")))
                    .when()
                    .post(BASE_PATH + "/submit/{traceId}", trace.getId())
                    .then()
                    .statusCode(200);
        }

        @Test
        @DisplayName("CT-TRT-043: Submeter resposta incorreta ou matriz vazia")
        void CT_TRT_043_submitAnswer_shouldPersistCurrentEndpointBehavior() {
            TraceTable trace = testDataSupport.prepararTraceTable("Exercício 046", "trt-professor-046@test.com", "Tema TRT 046");
            User student = testDataSupport.prepararUsuario("trt-student-046@test.com");

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + ApiTestSupport.obterToken(student.getEmail(), "password"))
                    .body(List.of())
                    .when()
                    .post(BASE_PATH + "/submit/{traceId}", trace.getId())
                    .then()
                    .statusCode(200);
        }

        @Test
        @DisplayName("CT-TRT-044: Submeter resposta sem token ou com token inválido")
        void CT_TRT_044_submitAnswerError_shouldReturn403WithoutToken() {
            TraceTable trace = testDataSupport.prepararTraceTable("Exercício 047", "trt-professor-047@test.com", "Tema TRT 047");

            given()
                    .contentType(ContentType.JSON)
                    .body(List.of(List.of("20")))
            .when()
                    .post(BASE_PATH + "/submit/{traceId}", trace.getId())
            .then()
                    .statusCode(403);
        }

        @Test
        @DisplayName("CT-TRT-045: Submeter resposta para trace table inexistente")
        void CT_TRT_045_submitAnswerError_shouldReturnServerErrorForUnknownTrace() {
            User student = testDataSupport.prepararUsuario("trt-student-048@test.com");

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + ApiTestSupport.obterToken(student.getEmail(), "password"))
                    .body(List.of(List.of("20")))
            .when()
                    .post(BASE_PATH + "/submit/{traceId}", 999999L)
            .then()
                    .log().all()
                    .statusCode(404);
        }

        @Test
        @DisplayName("CT-TRT-046: Submeter resposta com body malformado ou estrutura inválida")
        void CT_TRT_046_submitAnswerError_shouldReturn400ForMalformedBody() {
            TraceTable trace = testDataSupport.prepararTraceTable("Exercício 049", "trt-professor-049@test.com", "Tema TRT 049");
            User student = testDataSupport.prepararUsuario("trt-student-049@test.com");

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + ApiTestSupport.obterToken(student.getEmail(), "password"))
                    .body("{malformed")
            .when()
                    .post(BASE_PATH + "/submit/{traceId}", trace.getId())
            .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("CT-TRT-047: Verificar associação da resposta ao usuário autenticado")
        void CT_TRT_047_submitAnswer_shouldAssociateAuthenticatedUser() {
            TraceTable trace = testDataSupport.prepararTraceTable("Exercício 050", "trt-professor-050@test.com", "Tema TRT 050");
            User student = testDataSupport.prepararUsuario("trt-student-050@test.com");

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + ApiTestSupport.obterToken(student.getEmail(), "password"))
                    .body(List.of(List.of("20")))
            .when()
                    .post(BASE_PATH + "/submit/{traceId}", trace.getId())
            .then()
                    .statusCode(200);

            List<Answer> answers = answerRepository.findByTraceTableId(trace.getId());
            assertTrue(answers.stream().anyMatch(answer -> answer.getStudent().getId().equals(student.getId())));
        }
    }

    @Nested
    class getAnswers {

        @Test
        @DisplayName("CT-TRT-048: Consultar todas as respostas do exercício")
        void CT_TRT_048_getAnswersByExerciseSuccessfully_shouldReturnAnswers() {
            TraceTable trace = testDataSupport.prepararTraceTable("Exercício 051", "trt-professor-051@test.com", "Tema TRT 051");
            User student = testDataSupport.prepararUsuario("trt-student-051@test.com");

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + ApiTestSupport.obterToken(student.getEmail(), "password"))
                    .body(List.of(List.of("20")))
            .when()
                    .post(BASE_PATH + "/submit/{traceId}", trace.getId())
            .then()
                    .statusCode(200);

            given()
                    .accept(ContentType.JSON)
            .when()
                    .get(BASE_PATH + "/answers/{traceId}", trace.getId())
            .then()
                    .statusCode(200)
                    .body("[0].studentName", equalTo(student.getName()))
                    .body("[0].exerciseName", equalTo(trace.getExerciseName()));
        }

        @Test
        @DisplayName("CT-TRT-049: Filtrar respostas por período")
        void CT_TRT_049_getAnswersByExerciseSuccessfully_shouldFilterByPeriod() {
            TraceTable trace = testDataSupport.prepararTraceTable("Exercício 052", "trt-professor-052@test.com", "Tema TRT 052");
            User student = testDataSupport.prepararUsuario("trt-student-052@test.com");

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + ApiTestSupport.obterToken(student.getEmail(), "password"))
                    .body(List.of(List.of("20")))
            .when()
                    .post(BASE_PATH + "/submit/{traceId}", trace.getId())
            .then()
                    .statusCode(200);

            LocalDateTime now = LocalDateTime.now();

            given()
                    .accept(ContentType.JSON)
                    .queryParam("startDate", now.minusDays(1).toString())
                    .queryParam("endDate", now.plusDays(1).toString())
            .when()
                    .get(BASE_PATH + "/answers/{traceId}", trace.getId())
            .then()
                    .statusCode(200)
                    .body("[0].studentName", equalTo(student.getName()));
        }

        @Test
        @DisplayName("CT-TRT-050: Filtrar respostas por aluno")
        void CT_TRT_050_getAnswersByExerciseSuccessfully_shouldFilterByStudent() {
            TraceTable trace = testDataSupport.prepararTraceTable("Exercício 053", "trt-professor-053@test.com", "Tema TRT 053");
            User student = testDataSupport.prepararUsuario("trt-student-053@test.com");
            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + ApiTestSupport.obterToken(student.getEmail(), "password"))
                    .body(List.of(List.of("20")))
            .when()
                    .post(BASE_PATH + "/submit/{traceId}", trace.getId())
            .then()
                    .statusCode(200);

            given()
                    .accept(ContentType.JSON)
                    .queryParam("studentName", student.getName())
            .when()
                    .get(BASE_PATH + "/answers/{traceId}", trace.getId())
            .then()
                    .statusCode(200)
                    .body("[0].studentName", equalTo(student.getName()));
        }

        @Test
        @DisplayName("CT-TRT-051: Filtrar respostas por período e aluno")
        void CT_TRT_051_getAnswersByExerciseSuccessfully_shouldCombineFilters() {
            TraceTable trace = testDataSupport.prepararTraceTable("Exercício 054", "trt-professor-054@test.com", "Tema TRT 054");
            User student = testDataSupport.prepararUsuario("trt-student-054@test.com");
            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + ApiTestSupport.obterToken(student.getEmail(), "password"))
                    .body(List.of(List.of("20")))
            .when()
                    .post(BASE_PATH + "/submit/{traceId}", trace.getId())
            .then()
                    .statusCode(200);
            LocalDateTime now = LocalDateTime.now();

            given()
                    .accept(ContentType.JSON)
                    .queryParam("startDate", now.minusDays(1).toString())
                    .queryParam("endDate", now.plusDays(1).toString())
                    .queryParam("studentName", student.getName())
            .when()
                    .get(BASE_PATH + "/answers/{traceId}", trace.getId())
            .then()
                    .statusCode(200)
                    .body("[0].studentName", equalTo(student.getName()));
        }

        @Test
        @DisplayName("CT-TRT-052: Consultar exercício sem respostas")
        void CT_TRT_052_getAnswersByExerciseSuccessfully_shouldReturnEmptyList() {
            TraceTable trace = testDataSupport.prepararTraceTable("Exercício 055", "trt-professor-055@test.com", "Tema TRT 055");

            given()
                    .accept(ContentType.JSON)
            .when()
                    .get(BASE_PATH + "/answers/{traceId}", trace.getId())
            .then()
                    .statusCode(200)
                    .body("$", empty());
        }

        @Test
        @DisplayName("CT-TRT-053: Consultar respostas com datas inválidas ou período invertido")
        void CT_TRT_053_getAnswersByExerciseError_shouldReturn400ForInvalidDate() {
            TraceTable trace = testDataSupport.prepararTraceTable("Exercício 056", "trt-professor-056@test.com", "Tema TRT 056");

            given()
                    .accept(ContentType.JSON)
                    .queryParam("startDate", "data-invalida")
            .when()
                    .get(BASE_PATH + "/answers/{traceId}", trace.getId())
            .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("CT-TRT-054: Consultar respostas informando somente uma das datas")
        void CT_TRT_054_getAnswersByExercise_shouldRegisterSingleDateBehavior() {
            TraceTable trace = testDataSupport.prepararTraceTable("Exercício 057", "trt-professor-057@test.com", "Tema TRT 057");
            User student = testDataSupport.prepararUsuario("trt-student-057@test.com");
            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + ApiTestSupport.obterToken(student.getEmail(), "password"))
                    .body(List.of(List.of("20")))
            .when()
                    .post(BASE_PATH + "/submit/{traceId}", trace.getId())
            .then()
                    .statusCode(200);

            given()
                    .accept(ContentType.JSON)
                    .queryParam("startDate", LocalDateTime.now().minusDays(1).toString())
            .when()
                    .get(BASE_PATH + "/answers/{traceId}", trace.getId())
            .then()
                    .statusCode(200)
                    .body("[0].studentName", equalTo(student.getName()));
        }

        @Test
        @DisplayName("CT-TRT-055: Consultar respostas de trace table inexistente")
        void CT_TRT_055_getAnswersByExercise_shouldReturnEmptyForUnknownTrace() {
            given()
                    .accept(ContentType.JSON)
            .when()
                    .get(BASE_PATH + "/answers/{traceId}", 999999L)
            .then()
                    .statusCode(404);
        }

        @Test
        @DisplayName("CT-TRT-056: Consultar respostas sem autenticação")
        void CT_TRT_056_getAnswersByExercise_shouldBePublic() {
            TraceTable trace = testDataSupport.prepararTraceTable("Exercício 059", "trt-professor-059@test.com", "Tema TRT 059");

            given()
                    .accept(ContentType.JSON)
            .when()
                    .get(BASE_PATH + "/answers/{traceId}", trace.getId())
            .then()
                    .statusCode(200);
        }
    }

    @Nested
    class getMetrics {

        @Test
        @DisplayName("CT-TRT-057: Consultar métricas sem filtros")
        void CT_TRT_057_getMetricsByExerciseSuccessfully_shouldReturnMetrics() {
            TraceTable trace = testDataSupport.prepararTraceTable("Exercício 060", "trt-professor-060@test.com", "Tema TRT 060");

            given()
                    .accept(ContentType.JSON)
                    .when()
                    .get(BASE_PATH + "/metrics/{traceId}", trace.getId())
                    .then()
                    .statusCode(200)
                    .body("totalSubmissions", equalTo(0))
                    .body("correctSubmissions", equalTo(0))
                    .body("incorrectSubmissions", equalTo(0))
                    .body("accuracy", equalTo(0.0F))
                    .body("errorRate", equalTo(0.0F));
        }
    }
}
