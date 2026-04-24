-- Flyway executa este arquivo automaticamente na primeira vez que a app sobe

-- TABELA: usuarios
CREATE TABLE usuarios (
                          id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          nome            VARCHAR(100)    NOT NULL,
                          email           VARCHAR(150)    NOT NULL UNIQUE,
                          senha           VARCHAR(255)    NOT NULL,
                          perfil          VARCHAR(20)     NOT NULL DEFAULT 'ROLE_USER',
                          criado_em       TIMESTAMP       NOT NULL DEFAULT NOW(),
                          atualizado_em   TIMESTAMP       NOT NULL DEFAULT NOW()
);

-- TABELA: tarefas
CREATE TYPE status_tarefa AS ENUM ('PENDENTE', 'EM_ANDAMENTO', 'CONCLUIDA', 'CANCELADA');
CREATE TYPE prioridade_tarefa AS ENUM ('BAIXA', 'MEDIA', 'ALTA', 'URGENTE');

CREATE TABLE tarefas (
                         id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         usuario_id      UUID                NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
                         titulo          VARCHAR(200)        NOT NULL,
                         descricao       TEXT,
                         status          status_tarefa       NOT NULL DEFAULT 'PENDENTE',
                         prioridade      prioridade_tarefa   NOT NULL DEFAULT 'MEDIA',
                         data_prazo      TIMESTAMP,
                         notificado      BOOLEAN             NOT NULL DEFAULT FALSE,
                         deletado_em     TIMESTAMP,
                         criado_em       TIMESTAMP           NOT NULL DEFAULT NOW(),
                         atualizado_em   TIMESTAMP           NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_tarefas_usuario_id ON tarefas(usuario_id);
CREATE INDEX idx_tarefas_status     ON tarefas(status);
CREATE INDEX idx_tarefas_data_prazo ON tarefas(data_prazo);
CREATE INDEX idx_tarefas_ativas     ON tarefas(usuario_id) WHERE deletado_em IS NULL;

-- TABELA: log_notificacoes
CREATE TYPE status_envio AS ENUM ('ENVIADO', 'FALHOU');
CREATE TYPE tipo_notificacao AS ENUM ('LEMBRETE_PRAZO', 'TAREFA_ATRIBUIDA');

CREATE TABLE log_notificacoes (
                                  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                  tarefa_id       UUID                NOT NULL REFERENCES tarefas(id) ON DELETE CASCADE,
                                  tipo            tipo_notificacao    NOT NULL,
                                  status_envio    status_envio        NOT NULL,
                                  mensagem_erro   TEXT,
                                  enviado_em      TIMESTAMP           NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_log_notificacoes_tarefa_id ON log_notificacoes(tarefa_id);