package com.lucamoretti.adventure_together.util.passwordGenerator;

import org.springframework.stereotype.Service;
import java.security.SecureRandom;

/*
   Servizio per la generazione di password sicure randomiche. Utilizzato per creare password temporanee per nuovi utenti Planner.
   Le password generate rispettano i seguenti criteri:
   - Lunghezza compresa tra 8 e 20 caratteri
   - Almeno una lettera maiuscola
   - Almeno una lettera minuscola
   - Almeno una cifra
   - Almeno un carattere speciale (@$!%*?&)
 */

@Service
public class PasswordGeneratorService {

    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIALS = "@$!%*?&";
    private static final String ALL_CHARS = UPPERCASE + LOWERCASE + DIGITS + SPECIALS;

    private static final SecureRandom RANDOM = new SecureRandom();

    public String generateSecurePassword() {
        int length = 8 + RANDOM.nextInt(13); // lunghezza casuale tra 8 e 20

        StringBuilder password = new StringBuilder(length);

        // garantisce almeno un carattere per ogni categoria richiesta
        password.append(randomChar(UPPERCASE));
        password.append(randomChar(LOWERCASE));
        password.append(randomChar(DIGITS));
        password.append(randomChar(SPECIALS));

        // riempi il resto con caratteri casuali misti
        for (int i = 4; i < length; i++) {
            password.append(randomChar(ALL_CHARS));
        }

        // mescola i caratteri per evitare che i primi 4 siano prevedibili
        return shuffleString(password.toString());
    }

    private static char randomChar(String chars) {
        return chars.charAt(RANDOM.nextInt(chars.length()));
    }

    private static String shuffleString(String input) {
        char[] array = input.toCharArray();
        for (int i = array.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            char temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
        return new String(array);
    }
}