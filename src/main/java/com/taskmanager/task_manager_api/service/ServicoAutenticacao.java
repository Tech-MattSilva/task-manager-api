package com.taskmanager.task_manager_api.service;

import com.taskmanager.task_manager_api.model.dto.RequisicaoLogin;
import com.taskmanager.task_manager_api.model.dto.RequisicaoRegistro;
import com.taskmanager.task_manager_api.model.dto.RespostaAutenticacao;
import com.taskmanager.task_manager_api.model.entity.Usuario;
import com.taskmanager.task_manager_api.repository.UsuarioRepository;
import com.taskmanager.task_manager_api.security.ServicoJwt;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServicoAutenticacao {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder codificadorSenha;
    private final ServicoJwt servicoJwt;
    private final AuthenticationManager gerenciadorAutenticacao;

    public RespostaAutenticacao registrar(RequisicaoRegistro requisicao) {
        //Verifica se o email já está cadastrado
        if(usuarioRepository.existsByEmail(requisicao.getEmail())){
            throw new RuntimeException("Email já cadastrado");
        }

        //Cria o usuário com a senha codificada
        Usuario usuario = Usuario.builder()
                .nome(requisicao.getNome())
                .email(requisicao.getEmail())
                .senha(codificadorSenha.encode(requisicao.getSenha()))
                .build();

        usuarioRepository.save(usuario);

        //Gera os tokens para o usuário que acaba de se cadastrar
        String accessToken = servicoJwt.gerarAccessToken(usuario);
        String refreshToken = servicoJwt.gerarRefreshToken(usuario);

        return RespostaAutenticacao.of(accessToken, refreshToken, usuario.getEmail(), usuario.getNome());
    }

    public RespostaAutenticacao login(RequisicaoLogin requisicao){

        //Autentica o usuário e lança exceção se credenciais inválidas
        gerenciadorAutenticacao.authenticate(
                new UsernamePasswordAuthenticationToken(requisicao.getEmail(), requisicao.getSenha())
        );

        //Busca usuário no banco de dados
        Usuario usuario = usuarioRepository.findByEmail(requisicao.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        //Gera os tokens
        String accessToken = servicoJwt.gerarAccessToken(usuario);
        String refreshToken = servicoJwt.gerarRefreshToken(usuario);

        return RespostaAutenticacao.of(accessToken, refreshToken, usuario.getEmail(), usuario.getNome());

    }
}
