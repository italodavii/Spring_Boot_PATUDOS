package com.example.estudo_patudos_api_spring_jpa.service.email;

// Abstração de envio de e-mail. Em dev usa-se a impl que loga no console (sem SMTP);
// em produção a impl SMTP (Resend/SendGrid). Seleção por @ConditionalOnProperty.
public interface EmailService {
    void enviar(String destino, String assunto, String corpo);
}
