package com.puc.security;

import com.puc.model.UsuarioSistema;
import com.puc.repository.UsuarioSistemaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UsuarioSistemaRepository usuarioSistemaRepository) {
        return username -> usuarioSistemaRepository.findByUsername(username)
                .map(usuario -> User.builder()
                        .username(usuario.getUsername())
                        .password(usuario.getSenha())
                        .roles(usuario.getPerfil())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }

    @Bean
    public CommandLineRunner inicializaUsuariosSistema(
            UsuarioSistemaRepository usuarioSistemaRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            if (!usuarioSistemaRepository.existsByUsername("admin")) {
                usuarioSistemaRepository.save(new UsuarioSistema("admin",
                        passwordEncoder.encode("admin123"), "ADMIN"));
            }
            if (!usuarioSistemaRepository.existsByUsername("consulta")) {
                usuarioSistemaRepository.save(new UsuarioSistema("consulta",
                        passwordEncoder.encode("consulta123"), "CONSULTA"));
            }
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/livros", "/usuarios-sistema").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/livros/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/**").hasAnyRole("ADMIN", "CONSULTA")
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}
