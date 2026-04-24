package com.taskmanager.task_manager_api.repository;

import com.taskmanager.task_manager_api.model.entity.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, UUID>{
    List<Tarefa> findByUsuarioIdAndDeletadoEmIsNull(UUID usuarioId);

    Optional<Tarefa> findByIdAndUsuarioIdAndDeletadoEmIsNull(UUID id, UUID usuarioId);

    @Query("SELECT t FROM Tarefa t WHERE t.dataPrazo BETWEEN :agora AND :limite " +
            "AND t.notificado = false AND t.deletadoEm IS NULL")
    List<Tarefa> findTarefasParaNotificar(LocalDateTime agora, LocalDateTime limite);
}
