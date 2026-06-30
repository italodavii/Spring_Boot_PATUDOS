package com.example.estudo_patudos_api_spring_jpa.dto.auth;

// Passo 1 da troca protegida de e-mail. Confirmação de identidade: `senhaAtual` (contas de senha)
// OU `googleIdToken` (contas Google, reautenticação). `novoEmail` é o destino desejado.
public record SolicitarTrocaEmailRequest(String senhaAtual, String googleIdToken, String novoEmail) {}
