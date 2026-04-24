package com.taskmanager.task_manager_api.scheduler;

import com.taskmanager.task_manager_api.model.entity.LogNotificacao;
import com.taskmanager.task_manager_api.model.entity.Tarefa;
import com.taskmanager.task_manager_api.repository.LogNotificacaoRepository;
import com.taskmanager.task_manager_api.repository.TarefaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class AgendadorNotificacoes {

    private final TarefaRepository tarefaRepository;
    private final LogNotificacaoRepository logNotificacaoRepository;
    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String emailRemetente;

    @Value("${app.mail.notify-hours-before}")
    private int horasAntes;

    // Roda todos os dias às 8h da manhã
    // Para testar localmente, troque por: fixedRate = 60000 (a cada 1 minuto)
    @Scheduled(cron = "0 0 8 * * *")
    public void enviarLembretesPrazo() {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime limite = agora.plusHours(horasAntes);

        log.info("Verificando tarefas com prazo entre {} e {}", agora, limite);

        List<Tarefa> tarefas = tarefaRepository.findTarefasParaNotificar(agora, limite);

        log.info("{} tarefa(s) encontrada(s) para notificar", tarefas.size());

        for (Tarefa tarefa : tarefas) {
            try {
                enviarEmail(tarefa);

                // Marca a tarefa como notificada para não enviar novamente
                tarefa.setNotificado(true);
                tarefaRepository.save(tarefa);

                // Registra o log de sucesso
                salvarLog(tarefa, LogNotificacao.StatusEnvio.ENVIADO, null);

                log.info("E-mail enviado para tarefa: {}", tarefa.getTitulo());

            } catch (Exception e) {
                // Registra o log de falha mas não para a execução
                salvarLog(tarefa, LogNotificacao.StatusEnvio.FALHOU, e.getMessage());
                log.error("Erro ao enviar e-mail para tarefa {}: {}",
                        tarefa.getTitulo(), e.getMessage());
            }
        }
    }

    private void enviarEmail(Tarefa tarefa) {
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setFrom(emailRemetente);
        mensagem.setTo(tarefa.getUsuario().getEmail());
        mensagem.setSubject("Lembrete: Tarefa com prazo próximo!");
        mensagem.setText(String.format(
                "Olá, %s!\n\n" +
                        "A tarefa \"%s\" vence em %s.\n\n" +
                        "Prioridade: %s\n" +
                        "Status atual: %s\n\n" +
                        "Não se esqueça de concluí-la a tempo!\n\n" +
                        "Atenciosamente,\nTask Manager API",
                tarefa.getUsuario().getNome(),
                tarefa.getTitulo(),
                tarefa.getDataPrazo(),
                tarefa.getPrioridade(),
                tarefa.getStatus()
        ));
        mailSender.send(mensagem);
    }

    private void salvarLog(Tarefa tarefa, LogNotificacao.StatusEnvio status, String erro) {
        LogNotificacao registro = LogNotificacao.builder()
                .tarefa(tarefa)
                .tipo(LogNotificacao.Tipo.LEMBRETE_PRAZO)
                .statusEnvio(status)
                .mensagemErro(erro)
                .build();
        logNotificacaoRepository.save(registro);
    }
}
