package com.example.estudo_patudos_api_spring_jpa.security;

import com.example.estudo_patudos_api_spring_jpa.model.Usuario;
import com.example.estudo_patudos_api_spring_jpa.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario u = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        // Contas Google não têm senha (senhaHash null). O Spring User exige senha não-nula,
        // então usamos um placeholder que NUNCA casa no BCrypt (login por senha sempre falha).
        String senha = u.getSenhaHash() != null ? u.getSenhaHash() : "GOOGLE_ACCOUNT_NO_PASSWORD";

        // Authority "ROLE_ADMIN"/"ROLE_USER" para casar com hasRole(...) na config.
        return new User(
                u.getEmail(),
                senha,
                List.of(new SimpleGrantedAuthority("ROLE_" + u.getRole().name()))
        );
    }
}
