package com.example.estudo_patudos_api_spring_jpa.controller;

import com.example.estudo_patudos_api_spring_jpa.dto.auth.*;
import com.example.estudo_patudos_api_spring_jpa.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    // Login social: recebe o ID token do Google, valida e devolve o nosso JWT.
    @PostMapping("/google")
    public ResponseEntity<AuthResponse> google(@RequestBody GoogleLoginRequest req) {
        return ResponseEntity.ok(authService.loginComGoogle(req.idToken()));
    }

    // Validacao do token na carga do front. Token invalido/expirado -> 401 (entry point).
    @GetMapping("/me")
    public ResponseEntity<UsuarioDTO> me(Authentication auth) {
        return ResponseEntity.ok(UsuarioDTO.de(authService.buscarPorEmail(auth.getName())));
    }

    @PatchMapping("/me")
    public ResponseEntity<UsuarioDTO> atualizarMe(Authentication auth, @RequestBody UpdateMeRequest req) {
        return ResponseEntity.ok(authService.atualizarPerfil(auth.getName(), req));
    }

    // ===== Recuperação de senha =====

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody ForgotPasswordRequest req) {
        authService.solicitarResetSenha(req.email());
        return ResponseEntity.noContent().build(); // sempre 204 (anti-enumeração)
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequest req) {
        authService.resetarSenha(req.email(), req.codigo(), req.novaSenha());
        return ResponseEntity.noContent().build();
    }

    // ===== Verificação de e-mail =====

    @PostMapping("/verificar-email")
    public ResponseEntity<UsuarioDTO> verificarEmail(Authentication auth, @RequestBody VerificarEmailRequest req) {
        return ResponseEntity.ok(authService.verificarEmail(auth.getName(), req.codigo()));
    }

    @PostMapping("/reenviar-codigo")
    public ResponseEntity<Void> reenviarCodigo(Authentication auth) {
        authService.reenviarCodigoVerificacao(auth.getName());
        return ResponseEntity.noContent().build();
    }

    // Retorna um NOVO token (o e-mail é o subject do JWT; o token antigo deixa de valer).
    // Troca DIRETA — só para e-mail ainda não verificado.
    @PatchMapping("/email")
    public ResponseEntity<AuthResponse> trocarEmail(Authentication auth, @RequestBody TrocarEmailRequest req) {
        return ResponseEntity.ok(authService.trocarEmail(auth.getName(), req.novoEmail()));
    }

    // ===== Troca PROTEGIDA de e-mail (conta verificada) =====

    @PostMapping("/email/solicitar")
    public ResponseEntity<UsuarioDTO> solicitarTrocaEmail(Authentication auth, @RequestBody SolicitarTrocaEmailRequest req) {
        return ResponseEntity.ok(authService.solicitarTrocaEmail(auth.getName(), req.senhaAtual(), req.googleIdToken(), req.novoEmail()));
    }

    @PostMapping("/email/confirmar")
    public ResponseEntity<AuthResponse> confirmarTrocaEmail(Authentication auth, @RequestBody VerificarEmailRequest req) {
        return ResponseEntity.ok(authService.confirmarTrocaEmail(auth.getName(), req.codigo()));
    }

    @PostMapping("/email/reenviar")
    public ResponseEntity<Void> reenviarCodigoTroca(Authentication auth) {
        authService.reenviarCodigoTroca(auth.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/email/cancelar")
    public ResponseEntity<UsuarioDTO> cancelarTrocaEmail(Authentication auth) {
        return ResponseEntity.ok(authService.cancelarTrocaEmail(auth.getName()));
    }
}
