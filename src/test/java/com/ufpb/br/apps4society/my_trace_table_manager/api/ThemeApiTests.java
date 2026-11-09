package com.ufpb.br.apps4society.my_trace_table_manager.api;

import com.ufpb.br.apps4society.my_trace_table_manager.api.support.ApiTestSupport;
import com.ufpb.br.apps4society.my_trace_table_manager.api.support.TestDataSupport;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.Theme;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.TraceTable;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.User;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.role.Role;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ThemeApiTests {

    @Autowired
    private TestDataSupport testDataSupport;

    private static final String BASE_PATH = "/v1/theme";

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    class insertTheme {

        @Test
        @DisplayName("CT-THM-001: Criar tema para usuário existente")
        void CT_THM_001_insertThemeSuccessfully_shouldCreateThemeAndReturn200() {
            User professor = testDataSupport.prepararProfessor("professor-001@test.com");

            String theme = """
                    {
                        "name": "Tema de Teste 001"
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + ApiTestSupport.obterToken(professor.getEmail(), "password"))
                    .body(theme)
            .when()
                    .post(BASE_PATH + "/{id}", professor.getId())
            .then()
                    .log().all()
                    .statusCode(201)
                    .body("id", notNullValue())
                    .body("name", equalTo("Tema de Teste 001"))
                    .body("creator.id", equalTo((professor.getId().intValue())))
                    .body("creator.name", equalTo((professor.getName())))
                    .body("creator.email", equalTo((professor.getEmail())))
                    .body("creator.role", equalTo(professor.getRole().getRole()));
        }

        @Test
        @DisplayName("CT-THM-002: Criar tema para usuário inexistente")
        void CT_THM_002_insertThemeError_shouldReturn404() {
            String theme = """
                    {
                        "name": "Tema de Teste 002"
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + ApiTestSupport.obterTokenAdmin())
                    .body(theme)
            .when()
                    .post(BASE_PATH + "/{id}", 999999L)
            .then()
                    .statusCode(404);
        }

        @Test
        @DisplayName("CT-THM-003: Criar tema como nome ausente")
        void CT_THM_003_insertThemeError_shouldReturn400() {
            User professor = testDataSupport.prepararProfessor("professor-003@test.com");

            String theme = """
                    {
                        "name": ""
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + ApiTestSupport.obterTokenAdmin())
                    .body(theme)
            .when()
                    .post(BASE_PATH + "/{id}", professor.getId())
            .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("CT-THM-004: criar tema para outro usuário")
        void CT_THM_004_insertThemeError_shouldReturn403() {
            User professor = testDataSupport.prepararUsuario("usuario-004@test.com");
            User professor2 = testDataSupport.prepararProfessor("professor2-004@test.com");

            String theme = """
                    {
                        "name": "Tema de Teste 004"
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + ApiTestSupport.obterToken(professor.getEmail(), "password"))
                    .body(theme)
            .when()
                    .post(BASE_PATH + "/{id}", professor2.getId())
            .then()
                    .log().all()
                    .statusCode(403);
        }
    }

    @Nested
    class listThemes {

        @Test
        @DisplayName("CT-THM-005: listar temas com sucesso")
        void CT_THM_005_listThemesSuccessfully_shouldReturnListOfThemesAndReturn200() {
            User professor = testDataSupport.prepararProfessor("professor-005@test.com");
            testDataSupport.prepararTema("Tema de Teste 005", professor.getEmail());

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " +  ApiTestSupport.obterTokenAdmin())
            .when()
                    .get(BASE_PATH)
            .then()
                    .statusCode(200)
                    .body("$", notNullValue())
                    .body("$", not(empty()))
                    .body("$", everyItem(hasKey("id")))
                    .body("$", everyItem(hasKey("name")))
                    .body("$", everyItem(hasKey("creator")));
        }

        @Test
        @DisplayName("CT-THM-006: listar temas sem token (não logado) com sucesso")
        void CT_THM_006_listThemesError_shouldReturnE403() {
            given()
                    .contentType(ContentType.JSON)
            .when()
                    .get(BASE_PATH)
            .then()
                    .statusCode(403);
        }
    }

    @Nested
    class findTheme {
        @Test
        @DisplayName("CT-THM-007: buscar tema por id com sucesso")
        void CT_THM_007_findByIdSuccessfully_shouldReturnThemeAnd200() {
            Theme tema = testDataSupport.prepararTema("Tema de Teste 007", "professor-007@test.com");

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " +  ApiTestSupport.obterTokenAdmin())
            .when()
                    .get(BASE_PATH + "/{themeId}", tema.getId())
            .then()
                    .statusCode(200);
        }

        @Test
        @DisplayName("CT-THM-008: buscar tema inexistente")
        void CT_THM_008_findByIdError_shouldReturn404() {
            testDataSupport.prepararTema("Tema de Teste 008", "professor-008@test.com");

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " +  ApiTestSupport.obterTokenAdmin())
            .when()
                    .get(BASE_PATH + "/{themeId}", 999999L)
            .then()
                    .statusCode(404);
        }

        @Test
        @DisplayName("CT-THM-009: buscar tema sem autenticar")
        void CT_THM_009_findByIdError_shouldReturn403() {
            Theme tema = testDataSupport.prepararTema("Tema de Teste 009", "professor-009@test.com");

            given()
                    .contentType(ContentType.JSON)
            .when()
                    .get(BASE_PATH + "/{themeId}", tema.getId())
            .then()
                    .statusCode(403);
        }

        @Test
        @DisplayName("CT-THM-010: buscar tema por nome")
        void CT_THM_010_findByNameSuccessfully_shouldReturn200() {
            Theme tema = testDataSupport.prepararTema("Tema de Teste 010", "professor-010@test.com");

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + ApiTestSupport.obterTokenAdmin())
            .when()
                    .get(BASE_PATH + "/name/{themeName}", tema.getName())
            .then()
                    .statusCode(200)
                    .body("id", notNullValue())
                    .body("name", equalTo(tema.getName()))
                    .body("creator.id", notNullValue());
        }

        @Test
        @DisplayName("CT-THM-011: buscar tema inexistente por nome")
        void CT_THM_011_findByNameError_shouldReturn404() {
            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " +  ApiTestSupport.obterTokenAdmin())
            .when()
                    .get(BASE_PATH + "/name/{themeName}", "Tema inexistente")
            .then()
                    .statusCode(404);
        }

        @Test
        @DisplayName("CT-THM-012: buscar tema por nome sem token")
        void CT_THM_012_findByNameError_shouldReturn403() {
            Theme tema = testDataSupport.prepararTema("Tema de Teste 012", "professor-012@test.com");

            given()
                    .contentType(ContentType.JSON)
            .when()
                    .get(BASE_PATH + "/name/{themeName}", tema.getName())
            .then()
                    .statusCode(403);
        }
    }

    @Nested
    class listThemesByUser {
        @Test
        @DisplayName("CT-THM-013: buscar temas por usuários")
        void CT_THM_013_listByUserSuccessfully_shouldReturnListOfThemesAnd200() {
            User professor = testDataSupport.prepararProfessor("professor-013@test.com");
            testDataSupport.prepararTema("Tema de Teste 013", "professor-013@test.com");

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " +  ApiTestSupport.obterTokenAdmin())
            .when()
                    .get(BASE_PATH + "/user/{userId}", professor.getId())
            .then()
                    .statusCode(200)
                    .body("$", notNullValue())
                    .body("$", not(empty()))
                    .body("$", everyItem(hasKey("id")))
                    .body("$", everyItem(hasKey("name")))
                    .body("$", everyItem(hasKey("creator")));
        }

        @Test
        @DisplayName("CT-THM-014: busca vazia de temas por usuários")
        void CT_THM_014_listByUserSuccessfully_shouldReturnEmptyListAnd200() {
            User professor = testDataSupport.prepararProfessor("professor-014@test.com");

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " +  ApiTestSupport.obterTokenAdmin())
            .when()
                    .get(BASE_PATH + "/user/{userId}", professor.getId())
            .then()
                    .statusCode(200)
                    .body("$", notNullValue())
                    .body("$", empty());
        }

        @Test
        @DisplayName("CT-THM-015: busca de temas por usuários inexistente")
        void CT_THM_015_listByUserError_shouldReturn404() {
            User professor = testDataSupport.prepararProfessor("professor-015@test.com");

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " +  ApiTestSupport.obterTokenAdmin())
            .when()
                    .get(BASE_PATH + "/user/{userId}", 999999L)
            .then()
                    .statusCode(404);
        }

        @Test
        @DisplayName("CT-THM-016: busca de temas por usuário sem token")
        void CT_THM_016_listByUserError_shouldReturn404() {
            User professor = testDataSupport.prepararProfessor("professor-016@test.com");

            given()
                    .contentType(ContentType.JSON)
            .when()
                    .get(BASE_PATH + "/user/{userId}", 999999L)
            .then()
                    .statusCode(403);
        }
    }

    @Nested
    class listThemeByTraceTable {
        @Test
        @DisplayName("CT-THM-017: buscar temas do trace table")
        void CT_THM_017_listByTraceTableSuccessfully_shouldReturnListOfThemesAnd200() {
            User professor = testDataSupport.prepararProfessor("professor-017@test.com");
            Theme tema = testDataSupport.prepararTema("Tema de Teste 017", professor.getEmail());
            TraceTable trace = testDataSupport.prepararTraceTable("Trace de teste 017", professor.getId(), List.of(tema.getId()));

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + ApiTestSupport.obterTokenAdmin())
            .when()
                    .get(BASE_PATH + "/trace/{traceId}", trace.getId())
            .then()
                    .statusCode(200)
                    .body("$", notNullValue())
                    .body("$", not(empty()))
                    .body("content", everyItem(hasKey("id")))
                    .body("content", everyItem(hasKey("name")))
                    .body("content", everyItem(hasKey("creator")));
        }

        @Test
        @DisplayName("CT-THM-018: buscar temas de trace table inexistente")
        void CT_THM_018_listByTraceTableSuccessfully_shouldReturnEmptyPageAnd200() {
            User professor = testDataSupport.prepararProfessor("professor-018@test.com");
            Theme tema = testDataSupport.prepararTema("Tema de Teste 018", professor.getEmail());

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + ApiTestSupport.obterToken(professor.getEmail(), "password"))
            .when()
                    .get(BASE_PATH + "/trace/{traceId}", 999999L)
            .then()
                    .statusCode(404);
        }

        @Test
        @DisplayName("CT-THM-019: buscar temas de trace table sem token")
        void CT_THM_019_listByTraceTableError_shouldReturn403() {
            User professor = testDataSupport.prepararProfessor("professor-019@test.com");
            Theme tema = testDataSupport.prepararTema("Tema de Teste 019", professor.getEmail());

            given()
                    .contentType(ContentType.JSON)
            .when()
                    .get(BASE_PATH + "/trace/{traceId}", 999999L)
            .then()
                    .statusCode(403);
        }
    }

    @Nested
    class deleteTheme {

        @Test
        @DisplayName("CT-THM-020: deletar próprio tema")
        void CT_THM_020_deleteThemeSuccessfully_shouldReturn204() {
            User professor = testDataSupport.prepararProfessor("professor-020@test.com");
            Theme tema = testDataSupport.prepararTema("Tema de teste 020", professor.getEmail());
            String token = ApiTestSupport.obterToken(professor.getEmail(), "password");

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + token)
            .when()
                    .delete(BASE_PATH + "/{themeID}/{userId}", tema.getId(), professor.getId())
            .then()
                    .statusCode(204);
        }

        @Test
        @DisplayName("CT-THM-021: deletar tema com id inexistente")
        void CT_THM_021_deleteThemeError_shouldReturn404() {
            User professor = testDataSupport.prepararProfessor("professor-021@test.com");
            Theme tema = testDataSupport.prepararTema("Tema de teste 021", professor.getEmail());
            String token = ApiTestSupport.obterToken(professor.getEmail(), "password");

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + token)
            .when()
                    .delete(BASE_PATH + "/{themeID}/{userId}", 999999L, professor.getId())
            .then()
                    .statusCode(404);
        }

        @Test
        @DisplayName("CT-THM-022: deletar tema com id de usuário inexistente")
        void CT_THM_022_deleteThemeError_shouldReturn404() {
            User professor = testDataSupport.prepararProfessor("professor-022@test.com");
            Theme tema = testDataSupport.prepararTema("Tema de teste 022", professor.getEmail());
            String token = ApiTestSupport.obterToken(professor.getEmail(), "password");

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + token)
            .when()
                    .delete(BASE_PATH + "/{themeID}/{userId}", tema.getId(), 999999L)
            .then()
                    .statusCode(404);
        }

        @Test
        @DisplayName("CT-THM-023: usuário comum deleta tema")
        void CT_THM_023_deleteThemeError_shouldReturn403() {
            User professor = testDataSupport.prepararProfessor("professor-023@test.com");
            User aluno = testDataSupport.prepararUsuario("aluno2-023@test.com");
            Theme tema = testDataSupport.prepararTema("Tema de teste 023", professor.getEmail());
            String token = ApiTestSupport.obterToken(aluno.getEmail(), "password");

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + token)
            .when()
                    .delete(BASE_PATH + "/{themeID}/{userId}", tema.getId(), professor.getId())
            .then()
                    .statusCode(403);
        }

        @Test
        @DisplayName("CT-THM-024: deletar tema sem token")
        void CT_THM_024_deleteThemeError_shouldReturn403() {
            User professor = testDataSupport.prepararProfessor("professor-024@test.com");
            Theme tema = testDataSupport.prepararTema("Tema de teste 024", professor.getEmail());

            given()
                    .contentType(ContentType.JSON)
            .when()
                    .delete(BASE_PATH + "/{themeID}/{userId}", tema.getId(), professor.getId())
            .then()
                    .statusCode(403);
        }

        @Test
        @DisplayName("CT-THM-025: professor deleta tema de outro professor")
        void CT_THM_025_deleteThemeError_shouldReturn403() {
            User professor = testDataSupport.prepararProfessor("professor-025@test.com");
            User professor2 = testDataSupport.prepararUsuario("aluno2-025@test.com");
            Theme tema = testDataSupport.prepararTema("Tema de teste 025", professor.getEmail());
            String token = ApiTestSupport.obterToken(professor2.getEmail(), "password");

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + token)
            .when()
                    .delete(BASE_PATH + "/{themeID}/{userId}", tema.getId(), professor.getId())
            .then()
                    .statusCode(403);
        }

        @Test
        @DisplayName("CT-THM-026: deletar tema usado em Trace Table")
        void CT_THM_026_deleteThemeSuccessfully_shouldReturn204() {
            User professor = testDataSupport.prepararProfessor("professor-026@test.com");
            Theme tema = testDataSupport.prepararTema("Tema de teste 026", professor.getEmail());
            TraceTable trace = testDataSupport.prepararTraceTable("Trace de teste 026", professor.getId(), List.of(tema.getId()));

            String token = ApiTestSupport.obterToken(professor.getEmail(), "password");

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + token)
            .when()
                    .delete(BASE_PATH + "/{themeID}/{userId}", tema.getId(), professor.getId())
            .then()
                    .log().all()
                    .statusCode(204);
        }
    }

    @Nested
    class updateTheme {

        @Test
        @DisplayName("CT-THM-027: atualizar tema")
        void CT_THM_027_updateThemeSuccessfully_shouldReturn200() {
            User professor = testDataSupport.prepararProfessor("professor-027@test.com");
            Theme tema = testDataSupport.prepararTema("Tema de teste 027", professor.getEmail());
            String token = ApiTestSupport.obterToken(professor.getEmail(), "password");

            String request = """
                    {
                        "name": "Tema atualizado"
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + token)
                    .body(request)
            .when()
                    .put(BASE_PATH + "/{themeID}/{userId}", tema.getId(), professor.getId())
            .then()
                    .statusCode(200)
                    .body("name", equalTo("Tema atualizado"));
        }

        @Test
        @DisplayName("CT-THM-028: atualizar tema com nome inválido")
        void CT_THM_028_updateThemeError_shouldReturn400() {
            User professor = testDataSupport.prepararProfessor("professor-028@test.com");
            Theme tema = testDataSupport.prepararTema("Tema de teste 028", professor.getEmail());
            String token = ApiTestSupport.obterToken(professor.getEmail(), "password");

            String request = """
                    {
                        "name": ""
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + token)
                    .body(request)
            .when()
                    .put(BASE_PATH + "/{themeID}/{userId}", tema.getId(), professor.getId())
            .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("CT-THM-029: atualizar tema inexistente")
        void CT_THM_029_updateThemeError_shouldReturn404() {
            User professor = testDataSupport.prepararProfessor("professor-029@test.com");
            Theme tema = testDataSupport.prepararTema("Tema de teste 029", professor.getEmail());
            String token = ApiTestSupport.obterToken(professor.getEmail(), "password");

            String request = """
                    {
                        "name": "Tema atualizado 029"
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + token)
                    .body(request)
            .when()
                    .put(BASE_PATH + "/{themeID}/{userId}", 999999L, professor.getId())
            .then()
                    .statusCode(404);
        }

        @Test
        @DisplayName("CT-THM-030: usuário comum atualizar tema")
        void CT_THM_030_updateThemeError_shouldReturn403() {
            User professor = testDataSupport.prepararProfessor("professor-030@test.com");
            User aluno = testDataSupport.prepararUsuario("aluno-030@test.com");
            Theme tema = testDataSupport.prepararTema("Tema de teste 030", professor.getEmail());
            String token = ApiTestSupport.obterToken(aluno.getEmail(), "password");

            String request = """
                    {
                        "name": "Tema atualizado"
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + token)
                    .body(request)
            .when()
                    .put(BASE_PATH + "/{themeID}/{userId}", tema.getId(), professor.getId())
            .then()
                    .statusCode(403);
        }

        @Test
        @DisplayName("CT-THM-031: atualizar tema sem token")
        void CT_THM_031_updateThemeError_shouldReturn403() {
            User professor = testDataSupport.prepararProfessor("professor-031@test.com");
            Theme tema = testDataSupport.prepararTema("Tema de teste 031", professor.getEmail());

            String request = """
                    {
                        "name": "Tema atualizado"
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .body(request)
            .when()
                    .put(BASE_PATH + "/{themeID}/{userId}", tema.getId(), professor.getId())
            .then()
                    .statusCode(403);
        }

        @Test
        @DisplayName("CT-THM-032: atualizar tema de outro usuário")
        void CT_THM_032_updateThemeError_shouldReturn403() {
            User professor = testDataSupport.prepararProfessor("professor-032@test.com");
            Theme tema = testDataSupport.prepararTema("Tema de teste 032", professor.getEmail());
            User professor2 = testDataSupport.prepararProfessor("professor2-032@test.com");
            String token = ApiTestSupport.obterToken(professor2.getEmail(), "password");

            String request = """
                    {
                        "name": "Tema atualizado"
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + token)
                    .body(request)
            .when()
                    .put(BASE_PATH + "/{themeID}/{userId}", tema.getId(), professor.getId())
            .then()
                    .statusCode(403);
        }
    }
}
