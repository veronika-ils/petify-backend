package com.petify.petify.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;


import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //@Bean
    //public PasswordEncoder passwordEncoder() {
     //   return new BCryptPasswordEncoder();
   // }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - no authentication required
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/users/*/pets").permitAll()

                        // Public listings endpoints
                        .requestMatchers(HttpMethod.GET, "/api/listings/active").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/listings/*").permitAll()

                        // Protected listings endpoints
                        .requestMatchers(HttpMethod.GET, "/api/listings/my-listings").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/listings").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/api/listings/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/listings/*").authenticated()

                        // Protected user endpoints
                        .requestMatchers(HttpMethod.GET, "/api/users/*").authenticated()

                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .formLogin(Customizer.withDefaults());

        return http.build();
    }
}
