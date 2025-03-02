package org.example.Controller;

import org.example.entities.AvisVoiture;
import org.example.services.AvisVoitureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/avis-voiture")
public class AvisVoitureController {

    @Autowired
    private AvisVoitureService avisVoitureService;

    // Récupérer les avis d'une voiture
    @GetMapping("/voiture/{idvoiture}")
    public List<AvisVoiture> getAvisByVoiture(@PathVariable int idvoiture) throws SQLException {
        return avisVoitureService.getAvisByVoiture(idvoiture);
    }

    // Ajouter un avis pour une voiture
    @PostMapping("/add")
    public AvisVoiture addAvisVoiture(@RequestBody AvisVoiture avisVoiture) throws SQLException {
        // Valider que la note est entre 1 et 5
        if (avisVoiture.getNote() < 1 || avisVoiture.getNote() > 5) {
            throw new IllegalArgumentException("La note doit être entre 1 et 5.");
        }
        return avisVoitureService.ajouterAvis(avisVoiture);
    }

    // Supprimer un avis
    @DeleteMapping("/delete/{id}")
    public void deleteAvisVoiture(@PathVariable int id) throws SQLException {
        avisVoitureService.supprimerAvis(id);
    }

    // Calculer la note moyenne d'une voiture
    @GetMapping("/moyenne/{idvoiture}")
    public double getAverageRating(@PathVariable int idvoiture) throws SQLException {
        return avisVoitureService.getAverageRating(idvoiture);
    }
}