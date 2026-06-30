package com.example.estudo_patudos_api_spring_jpa.config;

import com.example.estudo_patudos_api_spring_jpa.model.Role;
import com.example.estudo_patudos_api_spring_jpa.model.Usuario;
import com.example.estudo_patudos_api_spring_jpa.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// Cria um ADMIN inicial se ainda nao existir. Credenciais vem do ambiente
// (ADMIN_EMAIL / ADMIN_PASSWORD) com defaults so para dev local.
@Component
public class AdminSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;
    private final String adminNome;

    public AdminSeeder(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       @Value("${admin.email:admin@patudos.org}") String adminEmail,
                       @Value("${admin.password:patudos123}") String adminPassword,
                       @Value("${admin.nome:Administrador Patudos}") String adminNome) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
        this.adminNome = adminNome;
    }

    @Override
    public void run(String... args) {
        String email = adminEmail.toLowerCase().trim();
        if (usuarioRepository.existsByEmail(email)) return;

        Usuario admin = new Usuario();
        admin.setNomeCompleto(adminNome);
        admin.setEmail(email);
        admin.setTelefone("");
        admin.setSenhaHash(passwordEncoder.encode(adminPassword));
        admin.setRole(Role.ADMIN);
        admin.setEmailVerificado(true); // admin não precisa confirmar e-mail
        usuarioRepository.save(admin);
        System.out.println("[AdminSeeder] ADMIN criado: " + email);
    }
}
