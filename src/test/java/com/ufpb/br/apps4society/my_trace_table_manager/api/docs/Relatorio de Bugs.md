# APÊNDICE A – RELATÓRIO DE BUGS

## Informações

| Campo              | Informação              |
|--------------------|-------------------------|
| Projeto            | My Trace Table Manager  |
| Versão             | Kawê                    |
| Ambiente           | Win 11                  |
| Período dos testes | 17/10/2026              |
| Responsável        | Victor Kawe             |

## BUG-001 — Aumento de privilégio por usuário comum

**Resumo:**
Um usuário comum consegue alterar a própria role para `ADMIN`, elevando seus privilégios sem autorização.

**Funcionalidade:**
updateUser

**Endpoint:**

```http
PATCH /v1/user/{id}
```

**Severidade:** `Crítica`
**Prioridade:** `Alta`
**Status:** `Corrigido`

### Resultado esperado

O sistema deve impedir a alteração de privilégio e retornar HTTP 403.

### Resultado obtido

A atualização é aceita e retorna HTTP 200.

### Resposta obtida

```text
CT-USR-035: esperado 403, obtido 200
```

### Caso de teste relacionado

CT-USR-035

---

## BUG-002 — Criação de recurso para outro usuário

**Resumo:**
O usuário consegue criar um recurso destinado a outro usuário, embora a operação devesse ser recusada. A descrição recebida para o CT-THM-004 menciona a criação de uma trace para outro usuário.

**Funcionalidade:**
Criação de recurso com identificação de outro usuário.

**Endpoint:**

```http
POST /v1/theme/{userId}
```

**Severidade:** `Alta`
**Prioridade:** `Alta`
**Status:** `Corrigido`

### Resultado esperado

O sistema deve negar a operação e retornar HTTP 403.

### Resultado obtido

A operação é aceita e retorna HTTP 201.

### Resposta obtida

```text
CT-THM-004: esperado 403, obtido 201
```

### Caso de teste relacionado

CT-THM-004

---

## BUG-003 — Consultas de temas disponíveis sem autenticação

**Resumo:**
As consultas de temas permitem acesso sem token de autenticação, incluindo listagem, busca por identificador, busca por nome e consulta dos temas associados a uma trace table.

**Funcionalidade:**
findAllThemes, findThemeById, busca de tema por nome e findAllThemesByTraceTable.

**Endpoint:**

```http
GET /v1/theme/**
```

**Severidade:** `Alta`
**Prioridade:** `Alta`
**Status:** `Corrigido`

### Resultado esperado

O sistema deve negar a requisição e retornar HTTP 403.

### Resultado obtido

As consultas retornam HTTP 200 sem exigir autenticação.

### Resposta obtida

```text
CT-THM-006: esperado 403, obtido 200
CT-THM-009: esperado 403, obtido 200
CT-THM-012: esperado 403, obtido 200
CT-THM-019: esperado 403, obtido 200
```

### Casos de teste relacionados

CT-THM-006 | CT-THM-009 | CT-THM-012 | CT-THM-019

---

## BUG-004 — Consulta de temas para trace table inexistente retorna 200

**Resumo:**
O sistema permite consultar temas associados a uma trace table inexistente e retorna sucesso em vez de indicar que o recurso não foi encontrado.

**Funcionalidade:**
findAllThemesByTraceTable.

**Endpoint:**

```http
GET /v1/theme/trace/{traceId}
```

**Severidade:** `Média`
**Prioridade:** `Média`
**Status:** `Corrigido`

### Resultado esperado

O sistema deve retornar HTTP 404, indicando que a trace table não foi encontrada.

### Resultado obtido

O sistema retorna HTTP 200.

### Resposta obtida

```text
CT-THM-018: esperado 404, obtido 200
```

### Caso de teste relacionado

CT-THM-018

---

## BUG-005 — Exclusão de tema sem autorização adequada

**Resumo:**
Usuários sem a autorização necessária conseguem excluir temas, incluindo usuário comum/aluno e professor tentando excluir tema de outro professor.

**Funcionalidade:**
deleteTheme.

**Endpoint:**

```http
DELETE /v1/theme/{themeId}/{userId}
```

**Severidade:** `Alta`
**Prioridade:** `Alta`
**Status:** `Corrigido`

### Resultado esperado

O sistema deve negar a operação e retornar HTTP 403.

### Resultado obtido

A exclusão é aceita e retorna HTTP 204.

### Resposta obtida

```text
CT-THM-023: esperado 403, obtido 204
CT-THM-025: esperado 403, obtido 204
```

### Casos de teste relacionados

CT-THM-023 | CT-THM-025

---

## BUG-006 — Atualização de tema sem autorização adequada

**Resumo:**
Usuários sem a autorização necessária conseguem atualizar temas, incluindo usuário comum/aluno e professor tentando alterar tema de outro professor.

**Funcionalidade:**
updateTheme.

**Endpoint:**

```http
PUT /v1/theme/{themeId}/{userId}
```

**Severidade:** `Alta`
**Prioridade:** `Alta`
**Status:** `Corrigido`

### Resultado esperado

O sistema deve negar a operação e retornar HTTP 403.

### Resultado obtido

A atualização é aceita e retorna HTTP 200.

### Resposta obtida

```text
CT-THM-030: esperado 403, obtido 200
CT-THM-032: esperado 403, obtido 200
```

### Casos de teste relacionados

CT-THM-030 | CT-THM-032

---

## BUG-007 — Criação de trace table para outro usuário

**Resumo:**
Um usuário consegue criar uma trace table para outro usuário usando um identificador informado na requisição.

**Funcionalidade:**
Criação de trace table.

**Endpoint:**

```http
POST /v1/trace/{userId}?themesIds=
```

**Severidade:** `Crítica`
**Prioridade:** `Alta`
**Status:** `Corrigido`

### Resultado esperado

O sistema deve negar a operação e retornar HTTP 403.

### Resultado obtido

O sistema retorna HTTP 201 e não bloqueia a operação conforme esperado.

### Resposta obtida

```text
CT-TRT-007: esperado 403, obtido 201
```

### Caso de teste relacionado

CT-TRT-007

---

## BUG-008 — Edição ou atualização de trace table de outro usuário

**Resumo:**
O sistema permite que um usuário edite ou atualize uma trace table de outro usuário ao informar o identificador do proprietário na requisição.

**Funcionalidade:**
Edição e atualização de trace table.

**Endpoint:**

```http
PUT /v1/trace/{traceId}/{userId}
```

**Severidade:** `Crítica`
**Prioridade:** `Alta`
**Status:** `Corrigido`

### Resultado esperado

O sistema deve negar a operação e retornar HTTP 403.

### Resultado obtido

As operações são aceitas e retornam os códigos HTTP informados abaixo.

### Resposta obtida

```text
CT-TRT-025: esperado 403, obtido 204
CT-TRT-033: esperado 403, obtido 200
```

### Casos de teste relacionados

CT-TRT-025 | CT-TRT-033

---

## BUG-009 — Código HTTP incorreto em cenário que deveria retornar 404

**Resumo:**
Um caso identificado como erro de código retorna HTTP 403 quando o resultado esperado é HTTP 404.

**Funcionalidade:**
Tratamento de erro na consulta exercitada pelo CT-TRT-045.

**Endpoint:**

```http
POST /v1/trace/submit/{traceId}
```

**Severidade:** `Média`
**Prioridade:** `Média`
**Status:** `Corrigido`

### Resultado esperado

O sistema deve retornar HTTP 404.

### Resultado obtido

O sistema retorna HTTP 403.

### Resposta obtida

```text
CT-TRT-045: esperado 404, obtido 403
```

### Caso de teste relacionado

CT-TRT-045

---

## BUG-010 — Consulta de respostas para trace table inexistente retorna 200

**Resumo:**
O sistema permite consultar respostas de uma trace table inexistente e retorna HTTP 200, em vez de informar que o recurso não foi encontrado.

**Funcionalidade:**
Consulta de respostas de trace table.

**Endpoint:**

```http
GET /v1/trace/answers/{traceId}?startDate=&endDate=&studentName=
```

**Severidade:** `Média`
**Prioridade:** `Média`
**Status:** `Corrigido`

### Resultado esperado

O sistema deve retornar HTTP 404.

### Resultado obtido

O sistema retorna HTTP 200.

### Resposta obtida

```text
CT-TRT-055: esperado 404, obtido 200
```

### Caso de teste relacionado

CT-TRT-055

---

# Resumo dos Bugs

| ID      | Bug                                                            | Casos relacionados        | Severidade | Prioridade | Status    |
|---------|----------------------------------------------------------------|---------------------------|------------|------------|-----------|
| BUG-001 | Escalação de privilégio ao atualizar usuário                   | CT-USR-035                | Crítica    | Alta       | Corrigido |
| BUG-002 | Criação de recurso para outro usuário                          | CT-THM-004                | Alta       | Alta       | Corrigido |
| BUG-003 | Consultas de temas disponíveis sem autenticação                | CT-THM-006, 009, 012, 019 | Alta       | Alta       | Corrigido |
| BUG-004 | Consulta de temas para trace table inexistente retorna 200     | CT-THM-018                | Média      | Média      | Corrigido |
| BUG-005 | Exclusão de tema sem autorização adequada                      | CT-THM-023, 025           | Alta       | Alta       | Corrigido |
| BUG-006 | Atualização de tema sem autorização adequada                   | CT-THM-030, 032           | Alta       | Alta       | Corrigido |
| BUG-007 | Criação de trace table para outro usuário                      | CT-TRT-007                | Crítica    | Alta       | Corrigido |
| BUG-008 | Edição ou atualização de trace table de outro usuário          | CT-TRT-025, 033           | Crítica    | Alta       | Corrigido |
| BUG-009 | Código HTTP incorreto em cenário que deveria retornar 404      | CT-TRT-045                | Média      | Média      | Corrigido |
| BUG-010 | Consulta de respostas para trace table inexistente retorna 200 | CT-TRT-055                | Média      | Média      | Corrigido |

---

# Métricas

| Métrica                                          | Quantidade |
|--------------------------------------------------|-----------:|
| Bugs confirmados (agrupados por comportamento)   |         10 |
| Casos de teste relacionados aos bugs confirmados |         16 |
| Bugs críticos                                    |          3 |
| Bugs altos                                       |          4 |
| Bugs médios                                      |          3 |
| Bugs baixos                                      |          0 |
| Bugs corrigidos                                  |         10 |
| Bugs abertos                                     |          0 |
