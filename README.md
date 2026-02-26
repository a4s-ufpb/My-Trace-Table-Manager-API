# 🚀 My-Trace-Table-Manager-API

Backend oficial do projeto **Apps4Society (UFPB)**.

Este serviço é responsável por:

- Lógica de negócio
- Persistência de dados (PostgreSQL)
- Armazenamento de arquivos (MinIO)
- Exposição de endpoints REST (Spring Boot)

---

# 🏗️ Arquitetura do Sistema

O sistema é composto por três repositórios:

- 👤 Front-end Usuário  
  https://github.com/a4s-ufpb/My-Trace-Table

- 🖥️ Front-end Administrativo  
  https://github.com/a4s-ufpb/My-Trace-Table-Manager

- ⚙️ Backend (API)  
  https://github.com/a4s-ufpb/My-Trace-Table-Manager-API

⚠️ A API deve estar rodando para que os dois front-ends funcionem corretamente.

---

# 🐳 Como Rodar a API com Docker

O projeto utiliza Docker Compose para subir automaticamente:

- PostgreSQL
- pgAdmin
- MinIO
- API (Spring Boot)

---

## 1️⃣ Pré-requisitos

- Docker instalado
- Docker Compose instalado

Verifique com:

```bash
docker --version
docker compose version
```

---

## 2️⃣ Configuração do `.env`

O repositório possui o arquivo:

```
.env.example
```

### Passos:

1. Copie o arquivo:

```bash
cp .env.example .env
```

*(No Windows, copie manualmente e renomeie para `.env`)*

2. Verifique se as variáveis principais estão assim:

```env
POSTGRES_HOST=db
MINIO_URL=http://minio:9000
```

⚠️ Esses valores são importantes porque os containers se comunicam pelo nome do serviço dentro da rede Docker.

---

## 3️⃣ Subindo os Containers

Na raiz do projeto, execute:

```bash
docker compose up -d --build
```

Isso irá:

- Construir a imagem da API
- Subir o PostgreSQL
- Subir o pgAdmin
- Subir o MinIO
- Subir a API
- Rodar tudo em segundo plano (-d)

---

## 4️⃣ Serviços Disponíveis

Após iniciar, os serviços estarão acessíveis em:

- 🚀 API  
  http://localhost:8080

- 📘 Swagger  
  http://localhost:8080/swagger-ui/index.html

- 🐘 pgAdmin  
  http://localhost:5050

- 📦 MinIO Console  
  http://localhost:9001

- 📦 MinIO API  
  http://localhost:9000

---

# 🐘 Acessando o Banco pelo pgAdmin

1. Acesse:

```
http://localhost:5050
```

2. Login:

```
Email: admin@ufpb.br
Senha: admin
```

3. Criar novo servidor com:

- Host: db
- Porta: 5432
- Usuário: (definido no .env)
- Senha: (definida no .env)

---

# 📦 Acessando o MinIO

Acesse:

```
http://localhost:9001
```

Use as credenciais definidas no `.env`.

O bucket será utilizado para armazenamento de arquivos do sistema.

---

# 🔌 Como Usar a API

## 📘 Swagger (Forma Recomendada)

A documentação interativa está disponível em:

```
http://localhost:8080/swagger-ui/index.html
```

Você pode:

- Visualizar endpoints
- Testar requisições
- Ver modelos de request/response

---

## 🌐 Base URL da API

```
http://localhost:8080/v1
```

---

# 🧹 Parando os Containers

Para parar os serviços:

```bash
docker compose down
```

Para remover volumes também:

```bash
docker compose down -v
```

---

# 🧠 Estrutura do Docker Compose

O projeto sobe automaticamente os seguintes serviços:

- db → PostgreSQL 16
- pgadmin → Interface visual do banco
- minio → Object Storage
- trace-api → Aplicação Spring Boot

Todos conectados na rede:

```
tracetable-network
```

---

# 🔗 Ordem Recomendada para Rodar o Sistema Completo

1. Subir a API (My-Trace-Table-Manager-API)
2. Subir o front-end administrativo (My-Trace-Table-Manager)
3. Subir o front-end do usuário (My-Trace-Table)

---

# 📌 Observações Importantes

- A API depende do banco e do MinIO
- Os front-ends dependem da API
- A comunicação entre containers ocorre pela rede Docker
