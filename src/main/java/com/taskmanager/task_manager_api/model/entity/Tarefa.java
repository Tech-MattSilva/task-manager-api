package com.taskmanager.task_manager_api.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "tarefas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tarefa {
    public enum Status { PENDENTE, EM_ANDAMENTO, CONCLUIDA, CANCELADA }
    public enum Prioridade { BAIXA, MEDIA, ALTA, URGENTE }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.PENDENTE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Prioridade prioridade = Prioridade.MEDIA;

    @Column(name = "data_prazo")
    private LocalDateTime dataPrazo;

    @Column(nullable = false)
    @Builder.Default
    private Boolean notificado = false;

    @Column(name = "deletado_em")
    private LocalDateTime deletadoEm;

    @OneToMany(mappedBy= "tarefa", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LogNotificacao> logNotificacaos = new ArrayList<>();

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    @PrePersist
    protected void aoSalvar() {
        criadoEm = LocalDateTime.now();
        atualizadoEm = LocalDateTime.now();
    }

    @PreUpdate
    protected void aoAtualizar() {
        atualizadoEm = LocalDateTime.now();
    }

    public void deletar() {
        this.deletadoEm = LocalDateTime.now();
    }

    public boolean isDeletado() {
        return this.deletadoEm != null;
    }
}
