package com.taskmanager.task_manager_api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class FiltroAutenticacaoJwt extends OncePerRequestFilter{
    private final ServicoJwt servicoJwt;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest requisicao,
                                    @NonNull HttpServletResponse resposta,
                                    @NonNull FilterChain cadeiaFiltros)

            throws ServletException, IOException {
        final String headerAutorizacao = requisicao.getHeader("Autorizacao");

        if(headerAutorizacao == null || !headerAutorizacao.startsWith("Bearer ")){
            cadeiaFiltros.doFilter(requisicao, resposta);
            return;
        }

        final String jwt = headerAutorizacao.substring(7);
        final String emailUsuario;

        try{
            emailUsuario = servicoJwt.extrairEmail(jwt);
        } catch (Exception e ){
            cadeiaFiltros.doFilter(requisicao, resposta);
            return;
        }

        if(emailUsuario != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = userDetailsService.loadUserByUsername(emailUsuario);

            if(servicoJwt.isTokenValido(jwt, userDetails)){

                UsernamePasswordAuthenticationToken tokenAutenticacao = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                tokenAutenticacao.setDetails(new WebAuthenticationDetailsSource().buildDetails(requisicao));

                SecurityContextHolder.getContext().setAuthentication(tokenAutenticacao);
            }
        }

        cadeiaFiltros.doFilter(requisicao, resposta);
    }
}
