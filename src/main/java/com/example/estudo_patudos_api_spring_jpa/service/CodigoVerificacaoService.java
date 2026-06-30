package com.example.estudo_patudos_api_spring_jpa.service;

import com.example.estudo_patudos_api_spring_jpa.model.CodigoVerificacao;
import com.example.estudo_patudos_api_spring_jpa.model.TipoCodigo;
import com.example.estudo_patudos_api_spring_jpa.model.Usuario;
import com.example.estudo_patudos_api_spring_jpa.repository.CodigoVerificacaoRepository;
import com.example.estudo_patudos_api_spring_jpa.service.email.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CodigoVerificacaoService {

    private static final int EXPIRACAO_MIN = 15;     // validade do código
    private static final int COOLDOWN_SEG = 60;      // intervalo mínimo entre envios
    private static final int MAX_TENTATIVAS = 5;     // tentativas de validação antes de invalidar

    private final CodigoVerificacaoRepository repo;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom random = new SecureRandom();

    public CodigoVerificacaoService(CodigoVerificacaoRepository repo, EmailService emailService,
                                    PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    // Gera um código novo (invalidando os anteriores) e envia para o e-mail atual do usuário.
    // @Transactional aqui também: como esta entrada delega para a versão de 3 args na MESMA classe
    // (auto-invocação não passa pelo proxy), a transação precisa começar já neste ponto de entrada.
    @Transactional
    public void gerarEEnviar(Usuario usuario, TipoCodigo tipo) {
        gerarEEnviar(usuario, tipo, usuario.getEmail());
    }

    // Igual, mas envia para um DESTINO específico (ex.: o NOVO e-mail na troca protegida).
    // SEM cooldown: usado em ações deliberadas (cadastro, solicitar troca) que disparam o código.
    @Transactional
    public void gerarEEnviar(Usuario usuario, TipoCodigo tipo, String destino) {
        repo.invalidarAtivos(usuario, tipo);

        String codigo = String.format("%06d", random.nextInt(1_000_000));
        CodigoVerificacao cv = new CodigoVerificacao();
        cv.setUsuario(usuario);
        cv.setTipo(tipo);
        cv.setCodigoHash(passwordEncoder.encode(codigo));
        cv.setExpiraEm(LocalDateTime.now().plusMinutes(EXPIRACAO_MIN));
        repo.save(cv);

        emailService.enviar(destino, assuntoDe(tipo), corpoDe(tipo, codigo));
    }

    // Reenvio explícito (botão "reenviar código" / "esqueci a senha") — respeita o cooldown.
    @Transactional
    public void reenviar(Usuario usuario, TipoCodigo tipo) {
        reenviar(usuario, tipo, usuario.getEmail());
    }

    @Transactional
    public void reenviar(Usuario usuario, TipoCodigo tipo, String destino) {
        Optional<CodigoVerificacao> ultimo = repo.findTopByUsuarioAndTipoAndUsadoFalseOrderByCriadoEmDesc(usuario, tipo);
        if (ultimo.isPresent()
                && ultimo.get().getCriadoEm() != null
                && ultimo.get().getCriadoEm().isAfter(LocalDateTime.now().minusSeconds(COOLDOWN_SEG))) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Aguarde alguns instantes antes de pedir um novo código.");
        }
        gerarEEnviar(usuario, tipo, destino);
    }

    // Valida o código informado. Lança 400 com mensagem amigável em qualquer falha.
    @Transactional
    public void validar(Usuario usuario, TipoCodigo tipo, String codigo) {
        CodigoVerificacao cv = repo.findTopByUsuarioAndTipoAndUsadoFalseOrderByCriadoEmDesc(usuario, tipo)
                .orElseThrow(() -> badRequest("Código inválido ou expirado. Solicite um novo."));

        if (cv.getExpiraEm().isBefore(LocalDateTime.now())) {
            cv.setUsado(true);
            repo.save(cv);
            throw badRequest("Código expirado. Solicite um novo.");
        }
        if (cv.getTentativas() >= MAX_TENTATIVAS) {
            cv.setUsado(true);
            repo.save(cv);
            throw badRequest("Muitas tentativas. Solicite um novo código.");
        }

        cv.setTentativas(cv.getTentativas() + 1);

        if (!passwordEncoder.matches(codigo == null ? "" : codigo.trim(), cv.getCodigoHash())) {
            repo.save(cv);
            throw badRequest("Código incorreto.");
        }

        cv.setUsado(true);
        repo.save(cv);
    }

    // Aviso de segurança ao e-mail ANTIGO quando alguém solicita a troca (dica de UX do plano).
    public void notificarTrocaEmailSolicitada(String emailAntigo, String novoEmail) {
        String corpo = "Olá!\n\nRecebemos uma solicitação para alterar o e-mail da sua conta Patudos da Rua "
                + "para " + novoEmail + "."
                + "\n\nSe foi você, basta confirmar com o código que enviamos ao novo e-mail."
                + "\n\nSe NÃO foi você, troque sua senha imediatamente e fale com a ONG — sua conta pode estar em risco."
                + "\n\nEquipe Patudos da Rua 🐾";
        emailService.enviar(emailAntigo, "Patudos da Rua — Alteração de e-mail solicitada", corpo);
    }

    private String assuntoDe(TipoCodigo tipo) {
        return tipo == TipoCodigo.RESET_SENHA
                ? "Patudos da Rua — Recuperação de senha"
                : "Patudos da Rua — Confirme seu e-mail";
    }

    private String corpoDe(TipoCodigo tipo, String codigo) {
        String acao = tipo == TipoCodigo.RESET_SENHA ? "redefinir sua senha" : "confirmar seu e-mail";
        return "Olá!\n\nSeu código para " + acao + " é:\n\n    " + codigo
                + "\n\nEle expira em " + EXPIRACAO_MIN + " minutos. Se você não solicitou, ignore este e-mail."
                + "\n\nEquipe Patudos da Rua 🐾";
    }

    private ResponseStatusException badRequest(String msg) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
    }
}
