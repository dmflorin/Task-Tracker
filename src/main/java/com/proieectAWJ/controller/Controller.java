/** Clasa pentru Controller ul aplicatiei care gestioneaza metodele de tip get si post
 * @author Dumitrescu Andrei Florentin
 * @version 10 Ianuarie 2025
 * */
package com.proieectAWJ.controller;

import com.proieectAWJ.model.Activity;
import com.proieectAWJ.repository.ActivityRepository;
import jakarta.validation.Valid;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@org.springframework.stereotype.Controller
public class Controller {
    private final ActivityRepository activitatiRepository; //ofera acces la datele activitatilor stocate
    private boolean esteModNoapte = false;

    public Controller(ActivityRepository activitatiRepository) {
        this.activitatiRepository = activitatiRepository;
    }

    // metoda care acceseaza prima pagina folosind localhost:8080
    @GetMapping("/")
    public String activitati(Model model) {
        List<Activity> activitati = activitatiRepository.gasesteTot();
        List<Activity> activitatiUrgente = activitatiRepository.gasesteActivitatiUrgente();
        model.addAttribute("activitatiUrgente", activitatiUrgente);
        model.addAttribute("activitati", activitati);
        model.addAttribute("esteVizibil", false);
        model.addAttribute("esteModNoapte", esteModNoapte);
        return "activities";
    }
    // metoda care acceseaza activitatile
    @GetMapping("/activities")
    public String listeazaActivititati(@RequestParam(value = "esteVizibil", defaultValue = "false") boolean esteVizibil, Model model) {
        List<Activity> activitati = activitatiRepository.gasesteTot();
        List<Activity> activitatiUrgente = activitatiRepository.gasesteActivitatiUrgente();

        model.addAttribute("activitati", activitati);
        model.addAttribute("activitatiUrgente", activitatiUrgente);
        model.addAttribute("esteVizibil", esteVizibil);
        model.addAttribute("esteModNoapte", esteModNoapte);
        return "activities";
    }

    //metoda post ce este accesata de un formular din paginile html
    @PostMapping("/activities")
    public String salveazaActivitati(
            // @valid pentru a verifica daca datele trimise respecta constrangerile
            @Valid @ModelAttribute("activitate") Activity activitate,
            //binding result colecteaza erorile
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("esteModNoapte", esteModNoapte);
            return "new-activity";
        }
        List<Activity> activitati = activitatiRepository.gasesteTot();
        model.addAttribute("activitati", activitati);
        model.addAttribute("esteModNoapte", esteModNoapte);
        activitatiRepository.salveaza(activitate);
        model.addAttribute("esteVizibil", false);
        return "redirect:/activities";
    }

    // metoda get ce acceseaza pagina pentru a introduce o noua activitate
    @GetMapping("/activities/new")
    public String formularActivitateNoua(Model model) {
        model.addAttribute("activitate", new Activity());
        model.addAttribute("esteModNoapte", esteModNoapte);
        return "new-activity";
    }

    // metoda get ce acceseaza pagina pentru a sterge o activitate in functie de id
    //@pathvariable ia date din url
    @GetMapping("/activities/{id}/delete")
    public String confirmareStergere(@PathVariable Long id, Model model) {
        List<Activity> activitati = activitatiRepository.gasesteTot();
        model.addAttribute("activitati", activitati);
        List<Activity> activitatiUrgente = activitatiRepository.gasesteActivitatiUrgente();
        model.addAttribute("activitatiUrgente", activitatiUrgente);
        model.addAttribute("esteModNoapte", esteModNoapte);
        Activity activitate = activitatiRepository.gasesteTot().stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (activitate != null) {
            model.addAttribute("activitate", activitate);
            model.addAttribute("esteVizibil", true);
        }
        return "/activities";
    }

    //metoda post ce este accesata de un formular din paginile html
    @PostMapping("/activities/{id}/delete")
    public String stergeActivitate(@PathVariable Long id) {
        activitatiRepository.stergeInFunctieDeID(id);
        return "redirect:/activities";
    }

    // metoda get ce acceseaza pagina pentru a edita o activitate in functie de id
    @GetMapping("/activities/{id}/edit")
    public String editeazaActivitate(@PathVariable Long id, Model model) {
        Activity activitate = activitatiRepository.gasesteTot().stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (activitate != null) {
            model.addAttribute("activitate", activitate);
            model.addAttribute("esteModNoapte", esteModNoapte);
            return "edit-activity";
        }
        return "redirect:/activities";
    }

    //metoda post ce este accesata de un formular din paginile html
    @PostMapping("/activities/{id}/edit")
    public String actualizeazaActivitate(
            @PathVariable Long id,
            @Valid @ModelAttribute("activitate") Activity activitateActualizata,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("activitate", activitateActualizata);
            model.addAttribute("esteModNoapte", esteModNoapte);
            return "edit-activity";
        }
        Activity activitate = activitatiRepository.gasesteTot().stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (activitate != null) {
            activitate.setNume(activitateActualizata.getNume());
            activitate.setStatus(activitateActualizata.getStatus());
            activitate.setProgres(activitateActualizata.getProgres());
            activitate.setTermenLimita(activitateActualizata.getTermenLimita());
            activitatiRepository.salveazaEditare(activitate);
        }
        return "redirect:/activities";
    }
    @PostMapping("/toggleTheme")
    public String schimbaTema() {
        esteModNoapte = !esteModNoapte;
        return "redirect:/";
    }
}