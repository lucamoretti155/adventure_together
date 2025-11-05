package com.lucamoretti.adventure_together.model.payment;

import com.lucamoretti.adventure_together.model.booking.Booking;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/* Rappresenta un pagamento associato a una prenotazione
 Contiene informazioni sulla data del pagamento, l'importo pagato e l'importo dell'assicurazione
 Ogni pagamento è associato a una singola prenotazione tramite una relazione one-to-one
 La data del pagamento viene impostata automaticamente alla data corrente alla creazione dell'istanza
 L'importo dell'assicurazione rappresenta il costo dell'assicurazione associata alla prenotazione
 Viene creato automaticamente al momento della conferma della prenotazione
*/

@Data @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "payments")
public class Payment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // La data del pagamento viene impostata automaticamente alla data corrente alla creazione dell'istanza
    @Column(nullable=false)
    private LocalDate paymentDate = LocalDate.now();

    // L'importo totale pagato per la prenotazione
    @Column(nullable=false)
    private double amountPaid;

    // L'importo dell'assicurazione associata alla prenotazione (il di cui del costo totale)
    @Column(nullable=false)
    private double amountInsurance;

    // Associazione OneToOne con Booking
    // Ogni pagamento è associato a una singola prenotazione
    // Il pagamento non può esistere senza una prenotazione associata (optional = false)
    // Il proprietario della relazione è Payment, quindi la colonna foreign key sarà in questa tabella
    @OneToOne(optional = false)
    @JoinColumn(name = "booking_id", unique = true)
    private Booking booking;
}

