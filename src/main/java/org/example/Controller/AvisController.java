package org.example.Controller;

import org.example.entities.Avis;
import org.example.services.AvisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/avis")
public class AvisController {
    @Autowired
    private AvisService avisService;

    // Récupérer les avis d'un hébergement
    @GetMapping("/hebergement/{idheb}")
    public List<Avis> getAvisByHebergement(@PathVariable int idheb) throws SQLException {
        return avisService.getAvisByHebergement(idheb);
    }

    // Ajouter un avis
    @PostMapping("/add")
    public Avis addAvis(@RequestBody Avis avis, @RequestParam int iduser) throws SQLException {
        // Valider que la note est entre 1 et 5
        if (avis.getNote() < 1 || avis.getNote() > 5) {
            throw new IllegalArgumentException("La note doit être entre 1 et 5.");
        }

        // Définir l'iduser dans l'objet Avis
        avis.setIduser(iduser);

        // Ajouter la date de création automatiquement
        avis.setDateCreation(new java.util.Date());

        return avisService.ajouterAvis(avis);
    }

    // Supprimer un avis
    @DeleteMapping("/delete/{id}")
    public void deleteAvis(@PathVariable int id) throws SQLException {
        avisService.supprimerAvis(id);
    }

    // Récupérer la note moyenne d'un hébergement
    @GetMapping("/average/{idheb}")
    public double getAverageRating(@PathVariable int idheb) throws SQLException {
        return avisService.getAverageRating(idheb);
    }
}