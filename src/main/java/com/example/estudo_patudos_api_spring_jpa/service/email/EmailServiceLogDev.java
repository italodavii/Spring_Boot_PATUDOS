package com.example.estudo_patudos_api_spring_jpa.service.email;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

// Impl de dev: NÃO envia e-mail de verdade, só imprime no console (o código fica visível no log).
// Ativa quando app.email.enabled=false (default) — permite testar os fluxos sem credencial SMTP.
@Service
@ConditionalOnProperty(name = "app.email.enabled", havingValue = "false", matchIfMissing = true)
public class EmailServiceLogDev implements EmailService {

    @Override
    public void enviar(String destino, String assunto, String corpo) {
        System.out.println("================ [EMAIL DEV] ================");
        System.out.println("Para:     " + destino);
        System.out.println("Assunto:  " + assunto);
        System.out.println(corpo);
        System.out.println("============================================");
    }
}
