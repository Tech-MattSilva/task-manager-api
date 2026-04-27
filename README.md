# 📋 Task Manager API

API REST para gerenciamento de tarefas com autenticação JWT e notificações automáticas por e-mail, desenvolvida com Java e Spring Boot.

---

## 📌 Sobre o projeto

O **Task Manager API** é um sistema de gerenciamento de tarefas que permite aos usuários criar, organizar e acompanhar suas tarefas com prazos e prioridades. A API envia lembretes automáticos por e-mail quando uma tarefa está próxima do prazo.

Este projeto foi desenvolvido como portfólio, aplicando boas práticas de desenvolvimento de APIs REST com Java e Spring Boot.

---

## ✨ Funcionalidades

- ✅ Cadastro e autenticação de usuários com JWT
- ✅ Criação, listagem, atualização e exclusão de tarefas
- ✅ Controle de status das tarefas (Pendente → Em Andamento → Concluída)
- ✅ Prioridades (Baixa, Média, Alta, Urgente)
- ✅ Notificações automáticas por e-mail com prazo próximo
- ✅ Soft delete (tarefas não são removidas permanentemente)
- ✅ Histórico de notificações enviadas
- ✅ Documentação interativa com Swagger UI

---

## 🛠️ Tecnologias utilizadas

| Tecnologia | Versão | Descrição |
|---|---|---|
| Java | 17 | Linguagem principal |
| Spring Boot | 3.5.13 | Framework principal |
| Spring Security | 6.x | Autenticação e autorização |
| Spring Data JPA | 3.x | Persistência de dados |
| PostgreSQL | 18 | Banco de dados relacional |
| Flyway | 11.x | Migrations de banco de dados |
| JWT (jjwt) | 0.12.5 | Geração e validação de tokens |
| JavaMailSender | - | Envio de e-mails |
| Lombok | 1.18.x | Redução de código boilerplate |
| SpringDoc OpenAPI | 2.5.0 | Documentação Swagger |

---

## 🏗️ Arquitetura do projeto

```
src/main/java/com/taskmanager/task_manager_api/
├── config/          # Configurações de segurança e Swagger
├── controller/      # Controladores REST
├── service/         # Regras de negócio
├── repository/      # Acesso ao banco de dados
├── model/
│   ├── entity/      # Entidades JPA (Usuario, Tarefa, LogNotificacao)
│   └── dto/         # Objetos de transferência de dados
├── security/        # Filtro JWT e serviço de token
└── scheduler/       # Agendador de notificações por e-mail
```

---

## 🔐 Autenticação

A API utiliza autenticação **JWT (JSON Web Token)**. Para acessar os endpoints protegidos, é necessário:

1. Cadastrar um usuário em `POST /auth/registrar`
2. Fazer login em `POST /auth/login` e obter o `accessToken`
3. Enviar o token no header de todas as requisições:

```
Authorization: Bearer {seu_token}
```

---

## 📮 Endpoints

### Autenticação
| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| POST | `/auth/registrar` | Cadastra novo usuário | Não |
| POST | `/auth/login` | Autentica e retorna tokens | Não |

### Tarefas
| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| GET | `/tarefas` | Lista todas as tarefas | Sim |
| POST | `/tarefas` | Cria nova tarefa | Sim |
| GET | `/tarefas/{id}` | Busca tarefa por ID | Sim |
| PUT | `/tarefas/{id}` | Atualiza tarefa | Sim |
| PUT | `/tarefas/{id}/status` | Atualiza status da tarefa | Sim |
| DELETE | `/tarefas/{id}` | Remove tarefa (soft delete) | Sim |

---

## ⚙️ Como executar o projeto

### Pré-requisitos
- Java 17+
- PostgreSQL instalado e rodando
- Maven

### Passo a passo

**1. Clone o repositório**
```bash
git clone https://github.com/Tech-MattSilva/task-manager-api.git
cd task-manager-api
```

**2. Configure o banco de dados**

Crie o banco e o usuário no PostgreSQL:
```sql
CREATE DATABASE taskmanager;
CREATE USER taskuser WITH PASSWORD 'taskpass';
GRANT ALL PRIVILEGES ON DATABASE taskmanager TO taskuser;
\c taskmanager
GRANT ALL ON SCHEMA public TO taskuser;
```

**3. Configure o e-mail**

No arquivo `src/main/resources/application.yml`, substitua as credenciais do SMTP:
```yaml
spring:
  mail:
    username: SEU_USUARIO
    password: SUA_SENHA
```
> Para testes locais, utilize o [Mailtrap](https://mailtrap.io) — servidor SMTP gratuito para desenvolvimento.

**4. Execute a aplicação**
```bash
./mvnw spring-boot:run
```

**5. Acesse a documentação Swagger**
```
http://localhost:8080/swagger-ui.html
```

---

## 📧 Notificações por e-mail

A API possui um agendador (`@Scheduled`) que roda todos os dias às **8h da manhã** e verifica tarefas com prazo nas próximas 24 horas. Para cada tarefa encontrada, um e-mail de lembrete é enviado automaticamente ao usuário responsável.

O histórico de todos os envios (sucesso ou falha) fica registrado na tabela `log_notificacoes`.

---

## 🗃️ Modelo de dados

```
usuarios
├── id (UUID)
├── nome
├── email (único)
├── senha (hash BCrypt)
└── perfil

tarefas
├── id (UUID)
├── usuario_id (FK)
├── titulo
├── descricao
├── status (PENDENTE, EM_ANDAMENTO, CONCLUIDA, CANCELADA)
├── prioridade (BAIXA, MEDIA, ALTA, URGENTE)
├── data_prazo
├── notificado
└── deletado_em (soft delete)

log_notificacoes
├── id (UUID)
├── tarefa_id (FK)
├── tipo
├── status_envio
├── mensagem_erro
└── enviado_em
```

---

## 👨‍💻 Autor

Feito por **Matheus Melo**

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://linkedin.com/in/seu-perfil)
[![GitHub](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/Tech-MattSilva)