package com.taskmanager.task_manager_api.repository;

import com.taskmanager.task_manager_api.model.entity.LogNotificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LogNotificacaoRepository extends JpaRepository<LogNotificacao, UUID>{
    // Busca todos os logs de notificação de uma tarefa específica
    List<LogNotificacao> findByTarefaId(UUID tarefaId);
}
