package com.taskmanager.task_manager_api.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RespostaAutenticacao {

    private String accessToken;
    private String refreshToken;
    private String tipo;
    private String email;
    private String nome;

    public static RespostaAutenticacao of(String accessToken,
                                          String refreshToken,
                                          String email,
                                          String nome) {
        return RespostaAutenticacao.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tipo("Bearer")
                .email(email)
                .nome(nome)
                .build();
    }
}