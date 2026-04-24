package com.taskmanager.task_manager_api.config;

import com.taskmanager.task_manager_api.repository.UsuarioRepository;
import com.taskmanager.task_manager_api.security.FiltroAutenticacaoJwt;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class ConfiguracaoSeguranca {

    private final FiltroAutenticacaoJwt filtroAutenticacaoJwt;
    private final UsuarioRepository usuarioRepository;

    @Bean
    public SecurityFilterChain cadeiaFiltrosSeguranca(HttpSecurity http) throws Exception {
        http
                // Desativa CSRF — APIs REST stateless não precisam
                .csrf(AbstractHttpConfigurer::disable)

                // Rotas públicas e protegidas
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/api-docs/**",
                                "/actuator/health"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                // Stateless: sem sessão HTTP, cada requisição precisa do token JWT
                .sessionManagement(sessao ->
                        sessao.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authenticationProvider(provedorAutenticacao())

                // Adiciona nosso filtro JWT antes do filtro padrão
                .addFilterBefore(filtroAutenticacaoJwt, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuário não encontrado: " + email));
    }

    @Bean
    public DaoAuthenticationProvider provedorAutenticacao() {
        DaoAuthenticationProvider provedor = new DaoAuthenticationProvider();
        provedor.setUserDetailsService(userDetailsService());
        provedor.setPasswordEncoder(codificadorSenha());
        return provedor;
    }

    @Bean
    public PasswordEncoder codificadorSenha() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public AuthenticationManager gerenciadorAutenticacao(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}
