/** Clasa pentru Activity in care sunt definite variabilele, restrictiile, getter urile si setter urile
 * @author Dumitrescu Andrei Florentin
 * @version 10 Ianuarie 2025
 * */
package com.proieectAWJ.model;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Activity {
    private Long id;

    @NotBlank(message = "Numele nu poate fi gol")
    private String nume;

    @NotBlank(message = "Statusul nu poate fi gol")
    @Pattern(regexp = "^(In desfasurare|Finalizata|Suspendata)$", message = "Statusul trebuie să fie 'In desfasurare', 'Finalizata' sau 'Suspendata'")
    private String status;

    @NotNull(message = "Progresul nu poate fi nul")
    @Min(value = 0, message = "Progresul trebuie să fie cel puțin 0")
    @Max(value = 100, message = "Progresul trebuie să fie cel mult 100")
    private int progres;

    @NotNull(message = "Termenul Limita nu poate fi nul")
    @FutureOrPresent(message = "Deadline-ul trebuie să fie în viitor sau astăzi.")
    private LocalDate termenLimita;

    // formattedDeadLine e folosit pentru a afisa in formatul dd-mm-yyy
    private String termenLimitaFormatat;

    public Activity() {}

    public Activity(Long id, String name, String status, int progress, LocalDate deadline, String formattedDeadline) {
        this.id = id;
        this.nume = name;
        this.status = status;
        this.progres = progress;
        this.termenLimita = deadline;
        this.termenLimitaFormatat = formattedDeadline;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String name) {
        this.nume = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getProgres() {
        return progres;
    }

    public void setProgres(int progress) {
        this.progres = progress;
    }

    public LocalDate getTermenLimita() { return termenLimita; }

    public void setTermenLimita(LocalDate termenLimita) {
        this.termenLimita = termenLimita;
        //atunci cand este initializat deadline, se initializeaza si formattedDeadline
        formatTermenLimita();
    }

    public String getTermenLimitaFormatat() {
        return termenLimitaFormatat;
    }

    // metoda pentru setarea formattedDeadline in formatul dd-mm-yyyy in functie de deadline
    private void formatTermenLimita() {
        if (this.termenLimita != null) {
            DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            this.termenLimitaFormatat = this.termenLimita.format(format);
        } else {
            this.termenLimitaFormatat = null;
        }
    }
}
