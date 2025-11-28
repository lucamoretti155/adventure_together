# Adventure Together – Travel Booking Web Application

**Descrizione generale:**  
Adventure Together è una web application sviluppata in Java Spring Boot come progetto finale per il corso di Programmazione Web (A.A. 2024/2025 – Politecnico di Milano). L’applicazione gestisce itinerari di viaggio, viaggi programmati, prenotazioni e utenti con ruoli diversi (Traveler, Planner, Admin), seguendo un’architettura a livelli (Controller, DTO, Service, Repository) e adottando design pattern come State, Decorator e Observer per modellare la logica di dominio.

**Test e copertura:**  
Il progetto include test unitari sviluppati con JUnit 5 e Mockito. La copertura del codice è misurata con JaCoCo, generabile tramite `mvn test`; il relativo report è disponibile in `target/site/jacoco`.

**Struttura del progetto:**  
L’organizzazione segue le convenzioni Spring Boot: `src/main/java` contiene il codice sorgente, `src/main/resources` template e configurazioni, `src/test/java` i test unitari e `target/` i file compilati e il JAR eseguibile.

**Documentazione:**  
La documentazione completa del progetto è fornita nel file **Documentazione_AdventureTogether_Luca_Moretti.pdf**, incluso nella consegna.
