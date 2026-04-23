package com.taskmanager.task_manager_api.controller;

import com.taskmanager.task_manager_api.model.dto.RequisicaoLogin;
import com.taskmanager.task_manager_api.model.dto.RequisicaoRegistro;
import com.taskmanager.task_manager_api.model.dto.RespostaAutenticacao;
import com.taskmanager.task_manager_api.service.ServicoAutenticacao;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class ControladorAutenticacao {

    private final ServicoAutenticacao servicoAutenticacao;

    @PostMapping("/registrar")
    public ResponseEntity<RespostaAutenticacao> registrar(@Valid @RequestBody RequisicaoRegistro requisicao){
        return ResponseEntity.ok(servicoAutenticacao.registrar(requisicao));
    }

    @PostMapping("/login")
    public ResponseEntity<RespostaAutenticacao> login(@Valid @RequestBody RequisicaoLogin requisicao){
        return ResponseEntity.ok(servicoAutenticacao.login(requisicao));
    }


}
