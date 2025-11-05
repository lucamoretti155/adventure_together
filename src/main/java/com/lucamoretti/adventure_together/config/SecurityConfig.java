package com.lucamoretti.adventure_together.config;

import com.lucamoretti.adventure_together.service.customUserDetails.impl.CustomUserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// ARRICCHIRE COMMENTI COME NEGLI ALTRI PROGETTI

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Abilita @PreAuthorize
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsServiceImpl userDetailsService;

    // 1. BEAN: PasswordEncoder (Necessario per l'hashing)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. BEAN: AuthenticationManager (Necessario da iniettare nell'AuthController)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // 3. CONFIGURAZIONE PRINCIPALE: Definisce le regole di accesso agli URL
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Disabilita CSRF per API REST
                .authorizeHttpRequests(auth -> auth
                        // 1. REGOLA PIÙ SPECIFICA: Accesso solo per ADMIN
                        // Richiede il ruolo ROLE_ADMIN
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // 2. REGOLA MEDIA: Accesso per ADMIN o PLANNER
                        // Richiede il ruolo ROLE_ADMIN OPPURE ROLE_PLANNER
                        .requestMatchers("/planner/**").hasAnyRole("ADMIN", "PLANNER")

                        // 3. REGOLA GENERICA DI AUTENTICAZIONE: Accessibile a qualsiasi utente loggato
                        // (tutti i ruoli)

                        .requestMatchers("/traveler/**").authenticated()

                        // 4. REGOLA PUBBLICA: Accesso per chiunque
                        // PAGINA PUBBLICA
                        .requestMatchers("/", "/home", "/auth/**", "/public", "/trip/**").permitAll()

                        // STATICI
                        .requestMatchers("/js/**", "/css/**", "/images/**").permitAll()
                        // Endpoint AJAX pubblici
                        //TO DO: aggiungere endpoint ajax pubblici se necessari

                        // 5. CATCH-ALL: Qualsiasi altra richiesta che non è stata mappata, deve essere autenticata
                        .anyRequest().authenticated()

                )
                // Collega il servizio per il recupero utente
                .userDetailsService(userDetailsService)

                // Per test rapidi o configurazione base, abilito il login HTTP base
                .httpBasic(Customizer.withDefaults())

                .formLogin(form -> form.loginPage("/auth/login") // L'URL per mostrare il form
                        .loginProcessingUrl("/auth/authenticate") // L'URL dove viene inviato il POST
                        .defaultSuccessUrl("/home", true) // **IL REDIRECT LATO SERVER**
                        .failureUrl("/auth/login?error") // Pagina in caso di fallimento
                        .permitAll());
        return http.build();
    }
}
