package com.taskmanager.task_manager_api.controller;

import com.taskmanager.task_manager_api.model.dto.RequisicaoTarefa;
import com.taskmanager.task_manager_api.model.dto.RespostaTarefa;
import com.taskmanager.task_manager_api.model.entity.Tarefa;
import com.taskmanager.task_manager_api.model.entity.Usuario;
import com.taskmanager.task_manager_api.service.ServicoTarefa;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tarefas")
@RequiredArgsConstructor
public class ControladorTarefa {

    private final ServicoTarefa servicoTarefa;

    @GetMapping
    public ResponseEntity<List<RespostaTarefa>> listar(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(servicoTarefa.listar(usuario));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RespostaTarefa> buscarPorId(@PathVariable UUID id, @AuthenticationPrincipal Usuario usuario){
        return ResponseEntity.ok(servicoTarefa.buscarPorId(id, usuario));
    }

    @PostMapping
    public ResponseEntity<RespostaTarefa> criar(
            @Valid @RequestBody RequisicaoTarefa requisicao,
            @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(servicoTarefa.criar(requisicao, usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RespostaTarefa> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody RequisicaoTarefa requisicao,
            @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(servicoTarefa.atualizar(id, requisicao, usuario));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<RespostaTarefa> atualizarStatus(
            @PathVariable UUID id,
            @RequestParam Tarefa.Status novoStatus,
            @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(servicoTarefa.atualizarStatus(id, novoStatus, usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable UUID id,
            @AuthenticationPrincipal Usuario usuario) {
        servicoTarefa.deletar(id, usuario);
        return ResponseEntity.noContent().build();
    }
}
