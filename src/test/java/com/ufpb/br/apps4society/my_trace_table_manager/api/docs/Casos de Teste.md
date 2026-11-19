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

**Endpoint:** `POST /v1/user/register`

**Resultado esperado:** Retornar `201`

---

## CT-USR-002 — Registrar usuário com campo obrigatório ausente, nulo ou vazio

**Objetivo:** Validar a rejeição de dados obrigatórios inválidos no cadastro.

**Endpoint:** `POST /v1/user/register`

**Resultado esperado:** Cada requisição deve retornar `400`.

---

## CT-USR-003 — Registrar usuário com email, nome, role ou senha inválido ou acima do limite

**Objetivo:** Validar a rejeição de email, senha, nome e role fora das regras aceitas.

**Endpoint:** `POST /v1/user/register`

**Resultado esperado:** Cada requisição deve retornar `400`.

---

## CT-USR-004 — Registrar usuário com email já existente

**Objetivo:** Validar o tratamento de email duplicado.

**Endpoint:** `POST /v1/user/register`

**Resultado esperado:** Retornar `400` com a mensagem `Tente se registrar com outro email`.

---

## CT-USR-005 — Registrar usuário sem token

**Objetivo:** Validar a proteção do endpoint de registro administrativo.

**Endpoint:** `POST /v1/user/register`

**Resultado esperado:** Retornar `403`.

---

## CT-USR-006 — Registrar usuário com usuário autenticado sem ADMIN

**Objetivo:** Validar a autorização baseada na autoridade `ADMIN`.

**Endpoint:** `POST /v1/user/register`

**Resultado esperado:** Retornar `403`.

---

# 2. Registrar aluno

## CT-USR-007 — Registrar aluno com dados e código válidos

**Objetivo:** Validar o cadastro de aluno utilizando código válido.

**Endpoint:** `POST /v1/user/regAlun?code={code}`

**Resultado esperado:** Retornar `201`, com `id` preenchido, os dados enviados e `role` igual a `aluno`.

---

## CT-USR-008 — Registrar aluno com código ausente

**Objetivo:** Validar a rejeição do código de cadastro ausente.

**Endpoint:** `POST /v1/user/regAlun`

**Resultado esperado:** Retornar `403`.

---

## CT-USR-009 — Registrar aluno com código vazio

**Objetivo:** Validar a rejeição de código de cadastro vazio.

**Endpoint:** `POST /v1/user/regAlun?code=`

**Resultado esperado:** Retornar `403`

---

## CT-USR-010 — Registrar aluno com código inválido

**Objetivo:** Validar a rejeição de código de cadastro aleatório ou inexistente.

**Endpoint:** `POST /v1/user/regAlun?code={codeInvalid}`

**Resultado esperado:** Retornar `400`.

---

## CT-USR-011 — Registrar aluno com código expirado

**Objetivo:** Validar a rejeição de código de cadastro expirado.

**Endpoint:** `POST /v1/user/regAlun?code={codeExpired}`

**Resultado esperado:** Retornar `400`.

---

# 3. Login

## CT-USR-012 — Login com credenciais válidas

**Objetivo:** Validar autenticação com email e senha válidos.

**Endpoint:** `POST /v1/user/login`

**Resultado esperado:** Retornar `200`.

---

## CT-USR-013 — Login com email ou senha ausentes, nulos ou vazios

**Objetivo:** Validar os campos obrigatórios do login.

**Endpoint:** `POST /v1/user/login`

**Resultado esperado:** Retornar `400`.

---

## CT-USR-014 — Login com email ou senha em formato/tamanho inválido

**Objetivo:** Validar as restrições do payload.

**Endpoint:** `POST /v1/user/login`

**Resultado esperado:** Retornar `400`.

---

## CT-USR-015 — Login com email inexistente

**Objetivo:** Validar o tratamento de usuário não cadastrado.

**Endpoint:** `POST /v1/user/login`

**Resultado esperado:** Retornar `403`.

---

## CT-USR-016 — Login com senha incorreta

**Objetivo:** Validar o tratamento de credencial incorreta.

**Endpoint:** `POST /v1/user/login`

**Resultado esperado:** Retornar `403`.

---

# 4. Buscar usuário autenticado

## CT-USR-017 — Buscar usuário com token válido

**Objetivo:** Consultar o usuário associado ao JWT.

**Endpoint:** `GET /v1/user/find`

**Resultado esperado:** Retornar `200`, com o aluno preparado no teste: `Aluno de Teste`, `aluno-017@test.com` e role `aluno`.

---

## CT-USR-018 — Buscar usuário sem Authorization

**Objetivo:** Validar a proteção do endpoint.

**Endpoint:** `GET /v1/user/find`

**Resultado esperado:** Retornar `403`.

---

## CT-USR-019 — Buscar usuário com token inválido ou expirado

**Objetivo:** Validar rejeição de JWT inválido/expirado.

**Endpoint:** `GET /v1/user/find`

**Resultado esperado:** Retornar `403`.

---

## CT-USR-020 — Buscar usuário com token associado a usuário removido

**Objetivo:** Validar a consistência entre JWT e existência do usuário.

**Endpoint:** `GET /v1/user/find`

**Resultado esperado:** Retornar `403`.

---

# 5. Listar professores

## CT-USR-021 — Listar usuários com filtro de nome e paginação padrão

**Objetivo:** Validar a listagem de usuários com filtro de nome, paginação padrão e autenticação de administrador.

**Endpoint:** `GET /v1/user/allProfessors?page=&size=&name=aluno`

**Resultado esperado:** Retornar `200`

---

## CT-USR-022 — Listar professores com paginação padrão

**Objetivo:** Validar a listagem de professores com os parâmetros de paginação vazios e autenticação de administrador.

**Endpoint:** `GET /v1/user/allProfessors?page=&size=&name=`

**Resultado esperado:** Retornar `200`

---

## CT-USR-023 — Listar professores sem token

**Objetivo:** Validar autenticação do endpoint.

**Endpoint:** `GET /v1/user/allProfessors?page=&size=&name=`

**Resultado esperado:** Retornar `403`.


---

# 6. Remover usuário

## CT-USR-024 — Usuário remover o próprio cadastro

**Objetivo:** Validar exclusão autorizada.

**Endpoint:** `DELETE /v1/user/{id}`

**Resultado esperado:** Retornar `204`.


---

## CT-USR-025 — Remover usuário já removido

**Objetivo:** Validar tratamento de recurso inexistente.

**Endpoint:** `DELETE /v1/user/{id}`

**Resultado esperado:** Retornar `403`.


---

## CT-USR-026 — Remover novamente usuário já removido

**Objetivo:** Validar restrição de autorização.

**Endpoint:** `DELETE /v1/user/{id}`

**Resultado esperado:** Retornar `403`.


---

## CT-USR-027 — Remover usuário com ID inválido

**Objetivo:** Validar o parâmetro de identificação.

**Endpoint:** `DELETE /v1/user/{id}`

**Resultado esperado:** Retornar `404`.


---

## CT-USR-028 — Remover usuário com token inválido

**Objetivo:** Validar autenticação.

**Endpoint:** `DELETE /v1/user/{id}`

**Resultado esperado:** Retornar `403`.


---

# 7. Atualizar usuário

## CT-USR-029 — Atualizar o próprio usuário com dados válidos

**Objetivo:** Validar atualização de dados do usuário.

**Endpoint:** `PATCH /v1/user/{id}`

**Resultado esperado:** Retornar `200`, com nome `Testes de Aluno`, email `aluno.testes-029@test.com` e role `aluno`.


---

## CT-USR-030 — ADMIN atualizar outro usuário sem informar senha

**Objetivo:** Validar preservação da senha atual.

**Endpoint:** `PATCH /v1/user/{id}`

**Resultado esperado:** Retornar `200`

---

## CT-USR-031 — Atualizar usuário com ID inexistente e payload inválido

**Objetivo:** Validar o comportamento da atualização quando o ID informado não existe e o payload contém nome fora do limite.

**Endpoint:** `PATCH /v1/user/{id}`

**Resultado esperado:** Retornar `400`

---

## CT-USR-032 — Atualizar usuário com ID inexistente e email já utilizado

**Objetivo:** Validar que o ID inexistente é identificado antes da análise de unicidade do email.

**Endpoint:** `PATCH /v1/user/{id}`

**Resultado esperado:** Retornar `404`

---

## CT-USR-033 — Atualizar usuário inexistente

**Objetivo:** Validar tratamento de recurso inexistente.

**Endpoint:** `PATCH /v1/user/{id}`

**Resultado esperado:** Retornar `404`.


---

## CT-USR-034 — Usuário comum atualizar outro usuário

**Objetivo:** Validar autorização por usuário.

**Endpoint:** `PATCH /v1/user/{id}`

**Resultado esperado:** Retornar `403`.


---

## CT-USR-035 — Usuário comum alterar o próprio role para ADMIN

**Objetivo:** Verificar a regra de segurança identificada no código atual.

**Endpoint:** `PATCH /v1/user/{id}`

**Resultado esperado:** Retornar `403`.

---

# 8. Verificar administrador

## CT-USR-036 — ADMIN consultar o próprio cadastro

**Objetivo:** Validar consulta de autoridade administrativa.

**Endpoint:** `GET /v1/user/admin/{id}`

**Resultado esperado:** Retornar `200`.

---

## CT-USR-037 — Usuário comum consultar o próprio cadastro

**Objetivo:** Validar resposta para usuário que não é administrador.

**Endpoint:** `GET /v1/user/admin/{id}`

**Resultado esperado:** Retornar `200`.


---

## CT-USR-038 — Usuário comum consultar outro cadastro

**Objetivo:** Validar autorização da consulta.

**Endpoint:** `GET /v1/user/admin/{id}`

**Resultado esperado:** Retornar `403`.


---

## CT-USR-039 — Consultar usuário com ID inválido

**Objetivo:** Validar tratamento de identificador inválido/inexistente.

**Endpoint:** `GET /v1/user/admin/{id}`

**Resultado esperado:** Retornar `404`.


---

## CT-USR-040 — Consultar administrador sem token

**Objetivo:** Validar autenticação.

**Endpoint:** `GET /v1/user/admin/{id}`

**Resultado esperado:** Retornar `403`.


---

# VerificationController

## CT-VER-001 — Enviar código de cadastro para email novo

**Objetivo:** Validar o envio de código para email não cadastrado.

**Endpoint:** `POST /v1/verification/registration/send`

**Resultado esperado:** Retornar `200`.


---

## CT-VER-002 — Enviar código de cadastro para email já cadastrado

**Objetivo:** Validar a rejeição de email já cadastrado.

**Endpoint:** `POST /v1/verification/registration/send`

**Resultado esperado:** Retornar `400`.


---

## CT-VER-003 — Enviar código de cadastro com email inválido

**Objetivo:** Validar a rejeição de email vazio.

**Endpoint:** `POST /v1/verification/registration/send`

**Resultado esperado:** Retornar `400`.


---

## CT-VER-004 — Enviar código de recuperação para email existente

**Objetivo:** Validar o envio de código de recuperação e o disparo do email.

**Endpoint:** `POST /v1/verification/password-reset/send`

**Resultado esperado:** Retornar `200`.

---

## CT-VER-005 — Enviar recuperação para email inexistente

**Objetivo:** Validar o tratamento de email não cadastrado.

**Endpoint:** `POST /v1/verification/password-reset/send`

**Resultado esperado:** Retornar `404`.

---

## CT-VER-006 — Enviar recuperação com email vazio

**Objetivo:** Validar o campo de email no envio de recuperação.

**Endpoint:** `POST /v1/verification/password-reset/send`

**Resultado esperado:** Retornar `400`.

---

## CT-VER-007 — Confirmar recuperação com dados válidos

**Objetivo:** Validar a redefinição de senha com código válido.

**Endpoint:** `POST /v1/verification/password-reset/confirm`

**Resultado esperado:** Retornar `200`.

---

## CT-VER-008 — Confirmar recuperação com email vazio

**Objetivo:** Validar a rejeição de email vazio na confirmação.

**Endpoint:** `POST /v1/verification/password-reset/confirm`

**Resultado esperado:** Retornar `400`.

---

## CT-VER-009 — Confirmar recuperação com código vazio

**Objetivo:** Validar a rejeição de código vazio.

**Endpoint:** `POST /v1/verification/password-reset/confirm`

**Resultado esperado:** Retornar `400`.

---

## CT-VER-010 — Confirmar recuperação com código expirado

**Objetivo:** Validar a rejeição de código expirado.

**Endpoint:** `POST /v1/verification/password-reset/confirm`

**Resultado esperado:** Retornar `400`.

---

## CT-VER-011 — Confirmar recuperação com código utilizado

**Objetivo:** Validar a rejeição de código já utilizado.

**Endpoint:** `POST /v1/verification/password-reset/confirm`

**Resultado esperado:** Retornar `400`.

---

## CT-VER-012 — Confirmar recuperação com nova senha inválida

**Objetivo:** Validar a rejeição de nova senha fora das regras.

**Endpoint:** `POST /v1/verification/password-reset/confirm`

**Resultado esperado:** Retornar `400`.

---

## CT-VER-013 — Confirmar recuperação com código de outro usuário

**Objetivo:** Validar a consistência entre o código e o email informado.

**Endpoint:** `POST /v1/verification/password-reset/confirm`

**Resultado esperado:** Retornar `400`.

---

# ThemeController

## CT-THM-001 — Criar tema para usuário existente

**Objetivo:** Validar criação de tema com dados válidos.

**Endpoint:** `POST /v1/theme/{userId}`

**Resultado esperado:** Retornar `201`.

---

## CT-THM-002 — Criar tema para usuário inexistente ou com ID inválido

**Objetivo:** Validar o identificador do usuário.

**Endpoint:** `POST /v1/theme/{userId}`

**Resultado esperado:** Retornar `404`.

---

## CT-THM-003 — Criar tema com nome vazio.

**Objetivo:** Validar o campo `name`.

**Endpoint:** `POST /v1/theme/{userId}`

**Resultado esperado:** Retornar `400`.

---

## CT-THM-004 — Usuário autenticado criar tema para outro usuário

**Objetivo:** Verificar a autorização baseada no proprietário.

**Endpoint:** `POST /v1/theme/{userId}`

**Resultado esperado:** Retornar `403`.

---

## CT-THM-005 — Listar temas existentes

**Objetivo:** Validar listagem de temas.

**Endpoint:** `GET /v1/theme`

**Resultado esperado:** Retornar `200`.

---

## CT-THM-006 — Listar temas sem autenticação

**Objetivo:** Validar a autenticação da listagem.

**Endpoint:** `GET /v1/theme`

**Resultado esperado:** Retornar `403`.

---

## CT-THM-007 — Buscar tema existente por ID

**Objetivo:** Validar consulta de tema.

**Endpoint:** `GET /v1/theme/{themeId}`

**Resultado esperado:** Retornar `200`.

---

## CT-THM-008 — Buscar tema inexistente ou com ID inválido

**Objetivo:** Validar o identificador informado.

**Endpoint:** `GET /v1/theme/{themeId}`

**Resultado esperado:** Retornar `404`

---

## CT-THM-009 — Buscar tema por ID sem autenticação

**Objetivo:** Validar a autenticação da busca por ID.

**Endpoint:** `GET /v1/theme/{themeId}`

**Resultado esperado:** Retornar `403`.

---

## CT-THM-010 — Buscar tema por nome

**Objetivo:** Validar busca por nome.

**Endpoint:** `GET /v1/theme/name/{themeName}`

**Resultado esperado:** Retornar `200`.

---

## CT-THM-011 — Buscar tema inexistente por nome

**Objetivo:** Validar ausência de correspondência.

**Endpoint:** `GET /v1/theme/name/{themeName}`

**Resultado esperado:** Retornar `404`.

---

## CT-THM-012 — Buscar tema por nome sem autenticação

**Objetivo:** Validar a busca por nome sem token.

**Endpoint:** `GET /v1/theme/name/{themeName}`

**Resultado esperado:** Retornar `403`.

---

## CT-THM-013 — Buscar temas de usuário existente

**Objetivo:** Validar consulta dos temas de um usuário.

**Endpoint:** `GET /v1/theme/user/{userId}`

**Resultado esperado:** Retornar `200` com os temas do usuário.

---

## CT-THM-014 — Buscar temas de usuário sem temas

**Objetivo:** Validar lista vazia.

**Endpoint:** `GET /v1/theme/user/{userId}`

**Resultado esperado:** Retornar `200` com lista vazia.

---

## CT-THM-015 — Buscar temas de usuário inexistente ou com ID inválido

**Objetivo:** Validar o usuário informado.

**Endpoint:** `GET /v1/theme/user/{userId}`

**Resultado esperado:** Retornar `404`.

---

## CT-THM-016 — Buscar temas de usuário sem autenticação

**Objetivo:** Validar a busca por usuário sem token.

**Endpoint:** `GET /v1/theme/user/{userId}`

**Resultado esperado:** Retornar `403`.

---

## CT-THM-017 — Buscar temas de trace table existente

**Objetivo:** Validar temas associados a uma trace table.

**Endpoint:** `GET /v1/theme/trace/{traceId}?page=&size=&sort=`

**Resultado esperado:** Retornar `200` com os temas associados.

---

## CT-THM-018 — Buscar temas de trace table inexistente

**Objetivo:** Validar erro de trace.

**Endpoint:** `GET /v1/theme/trace/{traceId}?page=&size=&sort=`

**Resultado esperado:** Retornar `404`.

---

## CT-THM-019 — Buscar temas de trace table sem autenticação

**Objetivo:** Validar a autenticação da consulta por trace table.

**Endpoint:** `GET /v1/theme/trace/{traceId}?page=&size=&sort=`

**Resultado esperado:** Retornar `403`.

---

## CT-THM-020 — Criador remover o próprio tema

**Objetivo:** Validar remoção autorizada.

**Endpoint:** `DELETE /v1/theme/{themeId}/{userId}`

**Resultado esperado:** Retornar `204`.

---

## CT-THM-021 — Remover tema com ID inexistente

**Objetivo:** Validar os identificadores da remoção.

**Endpoint:** `DELETE /v1/theme/{themeId}/{userId}`

**Resultado esperado:** Retornar `404`.

---

## CT-THM-022 — Remover tema com usuário inexistente

**Objetivo:** Validar o usuário informado na remoção.

**Endpoint:** `DELETE /v1/theme/{themeId}/{userId}`

**Resultado esperado:** Retornar `404`.

---

## CT-THM-023 — Usuário comum remover tema de outro criador

**Objetivo:** Validar a autorização do usuário comum.

**Endpoint:** `DELETE /v1/theme/{themeId}/{userId}`

**Resultado esperado:** Retornar `403`.

---

## CT-THM-024 — Remover tema sem token

**Objetivo:** Verificar autorização.

**Endpoint:** `DELETE /v1/theme/{themeId}/{userId}`

**Resultado esperado:** Retornar `403`.

---

## CT-THM-025 — Outro usuário remover tema de professor

**Objetivo:** Validar a autorização de outro usuário sobre o tema.

**Endpoint:** `DELETE /v1/theme/{themeId}/{userId}`

**Resultado esperado:** Retornar `403`.

---

## CT-THM-026 — Remover tema associado a trace table

**Objetivo:** Validar a remoção de tema associado a trace table.

**Endpoint:** `DELETE /v1/theme/{themeId}/{userId}`

**Resultado esperado:** Retornar `204`.

---

## CT-THM-027 — Atualizar tema com nome válido

**Objetivo:** Validar atualização autorizada com nome válido.

**Endpoint:** `PUT /v1/theme/{themeId}/{userId}`

**Resultado esperado:** Retornar `200`.

---

## CT-THM-028 — Atualizar tema com nome vazio

**Objetivo:** Validar nome vazio na atualização.

**Endpoint:** `PUT /v1/theme/{themeId}/{userId}`

**Resultado esperado:** Retornar `400`.

---

## CT-THM-029 — Atualizar tema inexistente

**Objetivo:** Validar atualização de tema inexistente.

**Endpoint:** `PUT /v1/theme/{themeId}/{userId}`

**Resultado esperado:** Retornar `404`.

---

## CT-THM-030 — Usuário comum atualizar tema de outro criador

**Objetivo:** Validar a autorização de usuário comum.

**Endpoint:** `PUT /v1/theme/{themeId}/{userId}`

**Resultado esperado:** Retornar `403`.

---

## CT-THM-031 — Atualizar tema sem token

**Objetivo:** Validar a atualização sem token.

**Endpoint:** `PUT /v1/theme/{themeId}/{userId}`

**Resultado esperado:** Retornar `403`.

---

## CT-THM-032 — Atualizar tema de outro usuário

**Objetivo:** Validar a autorização quando outro usuário tenta atualizar o tema.

**Endpoint:** `PUT /v1/theme/{themeId}/{userId}`

**Resultado esperado:** Retornar `403`.

---

# TraceTableController

## CT-TRT-001 — Criar trace table com JSON, imagem e temas válidos

**Objetivo:** Validar criação completa de trace table.

**Endpoint:** `POST /v1/trace/{userId}?themesIds=`

**Resultado esperado:** Retornar `201`.

---

## CT-TRT-002 — Criar trace table com JSON malformado

**Objetivo:** Validar requisição.

**Endpoint:** `POST /v1/trace/{userId}?themesIds=`

**Resultado esperado:** Retorna `400` ou `403`.

---

## CT-TRT-003 — Criar trace table com usuário inexistente ou ID inválido

**Objetivo:** Validar o `userId`.

**Endpoint:** `POST /v1/trace/{userId}?themesIds=`

**Resultado esperado:** Retornar `404`.

---

## CT-TRT-004 — Criar trace table com temas inexistentes

**Objetivo:** Validar `themesIds`.

**Endpoint:** `POST /v1/trace/{userId}?themesIds=`

**Resultado esperado:** Retorna `404`.

---

## CT-TRT-005 — Criar trace table com imagem vazia

**Objetivo:** Validar restrições de imagem vazia.

**Endpoint:** `POST /v1/trace/{userId}?themesIds=`

**Resultado esperado:** Retorna `400`.

---

## CT-TRT-006 — Criar trace table sem token ou com token inválido

**Objetivo:** Validar autenticação.

**Endpoint:** `POST /v1/trace/{userId}?themesIds=`

**Resultado esperado:** Retornar `403`.


---

## CT-TRT-007 — Usuário criar trace table usando userId de outra pessoa

**Objetivo:** Verificar autorização pelo usuário autenticado.

**Endpoint:** `POST /v1/trace/{userId}?themesIds=`

**Resultado esperado:** Retornar `403`.

---

## CT-TRT-008 — Criar trace table com Content-Type diferente de multipart/form-data

**Objetivo:** Validar o formato exigido pelo endpoint.

**Endpoint:** `POST /v1/trace/{userId}`

**Resultado esperado:** Retorna `400`.

---

## CT-TRT-009 — Buscar trace table existente por ID

**Objetivo:** Validar consulta pública de trace table.

**Endpoint:** `GET /v1/trace/{traceId}`

**Resultado esperado:** Retornar `200`.

---

## CT-TRT-010 — Buscar trace table inexistente ou com ID inválido

**Objetivo:** Validar identificador da trace table.

**Endpoint:** `GET /v1/trace/{traceId}`

**Resultado esperado:** Retornar `404`.

---

## CT-TRT-011 — Listar trace tables de usuário existente

**Objetivo:** Validar listagem por usuário.

**Endpoint:** `GET /v1/trace/user/{userId}?page=&size=&sort=`

**Resultado esperado:** Retornar `200`.

---

## CT-TRT-012 — Listar trace tables de usuário sem resultados

**Objetivo:** Validar página vazia.

**Endpoint:** `GET /v1/trace/user/{userId}?page=&size=&sort=`

**Resultado esperado:** Retorna `200` com página vazia.

---

## CT-TRT-013 — Listar trace tables com usuário inexistente ou ID inválido

**Objetivo:** Validar o usuário informado.

**Endpoint:** `GET /v1/trace/user/{userId}?page=&size=&sort=`

**Resultado esperado:** Retorna `404`.

---

## CT-TRT-014 — Listar trace tables de usuário sem autenticação

**Objetivo:** Confirmar que o endpoint é público.

**Endpoint:** `GET /v1/trace/user/{userId}?page=&size=&sort=`

**Resultado esperado:** Retorna `200`.

---

## CT-TRT-015 — Buscar trace tables por tema e professor com filtros válidos

**Objetivo:** Validar busca combinando tema e professor.

**Endpoint:** `GET /v1/trace/theme/name/{themeName}?prof=&page=&size=&sort=`

**Resultado esperado:** Retornar `200`.

---

## CT-TRT-016 — Buscar trace tables por tema inexistente

**Objetivo:** Validar tema sem correspondência.

**Endpoint:** `GET /v1/trace/theme/name/{themeName}?prof=&page=&size=&sort=`

**Resultado esperado:** Retorna `404`.

---

## CT-TRT-017 — Informar prof inválido

**Objetivo:** Validar o parâmetro `prof`.

**Endpoint:** `GET /v1/trace/theme/name/{themeName}?prof=&page=&size=&sort=`

**Resultado esperado:** Retorna `400`.

---

## CT-TRT-018 — Listar trace tables por tema existente

**Objetivo:** Validar consulta por `themeId`.

**Endpoint:** `GET /v1/trace/theme/{themeId}?page=&size=&sort=`

**Resultado esperado:** Retornar `200`.

---

## CT-TRT-019 — Tema sem trace tables

**Objetivo:** Validar retorno vazio.

**Endpoint:** `GET /v1/trace/theme/{themeId}?page=&size=&sort=`

**Resultado esperado:** Retorna `200` com página vazia.

---

## CT-TRT-020 — Informar tema inexistente ou ID inválido

**Objetivo:** Validar `themeId`.

**Endpoint:** `GET /v1/trace/theme/{themeId}?page=&size=&sort=`

**Resultado esperado:** Retorna `404`.

---

## CT-TRT-021 — Criador remover a própria trace table ou ADMIN remover outra

**Objetivo:** Validar remoção autorizada.

**Endpoint:** `DELETE /v1/trace/{traceId}/{userId}`

**Resultado esperado:** Retornar `204`.

---

## CT-TRT-022 — Remover trace table com IDs inexistentes/inválidos

**Objetivo:** Validar identificadores usados na exclusão.

**Endpoint:** `DELETE /v1/trace/{traceId}/{userId}`

**Resultado esperado:** Retorna `404`.

---

## CT-TRT-023 — Usuário comum remover trace table de outro criador

**Objetivo:** Validar autorização.

**Endpoint:** `DELETE /v1/trace/{traceId}/{userId}`

**Resultado esperado:** Retorna `403`.

---

## CT-TRT-024 — Remover trace table sem token ou com token inválido

**Objetivo:** Validar autenticação.

**Endpoint:** `DELETE /v1/trace/{traceId}/{userId}`

**Resultado esperado:** Retornar `403`.

---

## CT-TRT-025 — Usuário não proprietário usar userId do proprietário na remoção

**Objetivo:** Verificar possível bypass de autorização.

**Endpoint:** `DELETE /v1/trace/{traceId}/{userId}`

**Resultado esperado:** Retornar `403`.

---

## CT-TRT-026 — Atualizar trace table com payload e temas existentes

**Objetivo:** Validar atualização completa.

**Endpoint:** `PUT /v1/trace/{traceId}/{userId}?themesIds=`

**Resultado esperado:** Retornar `200`.

---

## CT-TRT-027 — Atualizar trace table informando lista vazia de temas

**Objetivo:** Validar atualização sem temas.

**Endpoint:** `PUT /v1/trace/{traceId}/{userId}?themesIds=`

**Resultado esperado:** Retornar `400` ou `403`.

---

## CT-TRT-028 — Atualizar trace table com themesIds inexistentes

**Objetivo:** Validar `themesIds`.

**Endpoint:** `PUT /v1/trace/{traceId}/{userId}?themesIds=`

**Resultado esperado:** Retornar `404`.

---

## CT-TRT-029 — Atualizar trace table com body malformado

**Objetivo:** Validar o body.

**Endpoint:** `PUT /v1/trace/{traceId}/{userId}?themesIds=`

**Resultado esperado:** Retornar `400`.

---

## CT-TRT-030 — Atualizar trace table com trace table inexistentes

**Objetivo:** Validar os identificadores.

**Endpoint:** `PUT /v1/trace/{traceId}/{userId}?themesIds=`

**Resultado esperado:** Retornar 404.

---

## CT-TRT-031 — Atualizar trace table sem token ou com token inválido

**Objetivo:** Validar autenticação.

**Endpoint:** `PUT /v1/trace/{traceId}/{userId}?themesIds=`

**Resultado esperado:** Retornar `403`.

---

## CT-TRT-032 — Usuário comum atualizar trace table de outro criador

**Objetivo:** Validar autorização.

**Endpoint:** `PUT /v1/trace/{traceId}/{userId}?themesIds=`

**Resultado esperado:** Retorna `403`.

---

## CT-TRT-033 — Usuário não proprietário usar userId do proprietário na atualização

**Objetivo:** Verificar possível bypass de autorização.

**Endpoint:** `PUT /v1/trace/{traceId}/{userId}?themesIds=`

**Resultado esperado:** Retornar `403`.

---

## CT-TRT-034 — Validar resposta com tabela completamente correta

**Objetivo:** Validar resposta correta do aluno.

**Endpoint:** `POST /v1/trace/check/{traceId}`

**Resultado esperado:** Retornar `200`.

---

## CT-TRT-035 — Enviar valor diferente em célula cujo esperado é #

**Objetivo:** Validar regra de célula ignorada.

**Endpoint:** `POST /v1/trace/check/{traceId}`

**Resultado esperado:** Retorna `200`

---

## CT-TRT-036 — Enviar valor incorreto ou tipo inválido

**Objetivo:** Validar identificação de células incorretas.

**Endpoint:** `POST /v1/trace/check/{traceId}`

**Resultado esperado:** Retornar `400`, com os erros contendo `row`, `column` e `errorMessage`.

---

## CT-TRT-037 — Validar representantes de string, int, double, float e boolean

**Objetivo:** Validar comparação dos diferentes tipos de célula.

**Endpoint:** `POST /v1/trace/check/{traceId}`

**Resultado esperado:** Retorna `200`.

---

## CT-TRT-038 — Validar trace table inexistente

**Objetivo:** Validar tratamento de dados não suportados.

**Endpoint:** `POST /v1/trace/check/{traceId}`

**Resultado esperado:** Retornar `404`.

---

## CT-TRT-039 — Validar dimensões incompatíveis

**Objetivo:** Validar estrutura da matriz recebida.

**Endpoint:** `POST /v1/trace/check/{traceId}`

**Resultado esperado:** Retorna `400`.

---

## CT-TRT-040 — Validar formato da lista de erros

**Objetivo:** Validar que os erros retornados contenham linha, coluna e mensagem.

**Endpoint:** `POST /v1/trace/check/{traceId}`

**Resultado esperado:** Retorna `400`.

---

## CT-TRT-041 — Validar resposta sem autenticação

**Objetivo:** Confirmar que `/check` é público.

**Endpoint:** `POST /v1/trace/check/{traceId}`

**Resultado esperado:** Retorna `200`.

---

## CT-TRT-042 — Submeter resposta para trace table existente

**Objetivo:** Validar persistência de resposta de aluno autenticado.

**Endpoint:** `POST /v1/trace/submit/{traceId}`

**Resultado esperado:** Retornar `200`.

---

## CT-TRT-043 — Submeter matriz vazia

**Objetivo:** Validar o comportamento do endpoint que atualmente apenas persiste os dados.

**Endpoint:** `POST /v1/trace/submit/{traceId}`

**Resultado esperado:** Retorna `200`.

---

## CT-TRT-044 — Submeter resposta sem token ou com token inválido

**Objetivo:** Validar autenticação.

**Endpoint:** `POST /v1/trace/submit/{traceId}`

**Resultado esperado:** Retornar `403`.

---

## CT-TRT-045 — Submeter resposta para trace table inexistente

**Objetivo:** Validar existência da trace table.

**Endpoint:** `POST /v1/trace/submit/{traceId}`

**Resultado esperado:** Retornar `404`.

---

## CT-TRT-046 — Submeter resposta com body malformado

**Objetivo:** Validar a estrutura da matriz recebida.

**Endpoint:** `POST /v1/trace/submit/{traceId}`

**Resultado esperado:** Retorna `400`.

---

## CT-TRT-047 — Verificar associação da resposta ao usuário autenticado

**Objetivo:** Validar consistência entre resposta salva e usuário do JWT.

**Endpoint:** `POST /v1/trace/submit/{traceId}`

**Resultado esperado:** Retorna `200`, com a resposta associada ao usuário autenticado.

---

## CT-TRT-048 — Consultar todas as respostas do exercício

**Objetivo:** Validar consulta de respostas.

**Endpoint:** `GET /v1/trace/answers/{traceId}?startDate=&endDate=&studentName=`

**Resultado esperado:** Retornar `200` com as respostas do exercício.

---

## CT-TRT-049 — Filtrar respostas por período

**Objetivo:** Validar filtro de datas.

**Endpoint:** `GET /v1/trace/answers/{traceId}?startDate=&endDate=&studentName=`

**Resultado esperado:** Retorna `200`, apenas com as respostas dentro do período informado.

---

## CT-TRT-050 — Filtrar respostas por aluno

**Objetivo:** Validar filtro `studentName`.

**Endpoint:** `GET /v1/trace/answers/{traceId}?startDate=&endDate=&studentName=`

**Resultado esperado:** Retorna `200`, apenas com respostas correspondentes ao aluno informado.

---

## CT-TRT-051 — Filtrar respostas por período e aluno

**Objetivo:** Validar combinação dos filtros.

**Endpoint:** `GET /v1/trace/answers/{traceId}?startDate=&endDate=&studentName=`

**Resultado esperado:** Retorna `200`, apenas com registros que atendam aos dois filtros.

---

## CT-TRT-052 — Consultar exercício sem respostas

**Objetivo:** Validar retorno sem registros.

**Endpoint:** `GET /v1/trace/answers/{traceId}?startDate=&endDate=&studentName=`

**Resultado esperado:** Retorna `200` com lista vazia.

---

## CT-TRT-053 — Consultar respostas com datas inválidas

**Objetivo:** Validar os filtros de data.

**Endpoint:** `GET /v1/trace/answers/{traceId}?startDate=&endDate=&studentName=`

**Resultado esperado:** Retorna `400`.

---

## CT-TRT-054 — Consultar respostas informando somente uma das datas

**Objetivo:** Validar o caso de fronteira dos filtros de período.

**Endpoint:** `GET /v1/trace/answers/{traceId}?startDate=&endDate=&studentName=`

**Resultado esperado:** Retorna `200`.

---

## CT-TRT-055 — Consultar respostas de trace table inexistente

**Objetivo:** Validar consistência do recurso consultado.

**Endpoint:** `GET /v1/trace/answers/{traceId}?startDate=&endDate=&studentName=`

**Resultado esperado:** Retorna `404`.

---

## CT-TRT-056 — Consultar respostas sem autenticação

**Objetivo:** Confirmar que a consulta é pública.

**Endpoint:** `GET /v1/trace/answers/{traceId}?startDate=&endDate=&studentName=`

**Resultado esperado:** Retorna `200`.

---

## CT-TRT-057 — Consultar métricas sem filtros

**Objetivo:** Validar consulta básica de métricas.

**Endpoint:** `GET /v1/trace/metrics/{traceId}?startDate=&endDate=&studentName=`

**Resultado esperado:** Retornar `200` com `MetricsResponse`.

---

# Resumo dos Casos

| Controller             | Casos documentados | Observação                                                                    |
|------------------------|-------------------:|-------------------------------------------------------------------------------|
| UserController         |                 40 | Casos correspondentes aos testes implementados em `UserApiTests.java`         |
| VerificationController |                 13 | Casos correspondentes aos testes implementados em `VerificationApiTests.java` |
| ThemeController        |                 32 | Casos correspondentes aos testes implementados em `ThemeApiTests.java`        |
| TraceTableController   |                 57 | Casos correspondentes aos testes implementados em `TraceTableApiTests.java`   |
| **Total documentado**  |            **142** | Casos correspondentes à suíte final                                           |