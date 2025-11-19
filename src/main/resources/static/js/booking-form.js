const participantTemplate = `
    <div class="participant-row border rounded p-3 mb-3">
        <div class="row">
            <div class="col-md-5 mb-2">
                <label class="form-label">Nome</label>
                <input type="text" class="form-control participant-firstname">
            </div>

            <div class="col-md-5 mb-2">
                <label class="form-label">Cognome</label>
                <input type="text" class="form-control participant-lastname">
            </div>

            <div class="col-md-2 d-flex align-items-end">
                <button type="button"
                        class="btn btn-danger btn-sm w-100 remove-participant">
                    Rimuovi
                </button>
            </div>
        </div>

        <div class="mt-2">
            <label class="form-label">Data di nascita</label>
            <input type="date" class="form-control participant-dob">
        </div>
    </div>
`;

function addParticipant() {
    const container = document.getElementById("participants-container");
    const wrapper = document.createElement("div");
    wrapper.insertAdjacentHTML("beforeend", participantTemplate);
    const row = wrapper.firstElementChild;

    container.appendChild(row);
    renumberParticipants();
}

function removeParticipant(button) {
    const row = button.closest(".participant-row");
    if (row) {
        row.remove();
        renumberParticipants();
    }
}

function renumberParticipants() {
    const rows = document.querySelectorAll("#participants-container .participant-row");

    rows.forEach((row, i) => {
        const firstNameInput = row.querySelector(".participant-firstname");
        const lastNameInput = row.querySelector(".participant-lastname");
        const dobInput = row.querySelector(".participant-dob");
        const removeBtn = row.querySelector(".remove-participant");

        if (firstNameInput) {
            firstNameInput.setAttribute("name", `participants[${i}].firstName`);
        }
        if (lastNameInput) {
            lastNameInput.setAttribute("name", `participants[${i}].lastName`);
        }
        if (dobInput) {
            dobInput.setAttribute("name", `participants[${i}].dateOfBirth`);
        }
        if (removeBtn) {
            removeBtn.onclick = () => removeParticipant(removeBtn);
        }
    });
}

document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(".remove-participant").forEach(btn => {
        btn.onclick = () => removeParticipant(btn);
    });

    const addBtn = document.getElementById("add-participant-btn");
    if (addBtn) {
        addBtn.onclick = addParticipant;
    }

    renumberParticipants();
});
