package com.lucamoretti.adventure_together.service.customUserDetails.impl;

import com.lucamoretti.adventure_together.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/*
    Implementazione del servizio per il caricamento dei dettagli utente richiesti da Spring Security.
    Utilizza il repository UserRepository per recuperare le informazioni dell'utente dal database.
 */

@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /*
     * Metodo richiesto da Spring Security durante il processo di login.
     */


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // Cerca l'utente nel database.
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Utente non trovato con username: " + email)
                );
    }
}
