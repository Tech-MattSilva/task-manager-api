package com.taskmanager.task_manager_api.service;

import com.taskmanager.task_manager_api.model.dto.RequisicaoTarefa;
import com.taskmanager.task_manager_api.model.dto.RespostaTarefa;
import com.taskmanager.task_manager_api.model.entity.Tarefa;
import com.taskmanager.task_manager_api.model.entity.Usuario;
import com.taskmanager.task_manager_api.repository.TarefaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServicoTarefa {

    private final TarefaRepository tarefaRepository;

    // Lista todas as tarefas ativas do usuário
    public List<RespostaTarefa> listar(Usuario usuario) {
        return tarefaRepository
                .findByUsuarioIdAndDeletadoEmIsNull(usuario.getId())
                .stream()
                .map(RespostaTarefa::de)
                .collect(Collectors.toList());
    }

    // Busca uma tarefa específica do usuário
    public RespostaTarefa buscarPorId(UUID id, Usuario usuario) {
        Tarefa tarefa = tarefaRepository
                .findByIdAndUsuarioIdAndDeletadoEmIsNull(id, usuario.getId())
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));
        return RespostaTarefa.de(tarefa);
    }

    // Cria uma nova tarefa para o usuário
    public RespostaTarefa criar(RequisicaoTarefa requisicao, Usuario usuario) {
        Tarefa tarefa = Tarefa.builder()
                .usuario(usuario)
                .titulo(requisicao.getTitulo())
                .descricao(requisicao.getDescricao())
                .prioridade(requisicao.getPrioridade() != null
                        ? requisicao.getPrioridade()
                        : Tarefa.Prioridade.MEDIA)
                .dataPrazo(requisicao.getDataPrazo())
                .build();

        return RespostaTarefa.de(tarefaRepository.save(tarefa));
    }

    // Atualiza os dados de uma tarefa existente
    public RespostaTarefa atualizar(UUID id, RequisicaoTarefa requisicao, Usuario usuario) {
        Tarefa tarefa = tarefaRepository
                .findByIdAndUsuarioIdAndDeletadoEmIsNull(id, usuario.getId())
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));

        tarefa.setTitulo(requisicao.getTitulo());
        tarefa.setDescricao(requisicao.getDescricao());
        tarefa.setDataPrazo(requisicao.getDataPrazo());

        if (requisicao.getPrioridade() != null) {
            tarefa.setPrioridade(requisicao.getPrioridade());
        }

        return RespostaTarefa.de(tarefaRepository.save(tarefa));
    }

    // Atualiza só o status da tarefa
    public RespostaTarefa atualizarStatus(UUID id, Tarefa.Status novoStatus, Usuario usuario) {
        Tarefa tarefa = tarefaRepository
                .findByIdAndUsuarioIdAndDeletadoEmIsNull(id, usuario.getId())
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));

        tarefa.setStatus(novoStatus);
        return RespostaTarefa.de(tarefaRepository.save(tarefa));
    }

    // Soft delete: marca a tarefa como deletada sem remover do banco
    public void deletar(UUID id, Usuario usuario) {
        Tarefa tarefa = tarefaRepository
                .findByIdAndUsuarioIdAndDeletadoEmIsNull(id, usuario.getId())
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));

        tarefa.deletar();
        tarefaRepository.save(tarefa);
    }
}
