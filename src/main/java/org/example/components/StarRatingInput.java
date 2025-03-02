package org.example.components;

import javafx.scene.layout.HBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class StarRatingInput extends HBox {
    private static final Image STAR_FULL = new Image("/images/star-full.png");
    private static final Image STAR_EMPTY = new Image("/images/star-empty.png");

    private int rating = 0; // Note sélectionnée par l'utilisateur

    public StarRatingInput() {
        this.setSpacing(2); // Espace entre les étoiles

        // Créer 5 étoiles vides
        for (int i = 0; i < 5; i++) {
            ImageView star = new ImageView(STAR_EMPTY);
            star.setFitWidth(20); // Taille des étoiles
            star.setFitHeight(20);

            // Gestionnaire d'événements pour cliquer sur une étoile
            final int starIndex = i + 1; // Note correspondante (1 à 5)
            star.setOnMouseClicked(event -> setRating(starIndex));

            this.getChildren().add(star);
        }
    }

    // Définir la note sélectionnée
    public void setRating(int rating) {
        this.rating = rating;

        // Mettre à jour l'affichage des étoiles
        for (int i = 0; i < 5; i++) {
            ImageView star = (ImageView) this.getChildren().get(i);
            if (i < rating) {
                star.setImage(STAR_FULL); // Étoile pleine
            } else {
                star.setImage(STAR_EMPTY); // Étoile vide
            }
        }
    }

    // Récupérer la note sélectionnée
    public int getRating() {
        return rating;
    }
}