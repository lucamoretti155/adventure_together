package com.lucamoretti.adventure_together.service.customUserDetails.impl;

import com.lucamoretti.adventure_together.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//RIVEDERE COLLEGAMENTI CON REPOSITORY USER

@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
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
