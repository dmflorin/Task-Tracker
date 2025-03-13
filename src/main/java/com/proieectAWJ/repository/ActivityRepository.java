/** Clasa pentru ActivityRepository in care sunt toate functiile care gestioneaza stocarea si incarcarea activitatilor, tot odata avem functiile pentru delete si edit
 * @author Dumitrescu Andrei Florentin
 * @version 10 Ianuarie 2025
 * */
package com.proieectAWJ.repository;

import com.proieectAWJ.model.Activity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class ActivityRepository {
    private static final String caleFisier = "activities.json"; // fisierul unde sunt salvate activitatile
    private final List<Activity> activitati;
    private final AtomicLong idGenerator;

    public ActivityRepository() {
        this.activitati = incarcaActivitatiDinFisier();
        this.idGenerator = new AtomicLong(preiaIDMaxim());
    }
    // Incarca activitatile din Json
    private List<Activity> incarcaActivitatiDinFisier() {
        ObjectMapper objectMapper = creazaObjectMapperPersonalizat();
        File file = new File(caleFisier);
        if (file.exists()) {
            try {
                // citeste ficare intrare din json si o traduce intr un obiect TypeReference, in cazul asta o lista de activitati
                return objectMapper.readValue(file, new TypeReference<List<Activity>>() {});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }
    // Salveaza activitatile in Json
    private void salveazaActivitatiInFisier() {
        ObjectMapper objectMapper = creazaObjectMapperPersonalizat();
        try {
            objectMapper.writeValue(new File(caleFisier), activitati);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private ObjectMapper creazaObjectMapperPersonalizat() {
        // instanta object mapper care functioneaza ca un pod intre obiecte java si json
        // objectmapper este o clasa din biblioteca Jackson care se ocupa cu serializare si deserializare
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule()); // folosit pentru ca jackson sa gestioneze tipurile de date specifice timpului
        return objectMapper;
    }
    // Obtine ID-ul maxim dintre activitatile existente
    private long preiaIDMaxim() {
        return activitati.stream()
                .mapToLong(Activity::getId)
                .max()
                .orElse(0); // Daca nu exista activitati, incepe cu ID-ul 0
    }
    // Gasește toate activitatile
    public List<Activity> gasesteTot() {
        return new ArrayList<>(activitati);
    }
    // Gaseste activitatile urgente
    public List<Activity> gasesteActivitatiUrgente() {
        LocalDate today = LocalDate.now(); // Data curenta

        return activitati.stream()
                .filter(activity -> activity.getTermenLimita() != null &&
                        ChronoUnit.DAYS.between(today, activity.getTermenLimita()) <= 1)
                .collect(Collectors.toList());
    }
    // Salveaza o activitate la creare
    public Activity salveaza(Activity activitate) {
        if (activitate.getId() == null) {
            activitate.setId(idGenerator.incrementAndGet());
        }
        activitati.add(activitate);
        salveazaActivitatiInFisier(); // Salveaza activitatile in fisier dupa fiecare adaugare
        return activitate;
    }
    // Salveaza o activitate dupa editare
    public Activity salveazaEditare(Activity activitate) {
        if (activitate.getId() == null) {
            activitate.setId(idGenerator.incrementAndGet());
        }
        salveazaActivitatiInFisier(); // Salveaza activitatile in fisier dupa fiecare editare
        return activitate;
    }
    // Șterge o activitate după ID
    public void stergeInFunctieDeID(Long id) {
        activitati.removeIf(activity -> activity.getId().equals(id));
        salveazaActivitatiInFisier(); // Salveaza activitatile in fisier dupa fiecare stergere
    }
}
