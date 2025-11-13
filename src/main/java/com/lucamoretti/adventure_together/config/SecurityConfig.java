package com.lucamoretti.adventure_together.config;

import com.lucamoretti.adventure_together.service.customUserDetails.impl.CustomUserDetailsServiceImpl;
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

/*

   Configurazione di sicurezza per l'applicazione Adventure Together.
   Definisce le regole di accesso agli URL, i metodi di autenticazione e il servizio per il recupero degli utenti.

   Sfrutta Spring Security per proteggere le risorse dell'applicazione.
   Usa BCrypt per l'hashing delle password.
   Usa l'autenticazione HTTP Basic e form-based login.
   Usa la classe CustomUserDetailsServiceImpl per il recupero degli utenti dal database

   Gli URL sono protetti in base ai ruoli (securityFilterChain): (ADMIN, PLANNER, TRAVELER, UTENTE NON AUTENTICATO)
   Così facendo, garantisce che solo gli utenti autorizzati possano accedere alle funzionalità sensibili dell'applicazione
   evitando di effettuare controlli di sicurezza a livello di controller, che sono meno efficienti e più soggetti a errori.

   Infine viene settato il login form personalizzato per l'autenticazione degli utenti.

*/

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Abilita @PreAuthorize
public class SecurityConfig {

    // Servizio per il recupero degli utenti dal database
    private final CustomUserDetailsServiceImpl userDetailsService;

    public SecurityConfig(CustomUserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

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
                        http
                        // Disabilita CSRF solo per gli endpoint REST API
                        .csrf(csrf -> csrf
                                .ignoringRequestMatchers("/api/**")) // disabilita solo per endpoint REST

                        // Abilita il "ricordami" per mantenere la sessione attiva
                        .rememberMe(remember -> remember
                            .key("uniqueAndSecret")
                            .tokenValiditySeconds(7 * 24 * 60 * 60) // 7 giorni
                        )

                        // Definisce le regole di autorizzazione per gli URL
                        .authorizeHttpRequests(auth -> auth
                        // 1. REGOLA PIÙ SPECIFICA: Accesso solo per ADMIN
                        // Richiede il ruolo ROLE_ADMIN
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // 2. REGOLA MEDIA: Accesso per ADMIN o PLANNER
                        // Richiede il ruolo ROLE_ADMIN oppure ROLE_PLANNER
                        .requestMatchers("/planner/**").hasAnyRole("ADMIN", "PLANNER")

                        // 3. REGOLA GENERICA: Accessibile a qualsiasi utente registrato
                        // (tutti i ruoli)

                        .requestMatchers("/traveler/**").authenticated()

                        // 4. REGOLA PUBBLICA: Accesso per chiunque
                        // PAGINE PUBBLICHE
                        .requestMatchers("/", "/home/**", "/auth/**", "/public", "/trips/**", "/search/**", "/error/**").permitAll()

                        // STATICI
                        .requestMatchers("/js/**", "/css/**", "/images/**").permitAll()
                        // Endpoint AJAX pubblici
                        //TO DO: aggiungere endpoint ajax pubblici se necessari


                        // 5. CATCH-ALL: Qualsiasi altra richiesta che non è stata mappata, è necessaria almeno l'autenticazione
                        .anyRequest().authenticated()


                )
                // Collega il servizio per il recupero utente
                .userDetailsService(userDetailsService)

                // Abilita l'autenticazione HTTP Basic
                .httpBasic(Customizer.withDefaults())

                // Configurazione del form di login
                .formLogin(form -> form.loginPage("/auth/login") // L'URL per mostrare il form
                        .loginProcessingUrl("/auth/authenticate") // L'URL dove viene inviato il POST
                        .defaultSuccessUrl("/home", true) // il redirect dopo il login
                        .failureUrl("/auth/login?error") // pagina in caso di fallimento
                        .permitAll())

                // Configurazione del logout
                .logout(logout -> logout
                        .logoutUrl("/auth/logout") // L'URL per effettuare il logout
                        .logoutSuccessUrl("/auth/login?logout") // pagina dopo il logout
                        .invalidateHttpSession(true) // Invalida la sessione
                        .deleteCookies("JSESSIONID") // Cancella i cookie di sessione
                        .permitAll() // Permette in ogni caso a tutti di accedere al logout
                );


        return http.build();
    }
}
