package com.ufpb.br.apps4society.my_trace_table_manager.api;

import com.ufpb.br.apps4society.my_trace_table_manager.api.support.ApiTestSupport;
import com.ufpb.br.apps4society.my_trace_table_manager.api.support.TestDataSupport;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.User;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.VerificationCode;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.role.Role;
import com.ufpb.br.apps4society.my_trace_table_manager.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.context.ActiveProfiles;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static com.ufpb.br.apps4society.my_trace_table_manager.api.support.ApiTestSupport.obterTokenAdmin;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserApiTests {

    @Autowired
    private UserRepository userRepository;

    private static final String BASE_PATH = "/v1/user";

    @LocalServerPort
    private int port;
    @Autowired
    private TestDataSupport testDataSupport;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    class RegisterUserAdmin {

        @Test
        @DisplayName("CT-USR-001: Deve criar com sucesso o usuário e retornar o HTTP status code 201")
        void CT_USR_001_shouldCreateUserAndReturn201() {

            String request = """
                    {
                        "name": "Testes de API",
                        "email": "api.testes-001@teste.com",
                        "password": "senhaForte123",
                        "role": "ADMIN"
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + obterTokenAdmin())
                    .body(request)
            .when()
                    .post(BASE_PATH + "/register")
            .then()
                    .statusCode(201)
                    .body("id", notNullValue())
                    .body("name", equalTo("Testes de API"))
                    .body("email", equalTo("api.testes-001@teste.com"))
                    .body("role", equalTo("admin"));
        }

        @Test
        @DisplayName("CT-USR-002: Deve retornar HTTP Status Code 400 quando a requisição estiver malformada")
        void CT_USR_002_shouldReturn400WhenBodyIsNotCorrect() {
            String request1 = """
                    {
                        "name": "Testes de API",
                        "email": "",
                        "password": "senhaForte123",
                        "role": "ALUNO"
                    }
                    """;

            String request2 = """
                    {
                        "name": "Testes de API",
                        "email": "api.testes-002@teste.com",
                        "role": "ALUNO"
                    }
                    """;

            String request3 = """
                    {
                        "name": "Testes de API",
                        "email": "api.testes-002@teste.com",
                        "password": "senhaForte123",
                        "role":
                    }
                    """;

//            //campo vazio
            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + obterTokenAdmin())
                    .body(request1)
            .when()
                    .post(BASE_PATH + "/register")
            .then()
                    .statusCode(400);

//            //campo inexistente
            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + obterTokenAdmin())
                    .body(request2)
            .when()
                    .post(BASE_PATH + "/register")
            .then()
                    .statusCode(400);

//            //campo nulo
            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + obterTokenAdmin())
                    .body(request3)
            .when()
                    .post(BASE_PATH + "/register")
            .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("CT-USR-003: Deve retornar HTTP Status Code 400 quando request contiver campos fora dos limites")
        void CT_USR_003_shouldReturn400WhenRequestInvalid() {

            String requestInvalidEmail = """
                    {
                        "name": "Testes de API",
                        "email": "api.testesinvalidinvalidinvalidinvalidinvalidinvalidinvalidinvalidinvalidinvalidinvalidinvalidinvalidinvalidinvalidinvalidinvalidinvalidinvalidinvalidinvalid@teste.com",
                        "password": "senhaForte123",
                        "role": "ADMIN"
                    }
                    """;

            String requestInvalidPassword = """
                    {
                        "name": "Testes de API",
                        "email": "api.testes-003@teste.com",
                        "password": "123",
                        "role": "ADMIN"
                    }
                    """;

            String requestInvalidName = """
                    {
                        "name": "Testes de API com mais de 30 caractÃ©res no nome",
                        "email": "api.testes-003@teste.com",
                        "password": "123",
                        "role": "ADMIN"
                    }
                    """;

            String requestInvalidRole = """
                    {
                        "name": "Testes de API",
                        "email": "api.testes-003@teste.com",
                        "password": "123",
                        "role": "INVALID"
                    }
                    """;

//            //Email invÃ¡lido
            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + obterTokenAdmin())
                    .body(requestInvalidEmail)
            .when()
                    .post(BASE_PATH + "/register")
            .then()
                    .statusCode(400);

//            //Senha invÃ¡lido
            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + obterTokenAdmin())
                    .body(requestInvalidPassword)
            .when()
                    .post(BASE_PATH + "/register")
            .then()
                    .statusCode(400);

//            //Nome invÃ¡lido
            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + obterTokenAdmin())
                    .body(requestInvalidName)
            .when()
                    .post(BASE_PATH + "/register")
            .then()
                    .statusCode(400);

//            //Role invÃ¡lido
            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + obterTokenAdmin())
                    .body(requestInvalidRole)
            .when()
                    .post(BASE_PATH + "/register")
            .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("CT-USR-004: Deve retornar HTTP Status Code 400 quando email já tiver sido registrado")
        void CT_USR_004_shouldReturn400WhenEmailAlreadyExists() {
            testDataSupport.prepararUsuario("api.testes-004@teste.com");

            String request = """
                    {
                        "name": "Testes de API",
                        "email": "api.testes-004@teste.com",
                        "password": "senhaForte123",
                        "role": "ADMIN"
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + ApiTestSupport.obterToken("api.testes-004@teste.com", "password"))
                    .body(request)
            .when()
                    .post(BASE_PATH + "/register")
            .then()
                    .statusCode(400)
                    .body("message", equalTo("Tente se registrar com outro email"));
        }

        @Test
        @DisplayName("CT-USR-005: Deve retornar HTTP Status Code 403 quando admin não estiver logado")
        void CT_USR_005_shouldReturn203WhenAdminIsNotLoged() {

            String request = """
                    {
                        "name": "Testes de API",
                        "email": "api.testes-005@teste.com",
                        "password": "senhaForte123",
                        "role": "ALUNO"
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .body(request)
            .when()
                    .post(BASE_PATH + "/register")
            .then()
                    .statusCode(403);
        }

        @Test
        @DisplayName("CT-USR-006: Deve retornar HTTP Status Code 403 quando usuário logado não for ADMIN")
        void CT_USR_006_shouldReturn403WhenAlunoIsLoged() {
            testDataSupport.prepararUsuario("aluno-006@test.com");

            String request = """
                    {
                        "name": "Testes de API",
                        "email": "api.testes-006@teste.com",
                        "password": "senhaForte123",
                        "role": "ADMIN"
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + ApiTestSupport.obterToken("aluno-006@test.com", "password"))
                    .body(request)
            .when()
                    .post(BASE_PATH + "/register")
            .then()
                    .statusCode(403);
        }
    }

    @Nested
    class RegisterCommomUsers {

        @Test
        @DisplayName("CT-USR-007: Deve registrar aluno com dados e código válido")
        void CT_USR_007_RegAlunSucessfully_ShouldReturn201() {

            VerificationCode code = testDataSupport.prepararCodigoRegistro("api.testes-007@teste.com", "A007AA");
            String request = """
                    {
                        "name": "Testes de API",
                        "email": "api.testes-007@teste.com",
                        "password": "senhaForte123",
                        "role": "ALUNO"
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .body(request)
            .when()
                    .post(BASE_PATH + "/regAlun?code={code}", code.getCode())
            .then()
                    .statusCode(201)
                    .body("id", notNullValue())
                    .body("name", equalTo("Testes de API"))
                    .body("email", equalTo("api.testes-007@teste.com"))
                    .body("role", equalTo("aluno"));
        }

        @Test
        @DisplayName("CT-USR-008: código de cadastro ausente retorna 403")
        void CT_USR_008_RegAlunError_ShouldReturn403() {
            String request = """
                    {
                        "name": "Testes de API",
                        "email": "api.testes-008@teste.com",
                        "password": "senhaForte123",
                        "role": "ALUNO"
                    }
                    """;

//            //Sem code
            given()
                    .contentType(ContentType.JSON)
                    .body(request)
            .when()
                    .post(BASE_PATH + "/regAlun")
            .then()
                    .statusCode(403);

        }

        @Test
        @DisplayName("CT-USR-009: Deve rejeitar a requisição que não possua code válido e retornar 403")
        void CT_USR_009_RegAlunError_ShouldReturn403() {
            String request = """
                    {
                        "name": "Testes de API",
                        "email": "api.testes-009@teste.com",
                        "password": "senhaForte123",
                        "role": "ALUNO"
                    }
                    """;

            //Code vazio
            String codeBlank = "";
            given()
                    .contentType(ContentType.JSON)
                    .body(request)
            .when()
                    .post(BASE_PATH + "/regAlun?code={codeBlank}", codeBlank)
            .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());

        }

        @Test
        @DisplayName("CT-USR-010: Deve rejeitar a requisição que não possua code válido e retornar 400")
        void CT_USR_010_RegAlunError_ShouldReturn400() {
            String request = """
                    {
                        "name": "Testes de API",
                        "email": "api.testes-010@teste.com",
                        "password": "senhaForte123",
                        "role": "ALUNO"
                    }
                    """;

            String codeInvalid = String.format("%06d", new SecureRandom().nextInt(1_000_000));
            given()
                    .contentType(ContentType.JSON)
                    .body(request)
            .when()
                    .post(BASE_PATH + "/regAlun?code={codeInvalid}", codeInvalid)
            .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("CT-USR-011: Deve rejeitar a requisição que não possua code válido e retornar 400")
        void CT_USR_011_RegAlunError_ShouldReturn400() {
            String request = """
                    {
                        "name": "Testes de API",
                        "email": "api.testes-011@teste.com",
                        "password": "senhaForte123",
                        "role": "ALUNO"
                    }
                    """;

            VerificationCode codeExpired = testDataSupport.prepararCodigoRegistroExpirado("api.testes-011@teste.com", "A011AA");
            codeExpired.setExpiresAt(LocalDateTime.now());

            given()
                    .contentType(ContentType.JSON)
                    .body(request)
            .when()
                    .post(BASE_PATH + "/regAlun?code={codeExpired}", codeExpired.getCode())
            .then()
                    .statusCode(400);
        }
    }

    @Nested
    class login {

        @Test
        @DisplayName("CT-USR-012")
        void CT_USR_012_LoginSuccessfully_shouldReturn200() {
            testDataSupport.prepararUsuario("aluno-012@test.com");

            String request = """
                    {
                        "email": "aluno-012@test.com",
                        "password": "password"
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .body(request)
            .when()
                    .post(BASE_PATH + "/login")
            .then()
                    .statusCode(HttpStatus.OK.value());
        }

        @Test
        @DisplayName("CT-USR-013: login com informação ausente")
        void CT_USR_013_LoginError_shouldReturn400() {
            testDataSupport.prepararUsuario("aluno-013@test.com");

            String request = """
                    {
                        "email": "aluno-013@error.com",
                        "password": ""
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .body(request)
            .when()
                    .post(BASE_PATH + "/login")
            .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @DisplayName("CT-USR-014: login com email em formato inválido")
        void CT_USR_014_LoginError_shouldReturn400() {
            testDataSupport.prepararUsuario("aluno-014@test.com");

            String request = """
                    {
                        "email": "aluno",
                        "password": "passwordWrong"
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .body(request)
            .when()
                    .post(BASE_PATH + "/login")
            .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @DisplayName("CT-USR-015: login com email inexistente")
        void CT_USR_015_LoginError_shouldReturn400() {
            testDataSupport.prepararUsuario("aluno-015@test.com");

            String request = """
                    {
                        "email": "aluno-015@notsaved.com",
                        "password": "passwordWrong"
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .body(request)
            .when()
                    .post(BASE_PATH + "/login")
            .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }

        @Test
        @DisplayName("CT-USR-016: login com senha errada")
        void CT_USR_016_LoginError_shouldReturn403() {
            testDataSupport.prepararUsuario("aluno-016@test.com");

            String request = """
                    {
                        "email": "aluno-016@test.com",
                        "password": "passwordWrong"
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .body(request)
            .when()
                    .post(BASE_PATH + "/login")
            .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }
    }

    @Nested
    class findUser {

        @Test
        @DisplayName("CT-USR-017: findUser executado com sucesso")
        void CT_USR_017_findUserSuccessfully_shouldReturnUser() {
            testDataSupport.prepararUsuario("aluno-017@test.com");
            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer "
                            + ApiTestSupport.obterToken("aluno-017@test.com", "password"))
            .when()
                    .get(BASE_PATH + "/find")
            .then()
                    .statusCode(200)
                    .body("id", notNullValue())
                    .body("name", equalTo("Aluno de Teste"))
                    .body("email", equalTo("aluno-017@test.com"))
                    .body("role", equalTo("aluno"));
        }

        @Test
        @DisplayName("CT-USR-018: findUser sem bearer token (sem login)")
        void CT_USR_018_findUserError_shouldReturn403() {
            given()
                    .contentType(ContentType.JSON)
            .when()
                    .get(BASE_PATH + "/find")
            .then()
                    .statusCode(403);
        }

        @Test
        @DisplayName("CT-USR-019: findUser com bearer token inválido")
        void CT_USR_019_findUserError_shouldReturn403() {
            testDataSupport.prepararUsuario("aluno-019@test.com");
            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + ApiTestSupport.obterToken("aluno-019@test.com", "password") + "a")
            .when()
                    .get(BASE_PATH + "/find")
            .then()
                    .statusCode(403);
        }

        @Test
        @DisplayName("CT-USR-020: findUser com bearer token de usuário excluído")
        void CT_USR_020_findUserError_shouldReturn403() {
            User aluno = testDataSupport.prepararUsuario("aluno-020@test.com");
            String token = ApiTestSupport.obterToken("aluno-020@test.com", "password");
            userRepository.delete(aluno);

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + token)
            .when()
                    .get(BASE_PATH + "/find")
            .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }
    }

    @Nested
    class findAll {

        @Test
        @DisplayName("CT-USR-021: find all Users")
        void CT_USR_021_findAllSuccessfully_shouldListAllUserAndReturn200() {
            testDataSupport.prepararUsuario("aluno-021@test.com");

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + obterTokenAdmin())
            .when()
                    .get(BASE_PATH + "/allProfessors?page=&size=&name={name}", "aluno")
            .then()
                    .statusCode(200)
                    .body("content", notNullValue())
                    .body("content", everyItem(hasKey("id")))
                    .body("content", everyItem(hasKey("name")))
                    .body("content", everyItem(hasKey("email")))
                    .body("content", everyItem(hasKey("role")))
                    .body("totalElements", greaterThanOrEqualTo(0))
                    .body("totalPages", greaterThanOrEqualTo(0))
                    .body("size", equalTo(12))
                    .body("number", equalTo(0));
        }

        @Test
        @DisplayName("CT-USR-022: find all Users with role Professor")
        void CT_USR_022_findAllSuccessfully_shouldListProfessorsUserAndReturn200() {
            testDataSupport.prepararUsuario("aluno-022@test.com");

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + obterTokenAdmin())
            .when()
                    .get(BASE_PATH + "/allProfessors?page=&size=&name=")
            .then()
                    .statusCode(200)
                    .body("content", notNullValue())
                    .body("content", everyItem(hasKey("id")))
                    .body("content", everyItem(hasKey("name")))
                    .body("content", everyItem(hasKey("email")))
                    .body("content", everyItem(hasKey("role")))
                    .body("totalElements", greaterThanOrEqualTo(0))
                    .body("totalPages", greaterThanOrEqualTo(0))
                    .body("size", equalTo(12))
                    .body("number", equalTo(0));
        }

        @Test
        @DisplayName("CT-USR-023: find all Users without bearer token")
        void CT_USR_023_findAllSuccessfully_shouldListProfessorsUserAndReturn200() {
            testDataSupport.prepararUsuario("aluno-023@test.com");

            given()
                    .contentType(ContentType.JSON)
            .when()
                    .get(BASE_PATH + "/allProfessors?page=&size=&name=")
            .then()
                    .statusCode(403);
        }
    }

    @Nested
    class deleteUser {

        @Test
        @DisplayName("CT-USR-024: delete user successfully")
        void CT_USR_024_deleteUserSuccessfully_shouldReturn204() {
            User user = testDataSupport.prepararUsuario("aluno-024@test.com");

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + ApiTestSupport.obterToken("aluno-024@test.com", "password"))
            .when()
                    .delete(BASE_PATH + "/{id}", user.getId())
            .then()
                    .statusCode(204);
        }

        @Test
        @DisplayName("CT-USR-025: deletando user já deletado")
        void CT_USR_025_deleteUserError_shouldReturn403() {
            User user = testDataSupport.prepararUsuario("aluno-025@test.com");
            String token = ApiTestSupport.obterToken("aluno-025@test.com", "password");
            userRepository.delete(user);

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + token)
            .when()
                    .delete(BASE_PATH + "/{id}", user.getId())
            .then()
                    .statusCode(403);
        }

        //olhar esse ainda
        @Test
        @DisplayName("CT-USR-026: deletando user já deletado")
        void CT_USR_026_deleteUserError_shouldReturn403() {
            User user = testDataSupport.prepararUsuario("aluno-026@test.com");
            String token = ApiTestSupport.obterToken("aluno-026@test.com", "password");
            userRepository.delete(user);

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + token)
            .when()
                    .delete(BASE_PATH + "/{id}", user.getId())
            .then()
                    .statusCode(403);
        }

        @Test
        @DisplayName("CT-USR-027: deletando user com id inválido")
        void CT_USR_027_deleteUserSuccessfully_shouldReturn404() {
            User user = testDataSupport.prepararUsuario("aluno-027@test.com");

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + ApiTestSupport.obterToken("aluno-027@test.com", "password"))
            .when()
                    .delete(BASE_PATH + "/{id}", 999999L)
            .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @DisplayName("CT-USR-028: deletando user com token inválido")
        void CT_USR_028_deleteUserSuccessfully_shouldReturn403() {
            User user = testDataSupport.prepararUsuario("aluno-028@test.com");

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + ApiTestSupport.obterToken("aluno-028@test.com", "password")+"a")
            .when()
                    .delete(BASE_PATH + "/{id}", 999999L)
            .then()
                    .statusCode(403);
        }
    }

    @Nested
    class updateUser {

        @Test
        @DisplayName("CT-USR-029: atualizar user e retornar 200")
        void CT_USR_029_updateUserSuccessfully_shouldUpdateUserAndReturn200() {
            User aluno = testDataSupport.prepararUsuario("aluno-029@test.com");

            String update = """
                    {
                        "name": "Testes de Aluno",
                        "email": "aluno.testes-029@test.com",
                        "password": "senhaForte12345",
                        "role": "ALUNO"
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + ApiTestSupport.obterToken("aluno-029@test.com", "password"))
                    .body(update)
            .when()
                    .patch(BASE_PATH + "/{id}", aluno.getId())
            .then()
                    .statusCode(200)
                    .body("id", notNullValue())
                    .body("name", equalTo("Testes de Aluno"))
                    .body("email", equalTo("aluno.testes-029@test.com"))
                    .body("role", equalTo("aluno"));
        }

        @Test
        @DisplayName("CT-USR-030: usuário admin atualiza outro usuário e retornar 200")
        void CT_USR_030_updateUserSuccessfully_shouldUpdateUserExceptPasswordAndReturn200() {
            User aluno = testDataSupport.prepararUsuario("aluno-030@test.com");

            String update = """
                    {
                        "name": "Testes de Aluno",
                        "email": "aluno.testes-030@test.com",
                        "role": "PROFESSOR"
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + obterTokenAdmin())
                    .body(update)
            .when()
                    .patch(BASE_PATH + "/{id}", aluno.getId())
            .then()
                    .statusCode(200)
                    .body("id", notNullValue())
                    .body("name", equalTo("Testes de Aluno"))
                    .body("email", equalTo("aluno.testes-030@test.com"))
                    .body("role", equalTo("professor"));
        }

        @Test
        @DisplayName("CT-USR-031: atualiza user com informação inválida e retornar 400")
        void CT_USR_031_updateUserError_shouldNotUpdateUserAndReturn400() {
            testDataSupport.prepararUsuario("aluno-031@test.com");
            String update = """
                    {
                        "name": "Testes de Aluno com título invalido com 30+ caracteres",
                        "email": "aluno.testes-031@test.com",
                        "password": "senhaForte12345",
                        "role": "PROFESSOR"
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + ApiTestSupport.obterToken("aluno-031@test.com", "password"))
                    .body(update)
            .when()
                    .patch(BASE_PATH + "/{id}", 999999L)
            .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @DisplayName("CT-USR-032: atualiza user usando email já registrado e retornar 404")
        void CT_USR_032_updateUserError_shouldNotUpdateUserAndReturn404() {
            testDataSupport.prepararUsuario("aluno-032@test.com");
            String update = """
                    {
                        "name": "Testes de Aluno",
                        "email": "admin@test.com",
                        "password": "senhaForte12345",
                        "role": "PROFESSOR"
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + ApiTestSupport.obterToken("aluno-032@test.com", "password"))
                    .body(update)
            .when()
                    .patch(BASE_PATH + "/{id}", 999999L)
            .then()
                    .statusCode(404);
        }

        @Test
        @DisplayName("CT-USR-033: atualiza user inexistente e retornar 404")
        void CT_USR_033_updateUserError_shouldNotUpdateUserAndReturn404() {
            testDataSupport.prepararUsuario("aluno-033@test.com");
            String update = """
                    {
                        "name": "Testes de Aluno",
                        "email": "aluno.testes-033@test.com",
                        "password": "senhaForte12345",
                        "role": "PROFESSOR"
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + ApiTestSupport.obterToken("aluno-033@test.com", "password"))
                    .body(update)
            .when()
                    .patch(BASE_PATH + "/{id}", 999999L)
            .then()
                    .statusCode(404);
        }

        @Test
        @DisplayName("CT-USR-034: usuário comum atualiza outro usuário e retorna 403")
        void CT_USR_034_updateUserError_shouldNotUpdateUserAndReturn404() {
            testDataSupport.prepararUsuario("aluno-034@test.com");
            User aluno2 = testDataSupport.prepararUsuario("aluno2-034@test.com");
            String update = """
                    {
                        "name": "Testes de Aluno",
                        "email": "aluno.testes-034@test.com",
                        "password": "senhaForte12345",
                        "role": "PROFESSOR"
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + ApiTestSupport.obterToken("aluno-034@test.com", "password"))
                    .body(update)
            .when()
                    .patch(BASE_PATH + "/{id}", aluno2.getId())
            .then()
                    .statusCode(403);
        }

        @Test
        @DisplayName("CT-USR-035: usuário comum atualiza role para ADMIN e retorna 403")
        void CT_USR_035_updateUserError_shouldNotUpdateUserAndReturn200() {
            User aluno = testDataSupport.prepararUsuario("aluno-035@test.com");
            String update = """
                    {
                        "name": "Testes de Aluno",
                        "email": "aluno.testes-035@test.com",
                        "password": "senhaForte12345",
                        "role": "ADMIN"
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + ApiTestSupport.obterToken("aluno-035@test.com", "password"))
                    .body(update)
            .when()
                    .patch(BASE_PATH + "/{id}", aluno.getId())
            .then()
                    .statusCode(200)
                    .body("role", equalTo("aluno"));
        }
    }

    @Nested
    class verifyAdmin {

        @Test
        @DisplayName("CT-USR-036: admin consulta o próprio cadastro")
        void CT_USR_036_verifyAdminSuccessfully_shouldReturn200AndTrue() {
            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + obterTokenAdmin())
                    .when()
                    .get(BASE_PATH + "/admin/{id}", userRepository.findUserByEmail("admin@test.com").get().getId())
                    .then()
                    .statusCode(200)
                    .body("isAdmin", equalTo(true));
        }

        @Test
        @DisplayName("CT-USR-037: usuário consulta o próprio cadastro")
        void CT_USR_037_verifyAdminSuccessfully_shouldReturn200AndFalse() {
            User user = testDataSupport.prepararUsuario("aluno-037@test.com");

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + ApiTestSupport.obterToken("aluno-037@test.com", "password"))
            .when()
                    .get(BASE_PATH + "/admin/{id}", user.getId())
            .then()
                    .statusCode(200)
                    .body("isAdmin", equalTo(false));
        }

        @Test
        @DisplayName("CT-USR-038: usuário comum consulta outro cadastro")
        void CT_USR_038_verifyAdminError_shouldReturn403() {
            testDataSupport.prepararUsuario("aluno-038@test.com");
            User user2 = testDataSupport.prepararUsuario("aluno2-038@test.com");

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + ApiTestSupport.obterToken("aluno-038@test.com", "password"))
            .when()
                    .get(BASE_PATH + "/admin/{id}", user2.getId())
            .then()
                    .statusCode(403);
        }

        @Test
        @DisplayName("CT-USR-039: usuário comum consulta usuário inválido")
        void CT_USR_039_verifyAdminError_shouldReturn403() {
            testDataSupport.prepararUsuario("aluno-039@test.com");

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + ApiTestSupport.obterToken("aluno-039@test.com", "password"))
            .when()
                    .get(BASE_PATH + "/admin/{id}", 999999L)
            .then()
                    .statusCode(404);
        }

        @Test
        @DisplayName("CT-USR-040: admin sem header consulta o próprio cadastro")
        void CT_USR_040_verifyAdminError_shouldReturn403() {
            given()
                    .contentType(ContentType.JSON)
            .when()
                    .get(BASE_PATH + "/admin/{id}", userRepository.findUserByEmail("admin@test.com").get().getId())
            .then()
                    .statusCode(403);
        }
    }
}
