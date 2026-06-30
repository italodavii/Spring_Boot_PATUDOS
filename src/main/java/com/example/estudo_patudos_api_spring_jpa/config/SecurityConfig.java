package com.example.estudo_patudos_api_spring_jpa.config;

import com.example.estudo_patudos_api_spring_jpa.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          CorsConfigurationSource corsConfigurationSource) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Não autenticado / token inválido/expirado -> 401 (pro front limpar o localStorage).
            // Autenticado porém sem permissão (ex.: USER em endpoint ADMIN) -> 403.
            // setStatus (e não sendError) evita o forward interno pro /error, que re-entra
            // no filtro como anônimo e mascararia o 403 como 401.
            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint((req, res, e) -> res.setStatus(HttpServletResponse.SC_UNAUTHORIZED))
                    .accessDeniedHandler((req, res, e) -> res.setStatus(HttpServletResponse.SC_FORBIDDEN)))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // /error: o dispatch interno de erro (ex.: ResponseStatusException 404 vinda de
                // um controller) re-entra na cadeia como anônimo — sem liberar, o JwtAuthenticationFilter
                // não roda no error-dispatch e o status real (404/400/...) seria mascarado como 401.
                .requestMatchers("/error").permitAll()
                // Auth público (login/cadastro/recuperação de senha/login Google)
                .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login", "/api/auth/google",
                        "/api/auth/forgot-password", "/api/auth/reset-password").permitAll()
                // Verificação de e-mail / troca de e-mail: usuário logado (já cairia no anyRequest,
                // explícito para documentar). forgot/reset/google (públicos) entram nas Fases B/C.
                .requestMatchers(HttpMethod.POST, "/api/auth/verificar-email", "/api/auth/reenviar-codigo").authenticated()
                .requestMatchers(HttpMethod.PATCH, "/api/auth/email").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/auth/email/**").authenticated()
                // Estatísticas: público vs dashboard (admin)
                .requestMatchers(HttpMethod.GET, "/api/estatisticas/publico").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/estatisticas/dashboard").hasRole("ADMIN")
                // Catálogo/Home: leituras públicas
                .requestMatchers(HttpMethod.GET, "/api/pets", "/api/pets/**",
                        "/api/eventos", "/api/eventos/**").permitAll()
                // Mutações de pets/eventos: só ADMIN
                .requestMatchers(HttpMethod.POST, "/api/pets/**", "/api/eventos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/pets/**", "/api/eventos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/pets/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/pets/**", "/api/eventos/**").hasRole("ADMIN")
                // Favoritos: qualquer usuário logado (USER ou ADMIN)
                .requestMatchers("/api/favoritos/**").authenticated()
                // Intenções de adoção: USER logado envia e vê as próprias; só ADMIN lista todas/conta/aprova/recusa.
                .requestMatchers(HttpMethod.POST, "/api/intencoes").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/intencoes/minhas").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/intencoes", "/api/intencoes/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/intencoes/**").hasRole("ADMIN")
                // Qualquer outra coisa (ex.: /api/auth/me) exige estar autenticado
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
