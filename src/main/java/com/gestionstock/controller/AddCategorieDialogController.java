package com.gestionstock.controller;

import com.gestionstock.model.Categorie;
import com.gestionstock.service.CategorieService;
import com.gestionstock.service.CategorieServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddCategorieDialogController {

    @FXML
    private TextField champNom;
    @FXML
    private TextArea champDescription;
    @FXML
    private Label labelErreur;

    private final CategorieService categorieService = new CategorieServiceImpl();

    private boolean categorieAjoutee = false;

    // null = mode ajout, non-null = mode modification
    private Categorie categorieEnEdition = null;

    /**
     * Appelée depuis CategorieController pour passer en mode modification.
     * Pré-remplit les champs avec les données de la catégorie sélectionnée.
     */
    public void setCategorieAModifier(Categorie categorie) {
        this.categorieEnEdition = categorie;
        champNom.setText(categorie.getNom());
        champDescription.setText(categorie.getDescription());
    }

    @FXML
    private void enregistrer() {
        String nom = champNom.getText();
        String description = champDescription.getText();

        if (nom == null || nom.trim().length() < 2) {
            labelErreur.setText("Le nom doit contenir au moins 2 caracteres.");
            return;
        }

        try {
            if (categorieEnEdition == null) {
                // Mode ajout
                Categorie categorie = new Categorie(description, nom.trim());
                categorieService.addCategorie(categorie);
            } else {
                // Mode modification
                categorieEnEdition.setNom(nom.trim());
                categorieEnEdition.setDescription(description);
                categorieService.updateCategorie(categorieEnEdition);
            }
            categorieAjoutee = true;
            fermerFenetre();
        } catch (Exception e) {
            labelErreur.setText("Erreur lors de l'enregistrement : " + e.getMessage());
        }
    }

    @FXML
    private void annuler() {
        fermerFenetre();
    }

    private void fermerFenetre() {
        Stage stage = (Stage) champNom.getScene().getWindow();
        stage.close();
    }

    public boolean isCategorieAjoutee() {
        return categorieAjoutee;
    }
}