# Casos de Teste

## Informações

| Campo          | Informação                              |
|----------------|-----------------------------------------|
| Projeto        | My Trace Table Manager                  |
| Funcionalidade | API                                     |
| Módulo         | User, Verification, Theme e Trace Table |
| Tipo de teste  | API                                     |
| Ferramenta     | REST Assured                            |
| Responsável    | Victor Kawê Santos Silva                |
| Data           | 22/09/26                                |
---

# UserController

## CT-USR-001 — Registrar usuário com dados válidos

**Objetivo:** Validar o cadastro de usuário utilizando um administrador.

**Pré-condições:**
- Possuir token JWT com autoridade `ADMIN`.
- Utilizar dados válidos para `name`, `email`, `password` e `role`.

**Endpoint:** `POST /v1/user/register`

**Passos:**
1. Autenticar como administrador.
2. Enviar o payload para `POST /v1/user/register`.

**Resultado esperado:** Retornar `201`, com `id` preenchido, `name` igual a `Testes de API`, `email` igual a `api.testes@teste.com` e `role` igual a `admin`.

**Resultado obtido:** [Preencher após execução]

**Status:** `NÃO EXECUTADO`

---

## CT-USR-002 — Registrar usuário com campo obrigatório ausente, nulo ou vazio

**Objetivo:** Validar a rejeição de dados obrigatórios inválidos no cadastro.

**Pré-condições:**
- Possuir token JWT `ADMIN`.

**Endpoint:** `POST /v1/user/register`

**Resultado esperado:** Cada requisição deve retornar `400`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-USR-003 — Registrar usuário com email, nome, role ou senha inválido ou acima do limite

**Objetivo:** Validar a rejeição de email, senha, nome e role fora das regras aceitas.

**Pré-condições:**
- Possuir token JWT `ADMIN`.

**Resultado esperado:** Cada requisição deve retornar `400`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-USR-004 — Registrar usuário com email já existente

**Objetivo:** Validar o tratamento de email duplicado.

**Pré-condições:**
- Possuir usuário previamente cadastrado com o email utilizado.
- Possuir o usuário `admin@test.com` previamente cadastrado.
- Possuir token válido de aluno, conforme o teste implementado.

**Resultado esperado:** Retornar `400` com a mensagem `Tente se registrar com outro email`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-USR-005 — Registrar usuário sem token

**Objetivo:** Validar a proteção do endpoint de registro administrativo.

**Endpoint:** `POST /v1/user/register`

**Resultado esperado:** Retornar `403` (é o status efetivamente verificado pelo teste, apesar do texto do `@DisplayName`).

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-USR-006 — Registrar usuário com usuário autenticado sem ADMIN

**Objetivo:** Validar a autorização baseada na autoridade `ADMIN`.

**Pré-condições:**
- Possuir token válido de usuário sem autoridade `ADMIN`.

**Resultado esperado:** Retornar `403`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

# 2. Registrar aluno

## CT-USR-007 — Registrar aluno com dados e código válidos

**Objetivo:** Validar o cadastro de aluno utilizando código válido.

**Endpoint:** `POST /v1/user/regAlun?code={code}`

**Resultado esperado:** Retornar `201`, com `id` preenchido, os dados enviados e `role` igual a `aluno`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-USR-008 — Registrar aluno com código ausente, vazio, inválido ou expirado

**Objetivo:** Validar as diferentes condições inválidas do código de cadastro.

**Resultado esperado:** Retornar `403`, conforme o comportamento atual do sistema.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-USR-009 — Registrar aluno com código vazio

**Objetivo:** Validar a rejeição de código de cadastro vazio.

**Endpoint:** `POST /v1/user/regAlun?code=`

**Resultado esperado:** Retornar `403`, conforme o comportamento atual do sistema.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-USR-010 — Registrar aluno com código inválido

**Objetivo:** Validar a rejeição de código de cadastro aleatório ou inexistente.

**Endpoint:** `POST /v1/user/regAlun?code={codeInvalid}`

**Resultado esperado:** Retornar `400`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-USR-011 — Registrar aluno com código expirado

**Objetivo:** Validar a rejeição de código de cadastro expirado.

**Endpoint:** `POST /v1/user/regAlun?code={codeExpired}`

**Resultado esperado:** Retornar `400`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

# 3. Login

## CT-USR-012 — Login com credenciais válidas

**Objetivo:** Validar autenticação com email e senha válidos.

**Endpoint:** `POST /v1/user/login`

**Resultado esperado:** Retornar `200`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-USR-013 — Login com email ou senha ausentes, nulos ou vazios

**Objetivo:** Validar os campos obrigatórios do login.

**Resultado esperado:** Retornar `400`, conforme o comportamento atual do sistema.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-USR-014 — Login com email ou senha em formato/tamanho inválido

**Objetivo:** Validar as restrições do payload.

**Resultado esperado:** Retornar `400`, conforme o comportamento atual do sistema.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-USR-015 — Login com email inexistente

**Objetivo:** Validar o tratamento de usuário não cadastrado.

**Resultado esperado:** Retornar `403`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-USR-016 — Login com senha incorreta

**Objetivo:** Validar o tratamento de credencial incorreta.

**Resultado esperado:** Retornar `403`, conforme o comportamento atual do sistema.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

# 4. Buscar usuário autenticado

## CT-USR-017 — Buscar usuário com token válido

**Objetivo:** Consultar o usuário associado ao JWT.

**Endpoint:** `GET /v1/user/find`

**Resultado esperado:** Retornar `200`, com o aluno preparado no teste: `Aluno de Teste`, `aluno@test.com` e role `aluno`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-USR-018 — Buscar usuário sem Authorization

**Objetivo:** Validar a proteção do endpoint.

**Resultado esperado:** Retornar `403`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-USR-019 — Buscar usuário com token inválido ou expirado

**Objetivo:** Validar rejeição de JWT inválido/expirado.

**Resultado esperado:** Retornar `403`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-USR-020 — Buscar usuário com token associado a usuário removido

**Objetivo:** Validar a consistência entre JWT e existência do usuário.

**Resultado esperado:** Rejeitar a consulta de usuário inexistente.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

# 5. Listar professores

## CT-USR-021 — Listar usuários com filtro de nome e paginação padrão

**Objetivo:** Validar a listagem de usuários com filtro de nome, paginação padrão e autenticação de administrador.

**Endpoint:** `GET /v1/user/allProfessors?page=&size=&name=aluno`

**Resultado esperado:** Retornar `200`, com `content` não nulo; cada item de `content` contendo `id`, `name`, `email` e `role`; `totalElements` e `totalPages` maiores ou iguais a zero; `size` igual a `12`; e `number` igual a `0`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-USR-022 — Listar professores com paginação padrão

**Objetivo:** Validar a listagem de professores com os parâmetros de paginação vazios e autenticação de administrador.

**Endpoint:** `GET /v1/user/allProfessors?page=&size=&name=`

**Resultado esperado:** Retornar `200`, com `content` não nulo; cada item de `content` contendo `id`, `name`, `email` e `role`; `totalElements` e `totalPages` maiores ou iguais a zero; `size` igual a `12`; e `number` igual a `0`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-USR-023 — Listar professores sem token

**Objetivo:** Validar autenticação do endpoint.

**Resultado esperado:** Retornar `403`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

# 6. Remover usuário

## CT-USR-024 — Usuário remover o próprio cadastro

**Objetivo:** Validar exclusão autorizada.

**Endpoint:** `DELETE /v1/user/{id}`

**Resultado esperado:** Retornar `204`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-USR-025 — Remover usuário já removido

**Objetivo:** Validar tratamento de recurso inexistente.

**Resultado esperado:** Retornar `403`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-USR-026 — Remover novamente usuário já removido

**Objetivo:** Validar restrição de autorização.

**Resultado esperado:** Retornar `403`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-USR-027 — Remover usuário com ID inválido

**Objetivo:** Validar o parâmetro de identificação.

**Resultado esperado:** Retornar `403`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-USR-028 — Remover usuário com token inválido

**Objetivo:** Validar autenticação.

**Resultado esperado:** Retornar `403`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

# 7. Atualizar usuário

## CT-USR-029 — Atualizar o próprio usuário com dados válidos

**Objetivo:** Validar atualização de dados do usuário.

**Endpoint:** `PATCH /v1/user/{id}`

**Resultado esperado:** Retornar `200`, com nome `Testes de Aluno`, email `aluno.testes@test.com` e role `professor`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-USR-030 — ADMIN atualizar outro usuário sem informar senha

**Objetivo:** Validar preservação da senha atual.

**Resultado esperado:** Retornar `200`, atualizar nome/email/role e manter a senha atual.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-USR-031 — Atualizar usuário com ID inexistente e payload inválido

**Objetivo:** Validar o comportamento da atualização quando o ID informado não existe e o payload contém nome fora do limite.

**Resultado esperado:** Retornar `400`, pois a validação do payload ocorre antes da busca do usuário.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-USR-032 — Atualizar usuário com ID inexistente e email já utilizado

**Objetivo:** Validar que o ID inexistente é identificado antes da análise de unicidade do email.

**Resultado esperado:** Retornar `404`, pois o usuário indicado pelo ID não existe.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-USR-033 — Atualizar usuário inexistente

**Objetivo:** Validar tratamento de recurso inexistente.

**Resultado esperado:** Retornar `404`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-USR-034 — Usuário comum atualizar outro usuário

**Objetivo:** Validar autorização por usuário.

**Resultado esperado:** Retornar `403`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-USR-035 — Usuário comum alterar o próprio role para ADMIN

**Objetivo:** Verificar a regra de segurança identificada no código atual.

**Resultado esperado:** Retornar `403`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

# 8. Verificar administrador

## CT-USR-036 — ADMIN consultar o próprio cadastro

**Objetivo:** Validar consulta de autoridade administrativa.

**Resultado esperado:** Retornar `200` com `isAdmin` igual a `true`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-USR-037 — Usuário comum consultar o próprio cadastro

**Objetivo:** Validar resposta para usuário que não é administrador.

**Resultado esperado:** Retornar `200` com `false`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-USR-038 — Usuário comum consultar outro cadastro

**Objetivo:** Validar autorização da consulta.

**Resultado esperado:** Retornar `403`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-USR-039 — Consultar usuário com ID inválido

**Objetivo:** Validar tratamento de identificador inválido/inexistente.

**Resultado esperado:** Retornar `404`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-USR-040 — Consultar administrador sem token

**Objetivo:** Validar autenticação.

**Resultado esperado:** Retornar `403`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

# VerificationController

## CT-VER-001 — Enviar código de cadastro para email novo

**Objetivo:** Validar o envio de código para email não cadastrado.

**Endpoint:** `POST /v1/verification/registration/send`

**Resultado esperado:** Retornar `200`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-VER-002 — Enviar código de cadastro para email já cadastrado

**Objetivo:** Validar a rejeição de email já cadastrado.

**Endpoint:** `POST /v1/verification/registration/send`

**Resultado esperado:** Retornar `400`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-VER-003 — Enviar código de cadastro com email inválido

**Objetivo:** Validar a rejeição de email vazio.

**Endpoint:** `POST /v1/verification/registration/send`

**Resultado esperado:** Retornar `400`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-VER-004 — Enviar código de recuperação para email existente

**Objetivo:** Validar o envio de código de recuperação e o disparo do email.

**Endpoint:** `POST /v1/verification/password-reset/send`

**Resultado esperado:** Retornar `200` e enviar uma mensagem pelo `JavaMailSender`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-VER-005 — Enviar recuperação para email inexistente

**Objetivo:** Validar o tratamento de email não cadastrado.

**Endpoint:** `POST /v1/verification/password-reset/send`

**Resultado esperado:** Retornar `404`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-VER-006 — Enviar recuperação com email vazio

**Objetivo:** Validar o campo de email no envio de recuperação.

**Endpoint:** `POST /v1/verification/password-reset/send`

**Resultado esperado:** Retornar `400`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-VER-007 — Confirmar recuperação com dados válidos

**Objetivo:** Validar a redefinição de senha com código válido.

**Endpoint:** `POST /v1/verification/password-reset/confirm`

**Resultado esperado:** Retornar `200`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-VER-008 — Confirmar recuperação com email vazio

**Objetivo:** Validar a rejeição de email vazio na confirmação.

**Resultado esperado:** Retornar `400`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-VER-009 — Confirmar recuperação com código vazio

**Objetivo:** Validar a rejeição de código vazio.

**Resultado esperado:** Retornar `400`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-VER-010 — Confirmar recuperação com código expirado

**Objetivo:** Validar a rejeição de código expirado.

**Resultado esperado:** Retornar `400`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-VER-011 — Confirmar recuperação com código utilizado

**Objetivo:** Validar a rejeição de código já utilizado.

**Resultado esperado:** Retornar `400`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-VER-012 — Confirmar recuperação com nova senha inválida

**Objetivo:** Validar a rejeição de nova senha fora das regras.

**Resultado esperado:** Retornar `400`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-VER-013 — Confirmar recuperação com código de outro usuário

**Objetivo:** Validar a consistência entre o código e o email informado.

**Resultado esperado:** Retornar `400`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

# ThemeController

## CT-THM-001 — Criar tema para usuário existente

**Objetivo:** Validar criação de tema com dados válidos.

**Endpoint:** `POST /v1/theme/{userId}`

**Resultado esperado:** Retornar `201`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-THM-002 — Criar tema para usuário inexistente ou com ID inválido

**Objetivo:** Validar o identificador do usuário.

**Resultado esperado:** Retornar `403`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-THM-003 — Criar tema com nome ausente, vazio ou fora dos limites

**Objetivo:** Validar o campo `name`.

**Resultado esperado:** Retornar `400`, conforme o comportamento atual do sistema.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-THM-004 — Usuário autenticado criar tema para outro usuário

**Objetivo:** Verificar a autorização baseada no proprietário.

**Resultado esperado:** Retornar `403`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-THM-005 — Listar temas existentes

**Objetivo:** Validar listagem pública de temas.

**Endpoint:** `GET /v1/theme`

**Resultado esperado:** Retornar `200`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-THM-006 — Listar temas sem autenticação

**Objetivo:** Validar a autenticação da listagem.

**Resultado esperado:** Retornar `403`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-THM-007 — Buscar tema existente por ID

**Objetivo:** Validar consulta de tema.

**Endpoint:** `GET /v1/theme/{themeId}`

**Resultado esperado:** Retornar `200`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-THM-008 — Buscar tema inexistente ou com ID inválido

**Objetivo:** Validar o identificador informado.

**Resultado esperado:** Retornar `404`, conforme o comportamento atual do sistema.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-THM-009 — Buscar tema por ID sem autenticação

**Objetivo:** Validar a autenticação da busca por ID.

**Resultado esperado:** Retornar `403`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-THM-010 — Buscar tema por nome

**Objetivo:** Validar busca por nome, inclusive diferenças de maiúsculas e minúsculas.

**Endpoint:** `GET /v1/theme/name/{themeName}`

**Resultado esperado:** Retornar `200` para tema existente e validar comportamento case-insensitive.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-THM-011 — Buscar tema inexistente por nome

**Objetivo:** Validar ausência de correspondência.

**Resultado esperado:** Retornar `404`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-THM-012 — Buscar tema por nome sem autenticação

**Objetivo:** Validar a busca por nome sem token.

**Resultado esperado:** Retornar `404`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-THM-013 — Buscar temas de usuário existente

**Objetivo:** Validar consulta pública dos temas de um usuário.

**Endpoint:** `GET /v1/theme/user/{userId}`

**Resultado esperado:** Retornar `200` com os temas do usuário.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-THM-014 — Buscar temas de usuário sem temas

**Objetivo:** Validar lista vazia.

**Resultado esperado:** Retornar `200` com lista vazia.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-THM-015 — Buscar temas de usuário inexistente ou com ID inválido

**Objetivo:** Validar o usuário informado.

**Resultado esperado:** Retornar `404`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-THM-016 — Buscar temas de usuário sem autenticação

**Objetivo:** Validar a busca por usuário sem token.

**Resultado esperado:** Retornar `404`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-THM-017 — Buscar temas de trace table existente

**Objetivo:** Validar temas associados a uma trace table.

**Endpoint:** `GET /v1/theme/trace/{traceId}?page=&size=&sort=`

**Resultado esperado:** Retornar `200` com os temas associados.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-THM-018 — Buscar temas de trace table inexistente ou sem temas

**Objetivo:** Validar lista vazia quando não há correspondências.

**Resultado esperado:** Retornar `200` com `content` vazio.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-THM-019 — Buscar temas de trace table sem autenticação

**Objetivo:** Validar a autenticação da consulta por trace table.

**Resultado esperado:** Retornar `403`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-THM-020 — Criador remover o próprio tema

**Objetivo:** Validar remoção autorizada.

**Endpoint:** `DELETE /v1/theme/{themeId}/{userId}`

**Resultado esperado:** Retornar `204`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-THM-021 — Remover tema com ID inexistente

**Objetivo:** Validar os identificadores da remoção.

**Resultado esperado:** Retornar `404`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-THM-022 — Remover tema com usuário inexistente

**Objetivo:** Validar o usuário informado na remoção.

**Resultado esperado:** Retornar `404`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-THM-023 — Usuário comum remover tema de outro criador

**Objetivo:** Validar a autorização do usuário comum.

**Resultado esperado:** Retornar `403`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-THM-024 — Remover tema sem token

**Objetivo:** Verificar possibilidade de bypass da autorização pelo `userId` do path.

**Resultado esperado:** Retornar `403`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-THM-025 — Outro usuário remover tema de professor

**Objetivo:** Validar a autorização de outro usuário sobre o tema.

**Resultado esperado:** Retornar `403`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-THM-026 — Remover tema associado a trace table

**Objetivo:** Validar a remoção de tema associado a trace table.

**Endpoint:** `PUT /v1/theme/{themeId}/{userId}`

**Resultado esperado:** Retornar `403`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-THM-027 — Atualizar tema com nome válido

**Objetivo:** Validar atualização autorizada com nome válido.

**Resultado esperado:** Retornar `200`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-THM-028 — Atualizar tema com nome vazio

**Objetivo:** Validar nome vazio na atualização.

**Resultado esperado:** Retornar `400`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-THM-029 — Atualizar tema inexistente

**Objetivo:** Validar atualização de tema inexistente.

**Resultado esperado:** Retornar `400`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-THM-030 — Usuário comum atualizar tema de outro criador

**Objetivo:** Validar a autorização de usuário comum.

**Resultado esperado:** Retornar `400`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-THM-031 — Atualizar tema sem token

**Objetivo:** Validar a atualização sem token.

**Resultado esperado:** Retornar `403`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-THM-032 — Atualizar tema de outro usuário

**Objetivo:** Validar a autorização quando outro usuário tenta atualizar o tema.

**Resultado esperado:** Retornar `403`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

# TraceTableController

## CT-TRT-001 — Criar trace table com JSON, imagem e temas válidos

**Objetivo:** Validar criação completa de trace table.

**Endpoint:** `POST /v1/trace/{userId}`

**Resultado esperado:** Retornar `201`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-002 — Criar trace table com parte obrigatória ausente, JSON malformado ou payload inválido

**Objetivo:** Validar as partes obrigatórias e o conteúdo da requisição multipart.

**Resultado esperado:** Rejeitar a requisição com erro apropriado.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-003 — Criar trace table com usuário inexistente ou ID inválido

**Objetivo:** Validar o `userId`.

**Resultado esperado:** Retornar erro apropriado, normalmente `404` para usuário inexistente.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-004 — Criar trace table com temas inexistentes

**Objetivo:** Validar `themesIds`.

**Resultado esperado:** Rejeitar a operação.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-005 — Criar trace table com imagem vazia, maior que 1 MB ou falha de upload

**Objetivo:** Validar restrições de imagem e tratamento de falha no upload.

**Resultado esperado:** Rejeitar a operação ou retornar erro de upload conforme o comportamento implementado.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-006 — Criar trace table sem token ou com token inválido

**Objetivo:** Validar autenticação.

**Resultado esperado:** Retornar `403`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-007 — Usuário criar trace table usando userId de outra pessoa

**Objetivo:** Verificar autorização pelo usuário autenticado.

**Resultado esperado:** Registrar o comportamento atual, pois o levantamento indica possibilidade de a operação ser permitida.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-008 — Criar trace table com Content-Type diferente de multipart/form-data

**Objetivo:** Validar o formato exigido pelo endpoint.

**Resultado esperado:** Rejeitar a requisição.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-009 — Buscar trace table existente por ID

**Objetivo:** Validar consulta pública de trace table.

**Endpoint:** `GET /v1/trace/{traceId}`

**Resultado esperado:** Retornar `200`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-010 — Buscar trace table inexistente ou com ID inválido

**Objetivo:** Validar identificador da trace table.

**Resultado esperado:** Retornar `404` para recurso inexistente ou erro apropriado para ID inválido.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-011 — Listar trace tables de usuário existente

**Objetivo:** Validar listagem por usuário.

**Endpoint:** `GET /v1/trace/user/{userId}?page=&size=&sort=`

**Resultado esperado:** Retornar `200`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-012 — Listar trace tables de usuário sem resultados

**Objetivo:** Validar página vazia.

**Resultado esperado:** Retornar página vazia.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-013 — Listar trace tables com usuário inexistente ou ID inválido

**Objetivo:** Validar o usuário informado.

**Resultado esperado:** Retornar erro apropriado.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-014 — Listar trace tables de usuário sem autenticação

**Objetivo:** Confirmar que o endpoint é público.

**Resultado esperado:** Executar sem token.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-015 — Buscar trace tables por tema e professor com filtros válidos

**Objetivo:** Validar busca combinando tema e professor.

**Endpoint:** `GET /v1/trace/theme/name/{themeName}?prof=&page=&size=&sort=`

**Resultado esperado:** Retornar `200` com resultados compatíveis.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-016 — Buscar trace tables por tema inexistente

**Objetivo:** Validar tema sem correspondência.

**Resultado esperado:** Retornar resultado vazio ou resposta definida para recurso inexistente, conforme implementação.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-017 — Omitir ou informar incorretamente prof

**Objetivo:** Validar o parâmetro `prof`.

**Resultado esperado:** Retornar erro ou resultado conforme validação implementada.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-018 — Listar trace tables por tema existente

**Objetivo:** Validar consulta por `themeId`.

**Endpoint:** `GET /v1/trace/theme/{themeId}?page=&size=&sort=`

**Resultado esperado:** Retornar `200`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-019 — Tema sem trace tables

**Objetivo:** Validar retorno vazio.

**Resultado esperado:** Retornar página vazia.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-020 — Informar tema inexistente ou ID inválido

**Objetivo:** Validar `themeId`.

**Resultado esperado:** Retornar erro apropriado.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-021 — Criador remover a própria trace table ou ADMIN remover outra

**Objetivo:** Validar remoção autorizada.

**Endpoint:** `DELETE /v1/trace/{traceId}/{userId}`

**Resultado esperado:** Retornar `204`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-022 — Remover trace table com IDs inexistentes/inválidos

**Objetivo:** Validar identificadores usados na exclusão.

**Resultado esperado:** Retornar erro apropriado.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-023 — Usuário comum remover trace table de outro criador

**Objetivo:** Validar autorização.

**Resultado esperado:** Rejeitar a operação.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-024 — Remover trace table sem token ou com token inválido

**Objetivo:** Validar autenticação.

**Resultado esperado:** Retornar `403`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-025 — Usuário não proprietário usar userId do proprietário na remoção

**Objetivo:** Verificar possível bypass de autorização.

**Resultado esperado:** Registrar o comportamento atual identificado no levantamento.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-026 — Atualizar trace table com payload e temas existentes

**Objetivo:** Validar atualização completa.

**Endpoint:** `PUT /v1/trace/{traceId}/{userId}?themesIds=`

**Resultado esperado:** Retornar `200`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-027 — Atualizar trace table informando lista vazia de temas

**Objetivo:** Validar atualização sem temas.

**Resultado esperado:** Retornar `200` ou comportamento definido pela implementação.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-028 — Atualizar trace table com themesIds inexistentes ou omitidos

**Objetivo:** Validar `themesIds`.

**Resultado esperado:** Rejeitar a requisição quando os dados forem inválidos ou obrigatórios.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-029 — Atualizar trace table com body malformado ou campos inválidos

**Objetivo:** Validar o body.

**Resultado esperado:** Retornar `400` quando aplicável.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-030 — Atualizar trace table com trace table, usuário ou IDs inexistentes/inválidos

**Objetivo:** Validar os identificadores.

**Resultado esperado:** Retornar erro apropriado.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-031 — Atualizar trace table sem token ou com token inválido

**Objetivo:** Validar autenticação.

**Resultado esperado:** Retornar `403`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-032 — Usuário comum atualizar trace table de outro criador

**Objetivo:** Validar autorização.

**Resultado esperado:** Rejeitar a operação.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-033 — Usuário não proprietário usar userId do proprietário na atualização

**Objetivo:** Verificar possível bypass de autorização.

**Resultado esperado:** Registrar o comportamento atual identificado.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-034 — Validar resposta com tabela completamente correta

**Objetivo:** Validar resposta correta do aluno.

**Endpoint:** `POST /v1/trace/check/{traceId}`

**Resultado esperado:** Retornar `200`, indicando resposta correta conforme a trace table.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-035 — Enviar valor diferente em célula cujo esperado é #

**Objetivo:** Validar regra de célula ignorada.

**Resultado esperado:** A célula com esperado `#` deve ser ignorada.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-036 — Enviar valor incorreto ou tipo inválido

**Objetivo:** Validar identificação de células incorretas.

**Resultado esperado:** Retornar resposta `200` com os erros correspondentes às células inválidas, conforme estrutura definida.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-037 — Validar representantes de string, int, double, float e boolean

**Objetivo:** Validar comparação dos diferentes tipos de célula.

**Resultado esperado:** Cada tipo deve ser tratado conforme a regra correspondente.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-038 — Validar trace table inexistente ou tipo de célula desconhecido

**Objetivo:** Validar tratamento de dados não suportados.

**Resultado esperado:** Retornar erro apropriado.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-039 — Validar body malformado, nulo ou com dimensões incompatíveis

**Objetivo:** Validar estrutura da matriz recebida.

**Resultado esperado:** Rejeitar a requisição.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-040 — Validar formato da lista de erros

**Objetivo:** Validar que os erros retornados contenham linha, coluna e mensagem.

**Resultado esperado:** Cada erro deve possuir `linha`, `coluna` e `mensagem` conforme estrutura implementada.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-041 — Validar resposta sem autenticação

**Objetivo:** Confirmar que `/check` é público.

**Resultado esperado:** Executar sem token quando o restante dos dados for válido.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-042 — Submeter resposta para trace table existente

**Objetivo:** Validar persistência de resposta de aluno autenticado.

**Endpoint:** `POST /v1/trace/submit/{traceId}`

**Resultado esperado:** Retornar `200`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-043 — Submeter resposta incorreta ou matriz vazia

**Objetivo:** Validar o comportamento do endpoint que atualmente apenas persiste os dados.

**Resultado esperado:** Registrar o comportamento atual, inclusive para resposta incorreta ou matriz vazia.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-044 — Submeter resposta sem token ou com token inválido

**Objetivo:** Validar autenticação.

**Resultado esperado:** Retornar `403`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-045 — Submeter resposta para trace table inexistente

**Objetivo:** Validar existência da trace table.

**Resultado esperado:** Retornar erro apropriado.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-046 — Submeter resposta com body malformado ou estrutura inválida

**Objetivo:** Validar a estrutura da matriz recebida.

**Resultado esperado:** Rejeitar a requisição.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-047 — Verificar associação da resposta ao usuário autenticado

**Objetivo:** Validar consistência entre resposta salva e usuário do JWT.

**Resultado esperado:** A resposta deve ficar associada ao usuário autenticado.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-048 — Consultar todas as respostas do exercício

**Objetivo:** Validar consulta de respostas.

**Endpoint:** `GET /v1/trace/answers/{traceId}?startDate=&endDate=&studentName=`

**Resultado esperado:** Retornar `200` com as respostas do exercício.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-049 — Filtrar respostas por período

**Objetivo:** Validar filtro de datas.

**Resultado esperado:** Retornar apenas as respostas dentro do período informado.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-050 — Filtrar respostas por aluno

**Objetivo:** Validar filtro `studentName`.

**Resultado esperado:** Retornar apenas respostas correspondentes ao aluno informado.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-051 — Filtrar respostas por período e aluno

**Objetivo:** Validar combinação dos filtros.

**Resultado esperado:** Retornar apenas registros que atendam aos dois filtros.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-052 — Consultar exercício sem respostas

**Objetivo:** Validar retorno sem registros.

**Resultado esperado:** Retornar lista vazia.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-053 — Consultar respostas com datas inválidas ou período invertido

**Objetivo:** Validar os filtros de data.

**Resultado esperado:** Rejeitar filtros inválidos.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-054 — Consultar respostas informando somente uma das datas

**Objetivo:** Validar o caso de fronteira dos filtros de período.

**Resultado esperado:** Registrar e validar o comportamento definido pela implementação.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-055 — Consultar respostas de trace table inexistente

**Objetivo:** Validar consistência do recurso consultado.

**Resultado esperado:** Retornar erro apropriado.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-056 — Consultar respostas sem autenticação

**Objetivo:** Confirmar que a consulta é pública.

**Resultado esperado:** Executar sem token.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

## CT-TRT-057 — Consultar métricas sem filtros

**Objetivo:** Validar consulta básica de métricas.

**Endpoint:** `GET /v1/trace/metrics/{traceId}?startDate=&endDate=&studentName=`

**Resultado esperado:** Retornar `200` com `MetricsResponse`.

**Resultado obtido:** [Preencher]

**Status:** `NÃO EXECUTADO`

---

# Resumo dos Casos

| Controller             | Casos documentados | Observação                                                                       |
|------------------------|-------------------:|----------------------------------------------------------------------------------|
| UserController         |                 40 | Casos correspondentes aos testes implementados em `UserApiTests.java`            |
| VerificationController |                 13 | Casos correspondentes aos testes implementados em `VerificationApiTests.java`   |
| ThemeController        |                 32 | Casos correspondentes aos testes implementados em `ThemeApiTests.java`          |
| TraceTableController   |                 57 | Casos correspondentes aos testes implementados em `TraceTableApiTests.java`     |
| **Total documentado**  |            **142** | Casos correspondentes à suíte final                                               |

---

# Premissas gerais dos testes

- Controllers analisados: `UserController`, `VerificationController`, `ThemeController` e `TraceTableController`.
- Total de endpoints analisados: 30.
- Headers JSON: `Content-Type: application/json` e `Accept: application/json`.
- Endpoints protegidos exigem `Authorization: Bearer <JWT>`.
- Erros de validação normalmente retornam `400`.
- Erros de recurso inexistente retornam `404`.
- Falhas de autenticação/autorização normalmente retornam `403`.
- Erros comuns possuem `timestamp`, `code`, `message` e `path`.
- Erros de validação também possuem `fieldMessageList`.
