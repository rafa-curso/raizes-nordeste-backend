package br.com.raizesnordeste.gestao.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuração de segurança da aplicação com Spring Security 6.
 * - Autenticação Stateless via JWT.
 * - CORS configurado por variável de ambiente (APP_CORS_ORIGINS).
 * - H2 Console desabilitado por padrão; habilitado apenas no perfil "dev".
 *
 * Autor: Rafael Inácio da Silva (RU: 4736098)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Value("${app.cors.allowed-origins:}")
    private String allowedOrigins;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Endpoints Públicos (Documentação OpenAPI, Swagger e H2 Console)
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/h2-console/**",
                    "/favicon.ico"
                ).permitAll()

                // Endpoints Públicos de Autenticação e Cadastro
                .requestMatchers("/api/v1/auth/**").permitAll()

                // Consultas públicas ao catálogo de cardápio e unidades
                .requestMatchers(HttpMethod.GET, "/api/v1/cardapio/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/unidades/**").permitAll()

                // Gestão de Cardápio e Unidades (Apenas Admin Matriz)
                .requestMatchers(HttpMethod.POST, "/api/v1/cardapio/**").hasRole("ADMIN_MATRIZ")
                .requestMatchers(HttpMethod.PUT, "/api/v1/cardapio/**").hasRole("ADMIN_MATRIZ")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/cardapio/**").hasRole("ADMIN_MATRIZ")
                .requestMatchers(HttpMethod.POST, "/api/v1/unidades/**").hasRole("ADMIN_MATRIZ")

                // Gestão de Estoque (Admin Matriz e Gerente de Loja)
                .requestMatchers("/api/v1/estoque/**").hasAnyRole("ADMIN_MATRIZ", "GERENTE_LOJA")

                // Trilha de Auditoria (Apenas Admin Matriz)
                .requestMatchers("/api/v1/auditoria/**").hasRole("ADMIN_MATRIZ")

                // Pedidos e Pagamentos (Usuários autenticados)
                .requestMatchers("/api/v1/pedidos/**").authenticated()

                // Qualquer outra requisição requer autenticação
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // SEGURANÇA: Origens configuradas por variável de ambiente, sem curinga irrestrito.
        List<String> origins = allowedOrigins == null
                ? List.of()
                : allowedOrigins.lines()
                    .flatMap(line -> List.of(line.split(",")).stream())
                    .map(String::trim)
                    .filter(origin -> !origin.isBlank())
                    .toList();

        if (origins.stream().anyMatch(origin -> origin.equals("*") || origin.contains("*"))) {
            throw new IllegalStateException(
                    "APP_CORS_ORIGINS não pode conter curingas quando credenciais estão habilitadas.");
        }

        config.setAllowedOrigins(origins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
