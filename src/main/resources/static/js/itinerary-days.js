// Nessuna variabile globale per l'indice: usiamo il numero di card esistenti

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

        <div class="mb-3">
            <label class="form-label"></label>
            <input type="hidden"
                   class="form-control day-number-input"
                   name="days[${index}].dayNumber"
                   value="${index + 1}"
                   required>
        </div>

        <div class="text-end">
            <button type="button" class="btn btn-danger btn-sm" onclick="removeDay(this)">
            Rimuovi giorno
            </button>
        </div>
    `;

    container.appendChild(dayDiv);
}

/**
 * Rimuove la card del giorno e poi reindicizza tutti i giorni rimasti.
 */
function removeDay(buttonElement) {
    const card = buttonElement.closest(".day-card");
    if (card) {
        card.remove();
        reindexDays();
    }
}

/**
 * Reindicizza tutte le card dei giorni:
 * - aggiorna il titolo "Giorno X"
 * - aggiorna i name: days[i].title, days[i].description, days[i].dayNumber
 * - aggiorna il valore di dayNumber (1-based)
 */
function reindexDays() {
    const container = document.getElementById("days-container");
    const cards = container.querySelectorAll(".day-card");

    cards.forEach((card, index) => {
        // Titolo
        const label = card.querySelector(".day-title-label");
        if (label) {
            label.textContent = `Giorno ${index + 1}`;
        }

        // Input titolo
        const titleInput = card.querySelector(".day-title-input");
        if (titleInput) {
            titleInput.setAttribute("name", `days[${index}].title`);
        }

        // Textarea descrizione
        const descriptionInput = card.querySelector(".day-description-input");
        if (descriptionInput) {
            descriptionInput.setAttribute("name", `days[${index}].description`);
        }

        // Input numero giorno
        const numberInput = card.querySelector(".day-number-input");
        if (numberInput) {
            numberInput.setAttribute("name", `days[${index}].dayNumber`);
            numberInput.value = index + 1;
        }
    });
}
