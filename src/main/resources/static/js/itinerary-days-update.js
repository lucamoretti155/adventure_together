/**
 * Script per la gestione dinamica dei giorni in fase di UPDATE dell'itinerario.
 * - Supporta giorni già esistenti caricati dal server
 * - Permette di aggiungere e rimuovere giorni
 * - Mantiene la re-indicizzazione corretta
 */

function addDay() {
    const container = document.getElementById("days-container");
    const index = container.querySelectorAll(".day-card").length; // indice successivo

    const dayDiv = document.createElement("div");
    dayDiv.classList.add("card", "mb-3", "p-3", "shadow-sm", "day-card");

    dayDiv.innerHTML = `
        <h5 class="day-title-label">Giorno ${index + 1}</h5>

        <div class="mb-3">
            <label class="form-label">Titolo</label>
            <input type="text"
                   class="form-control day-title-input"
                   name="days[${index}].title"
                   required>
        </div>

        <div class="mb-3">
            <label class="form-label">Descrizione</label>
            <textarea class="form-control day-description-input"
                      name="days[${index}].description"
                      rows="3"
                      required></textarea>
        </div>

        <input type="hidden"
               class="day-number-input"
               name="days[${index}].dayNumber"
               value="${index + 1}">

        <div class="text-end">
            <button type="button" class="btn btn-danger btn-sm" onclick="removeDay(this)">
                Rimuovi giorno
            </button>
        </div>
    `;

    container.appendChild(dayDiv);
}

/**
 * Rimuove una card di giorno e poi reindicizza tutti i giorni rimasti.
 */
function removeDay(buttonElement) {
    const card = buttonElement.closest(".day-card");
    if (card) {
        card.remove();
        reindexDays();
    }
}

/**
 * Reindicizza tutte le card:
 * - Aggiorna il titolo "Giorno X"
 * - Aggiorna gli attributi name dei campi
 * - Aggiorna dayNumber
 */
function reindexDays() {
    const container = document.getElementById("days-container");
    const cards = container.querySelectorAll(".day-card");

    cards.forEach((card, index) => {

        // Titolo Giorno X
        const label = card.querySelector(".day-title-label");
        if (label) {
            label.textContent = `Giorno ${index + 1}`;
        }

        // Titolo del giorno
        const titleInput = card.querySelector(".day-title-input");
        if (titleInput) {
            titleInput.setAttribute("name", `days[${index}].title`);
        }

        // Descrizione
        const descriptionInput = card.querySelector(".day-description-input");
        if (descriptionInput) {
            descriptionInput.setAttribute("name", `days[${index}].description`);
        }

        // Numero giorno
        const numberInput = card.querySelector(".day-number-input");
        if (numberInput) {
            numberInput.setAttribute("name", `days[${index}].dayNumber`);
            numberInput.value = index + 1;
        }
    });
}

