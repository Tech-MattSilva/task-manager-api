package com.taskmanager.task_manager_api.model.dto;

import com.taskmanager.task_manager_api.model.entity.Tarefa;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequisicaoTarefa {

    @NotBlank(message = "Título é obrigatório")
    @Size(min = 2, max = 200, message = "Título deve ter entre 2 e 200 caracteres")
    private String titulo;

    private String descricao;

    private Tarefa.Prioridade prioridade;

    private LocalDateTime dataPrazo;
}
