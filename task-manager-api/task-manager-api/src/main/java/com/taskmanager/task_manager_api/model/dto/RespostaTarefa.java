package com.taskmanager.task_manager_api.model.dto;

import com.taskmanager.task_manager_api.model.entity.Tarefa;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RespostaTarefa {

    private UUID id;
    private String titulo;
    private String descricao;
    private Tarefa.Status status;
    private Tarefa.Prioridade prioridade;
    private LocalDateTime dataPrazo;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    public static RespostaTarefa de(Tarefa tarefa) {
        return RespostaTarefa.builder()
                .id(tarefa.getId())
                .titulo(tarefa.getTitulo())
                .descricao(tarefa.getDescricao())
                .status(tarefa.getStatus())
                .prioridade(tarefa.getPrioridade())
                .dataPrazo(tarefa.getDataPrazo())
                .criadoEm(tarefa.getCriadoEm())
                .atualizadoEm(tarefa.getAtualizadoEm())
                .build();
    }
}
