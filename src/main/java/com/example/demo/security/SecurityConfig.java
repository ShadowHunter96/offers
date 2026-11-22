package com.example.demo.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security of the REST API.
 * <ul>
 *     <li>HTTP Basic authentication, stateless (no session, no CSRF token - the API is not used from browser forms).</li>
 *     <li>Role VIEWER may only read (GET), role MANAGER may also create, change and delete data
 *     and perform offer state transitions.</li>
 *     <li>OpenAPI documentation and Swagger UI stay public.</li>
 *     <li>401 and 403 responses use the same ApiErrorResponse structure as other API errors.</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    public static final String ROLE_VIEWER = "VIEWER";
    public static final String ROLE_MANAGER = "MANAGER";

    @Bean
    public SecurityFilterChain apiSecurity(HttpSecurity http, ApiSecurityErrorHandler errorHandler) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v3/api-docs", "/v3/api-docs/**", "/v3/api-docs.yaml",
                                "/swagger-ui/**", "/swagger-ui.html", "/error").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/**").hasAnyRole(ROLE_VIEWER, ROLE_MANAGER)
                        .requestMatchers("/api/**").hasRole(ROLE_MANAGER)
                        .anyRequest().authenticated())
                .httpBasic(basic -> basic.authenticationEntryPoint(errorHandler))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(errorHandler)
                        .accessDeniedHandler(errorHandler));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // bcrypt by default; stored hashes carry their algorithm prefix ({bcrypt}...)
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public UserDetailsService users(PasswordEncoder encoder,
                                    @Value("${app.security.viewer.username}") String viewerName,
                                    @Value("${app.security.viewer.password}") String viewerPassword,
                                    @Value("${app.security.manager.username}") String managerName,
                                    @Value("${app.security.manager.password}") String managerPassword) {
        return new InMemoryUserDetailsManager(
                User.withUsername(viewerName).password(encoder.encode(viewerPassword)).roles(ROLE_VIEWER).build(),
                User.withUsername(managerName).password(encoder.encode(managerPassword)).roles(ROLE_MANAGER).build());
    }
}
