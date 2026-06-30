package com.example.estudo_patudos_api_spring_jpa.service;

import com.example.estudo_patudos_api_spring_jpa.dto.auth.*;
import com.example.estudo_patudos_api_spring_jpa.model.Role;
import com.example.estudo_patudos_api_spring_jpa.model.TipoCodigo;
import com.example.estudo_patudos_api_spring_jpa.model.Usuario;
import com.example.estudo_patudos_api_spring_jpa.repository.UsuarioRepository;
import com.example.estudo_patudos_api_spring_jpa.security.GoogleTokenVerifier;
import com.example.estudo_patudos_api_spring_jpa.security.JwtService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CodigoVerificacaoService codigoService;
    private final GoogleTokenVerifier googleTokenVerifier;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService, AuthenticationManager authenticationManager,
                       CodigoVerificacaoService codigoService, GoogleTokenVerifier googleTokenVerifier) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.codigoService = codigoService;
        this.googleTokenVerifier = googleTokenVerifier;
    }

    public AuthResponse registrar(RegisterRequest req) {
        if (req.nomeCompleto() == null || req.nomeCompleto().isBlank()
                || req.email() == null || req.email().isBlank()
                || req.telefone() == null || req.telefone().isBlank()
                || req.senha() == null || req.senha().length() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Preencha nome, e-mail, telefone e uma senha de pelo menos 6 caracteres.");
        }
        String email = req.email().toLowerCase().trim();
        if (usuarioRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma conta com este e-mail.");
        }

        Usuario u = new Usuario();
        u.setNomeCompleto(req.nomeCompleto().trim());
        u.setEmail(email);
        u.setTelefone(req.telefone().trim());
        u.setSenhaHash(passwordEncoder.encode(req.senha()));
        u.setRole(Role.USER);
        u.setEmailVerificado(false); // confirma por código depois (não bloqueia o login)
        u = usuarioRepository.save(u);

        // Dispara o código de verificação; se o envio falhar, não derruba o cadastro.
        try {
            codigoService.gerarEEnviar(u, TipoCodigo.VERIFICACAO_EMAIL);
        } catch (Exception ignored) { }

        return new AuthResponse(jwtService.gerarToken(u), UsuarioDTO.de(u));
    }

    // Confirma o e-mail com o código recebido.
    public UsuarioDTO verificarEmail(String email, String codigo) {
        Usuario u = buscarPorEmail(email);
        if (u.isEmailVerificado()) return UsuarioDTO.de(u);
        codigoService.validar(u, TipoCodigo.VERIFICACAO_EMAIL, codigo);
        u.setEmailVerificado(true);
        return UsuarioDTO.de(usuarioRepository.save(u));
    }

    // Reenvia o código de verificação de e-mail.
    public void reenviarCodigoVerificacao(String email) {
        Usuario u = buscarPorEmail(email);
        if (u.isEmailVerificado()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seu e-mail já está verificado.");
        }
        codigoService.reenviar(u, TipoCodigo.VERIFICACAO_EMAIL);
    }

    // Troca DIRETA do e-mail — só quando a conta AINDA NÃO está verificada (errou no cadastro).
    // Conta já verificada deve usar o fluxo protegido (solicitarTrocaEmail/confirmarTrocaEmail).
    public AuthResponse trocarEmail(String email, String novoEmail) {
        Usuario u = buscarPorEmail(email);
        if (u.isEmailVerificado()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Seu e-mail já está verificado. Use a opção \"Alterar e-mail\" para trocá-lo com segurança.");
        }
        if (novoEmail == null || novoEmail.isBlank() || !novoEmail.contains("@")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe um e-mail válido.");
        }
        String alvo = novoEmail.toLowerCase().trim();
        if (alvo.equals(u.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este já é o seu e-mail atual.");
        }
        if (usuarioRepository.existsByEmail(alvo)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma conta com este e-mail.");
        }
        u.setEmail(alvo);
        u.setEmailVerificado(false);
        u = usuarioRepository.save(u);
        codigoService.gerarEEnviar(u, TipoCodigo.VERIFICACAO_EMAIL);
        return new AuthResponse(jwtService.gerarToken(u), UsuarioDTO.de(u));
    }

    // ===== Troca PROTEGIDA de e-mail (conta já verificada) =====

    // Passo 1: confirma a identidade (senha atual OU reautenticação Google), guarda o novo e-mail
    // como pendente, envia o código ao NOVO e-mail e avisa o e-mail ANTIGO. O e-mail atual segue valendo.
    public UsuarioDTO solicitarTrocaEmail(String email, String senhaAtual, String googleIdToken, String novoEmail) {
        Usuario u = buscarPorEmail(email);
        if (!u.isEmailVerificado()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Verifique seu e-mail atual antes de trocá-lo.");
        }
        // Confirmação de identidade
        if (u.getSenhaHash() != null) {
            if (senhaAtual == null || !passwordEncoder.matches(senhaAtual, u.getSenhaHash())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Senha atual incorreta.");
            }
        } else {
            // Conta Google (sem senha): reautenticar com o Google e conferir que é a mesma conta.
            GoogleIdToken.Payload p = googleTokenVerifier.verificar(googleIdToken);
            if (p == null || p.getEmail() == null || !p.getEmail().equalsIgnoreCase(u.getEmail())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Não foi possível confirmar sua identidade com o Google.");
            }
        }
        // Validação do novo e-mail
        if (novoEmail == null || novoEmail.isBlank() || !novoEmail.contains("@")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe um e-mail válido.");
        }
        String alvo = novoEmail.toLowerCase().trim();
        if (alvo.equals(u.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este já é o seu e-mail atual.");
        }
        if (usuarioRepository.existsByEmail(alvo)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma conta com este e-mail.");
        }

        u.setEmailPendente(alvo);
        usuarioRepository.save(u);
        codigoService.gerarEEnviar(u, TipoCodigo.VERIFICACAO_EMAIL, alvo);   // código vai ao NOVO e-mail
        codigoService.notificarTrocaEmailSolicitada(u.getEmail(), alvo);     // aviso ao e-mail ANTIGO
        return UsuarioDTO.de(u);
    }

    // Passo 2: valida o código (recebido no novo e-mail) e efetiva a troca. Reemite o token.
    public AuthResponse confirmarTrocaEmail(String email, String codigo) {
        Usuario u = buscarPorEmail(email);
        if (u.getEmailPendente() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não há troca de e-mail em andamento.");
        }
        if (usuarioRepository.existsByEmail(u.getEmailPendente())) {
            u.setEmailPendente(null);
            usuarioRepository.save(u);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esse e-mail já foi registrado por outra conta.");
        }
        codigoService.validar(u, TipoCodigo.VERIFICACAO_EMAIL, codigo);
        u.setEmail(u.getEmailPendente());
        u.setEmailPendente(null);
        u.setEmailVerificado(true);
        u = usuarioRepository.save(u);
        return new AuthResponse(jwtService.gerarToken(u), UsuarioDTO.de(u));
    }

    public void reenviarCodigoTroca(String email) {
        Usuario u = buscarPorEmail(email);
        if (u.getEmailPendente() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não há troca de e-mail em andamento.");
        }
        codigoService.reenviar(u, TipoCodigo.VERIFICACAO_EMAIL, u.getEmailPendente());
    }

    public UsuarioDTO cancelarTrocaEmail(String email) {
        Usuario u = buscarPorEmail(email);
        u.setEmailPendente(null);
        return UsuarioDTO.de(usuarioRepository.save(u));
    }

    public AuthResponse login(LoginRequest req) {
        String email = req.email() == null ? "" : req.email().toLowerCase().trim();
        // Conta criada só via Google não tem senha — orienta a usar o botão do Google.
        usuarioRepository.findByEmail(email).ifPresent(u -> {
            if (u.getSenhaHash() == null) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Esta conta usa login com o Google. Entre pelo botão \"Entrar com Google\".");
            }
        });
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, req.senha()));
        } catch (AuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos.");
        }
        Usuario u = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("E-mail ou senha inválidos."));
        return new AuthResponse(jwtService.gerarToken(u), UsuarioDTO.de(u));
    }

    // Login/cadastro via Google. Verifica o ID token e vincula por e-mail (cria a conta se não existir).
    public AuthResponse loginComGoogle(String idToken) {
        GoogleIdToken.Payload payload = googleTokenVerifier.verificar(idToken);
        if (payload == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Não foi possível validar o login com o Google.");
        }
        String email = payload.getEmail() == null ? "" : payload.getEmail().toLowerCase().trim();
        if (email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "A conta Google não tem um e-mail válido.");
        }
        String googleId = payload.getSubject();
        String nome = (String) payload.get("name");

        Usuario u = usuarioRepository.findByEmail(email).orElse(null);
        if (u == null) {
            // Conta nova via Google: sem senha, e-mail já verificado pelo Google.
            u = new Usuario();
            u.setNomeCompleto(nome != null && !nome.isBlank() ? nome : email);
            u.setEmail(email);
            u.setGoogleId(googleId);
            u.setEmailVerificado(true);
            u.setRole(Role.USER);
            u = usuarioRepository.save(u);
        } else {
            // Conta existente (de senha ou Google): vincula o googleId e marca o e-mail como verificado.
            boolean alterou = false;
            if (u.getGoogleId() == null) { u.setGoogleId(googleId); alterou = true; }
            if (!u.isEmailVerificado()) { u.setEmailVerificado(true); alterou = true; }
            if (alterou) u = usuarioRepository.save(u);
        }
        return new AuthResponse(jwtService.gerarToken(u), UsuarioDTO.de(u));
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sessão inválida."));
    }

    // ===== Recuperação de senha (esqueci minha senha) =====

    // Sempre "sucesso" para o cliente (anti-enumeração): só dispara o código se a conta existir.
    public void solicitarResetSenha(String email) {
        if (email == null || email.isBlank()) return;
        usuarioRepository.findByEmail(email.toLowerCase().trim()).ifPresent(u -> {
            try {
                codigoService.reenviar(u, TipoCodigo.RESET_SENHA);
            } catch (Exception ignored) { /* cooldown/erro de envio não vaza nada */ }
        });
    }

    // Valida o código de reset e grava a nova senha.
    public void resetarSenha(String email, String codigo, String novaSenha) {
        if (novaSenha == null || novaSenha.length() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A nova senha deve ter pelo menos 6 caracteres.");
        }
        Usuario u = usuarioRepository.findByEmail(email == null ? "" : email.toLowerCase().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Código inválido ou expirado."));
        codigoService.validar(u, TipoCodigo.RESET_SENHA, codigo);
        u.setSenhaHash(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(u);
    }

    public UsuarioDTO atualizarPerfil(String email, UpdateMeRequest req) {
        Usuario u = buscarPorEmail(email);
        if (req.nomeCompleto() != null && !req.nomeCompleto().isBlank()) u.setNomeCompleto(req.nomeCompleto().trim());
        if (req.telefone() != null && !req.telefone().isBlank()) u.setTelefone(req.telefone().trim());
        if (req.receberEmails() != null) u.setReceberEmails(req.receberEmails());
        if (req.cidade() != null) u.setCidade(req.cidade().trim());
        if (req.estado() != null) u.setEstado(req.estado().trim());
        if (req.logradouro() != null) u.setLogradouro(req.logradouro().trim());
        if (req.numero() != null) u.setNumero(req.numero().trim());
        if (req.complemento() != null) u.setComplemento(req.complemento().trim());
        if (req.bairro() != null) u.setBairro(req.bairro().trim());
        if (req.cep() != null) u.setCep(req.cep().trim());
        return UsuarioDTO.de(usuarioRepository.save(u));
    }
}
