package com.example.estudo_patudos_api_spring_jpa.service.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

// Impl de produção: envia via SMTP (JavaMailSender). Funciona com qualquer provedor que
// forneça SMTP (Resend, SendGrid, Gmail). Ativa quando app.email.enabled=true.
@Service
@ConditionalOnProperty(name = "app.email.enabled", havingValue = "true")
public class EmailServiceSmtp implements EmailService {

    private final JavaMailSender mailSender;
    private final String remetente;

    public EmailServiceSmtp(JavaMailSender mailSender,
                            @Value("${app.from-email:nao-responder@patudosdarua.com.br}") String remetente) {
        this.mailSender = mailSender;
        this.remetente = remetente;
    }

    @Override
    public void enviar(String destino, String assunto, String corpo) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(remetente);
        msg.setTo(destino);
        msg.setSubject(assunto);
        msg.setText(corpo);
        mailSender.send(msg);
    }
}
