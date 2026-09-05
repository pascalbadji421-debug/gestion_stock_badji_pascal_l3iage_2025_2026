package com.gestionstock.controller;

import com.gestionstock.model.Fournisseur;
import com.gestionstock.service.FournisseurService;
import com.gestionstock.service.FournisseurServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddFournisseurDialogController {

    @FXML
    private TextField champNom;
    @FXML
    private TextField champEmail;
    @FXML
    private TextField champTel;
    @FXML
    private Label labelErreur;

    private final FournisseurService fournisseurService = new FournisseurServiceImpl();

    private boolean fournisseurAjoute = false;

    // null = mode ajout, non-null = mode modification
    private Fournisseur fournisseurEnEdition = null;

    /**
     * Appelée depuis FournisseurController pour passer en mode modification.
     * Pré-remplit les champs avec les données du fournisseur sélectionné.
     */
    public void setFournisseurAModifier(Fournisseur fournisseur) {
        this.fournisseurEnEdition = fournisseur;
        champNom.setText(fournisseur.getNom());
        champEmail.setText(fournisseur.getEmail());
        champTel.setText(fournisseur.getTel());
    }

    @FXML
    private void enregistrer() {
        String nom = champNom.getText();
        String email = champEmail.getText();
        String tel = champTel.getText();

        if (nom == null || nom.trim().length() < 2) {
            labelErreur.setText("Le nom doit contenir au moins 2 caracteres.");
            return;
        }

        try {
            if (fournisseurEnEdition == null) {
                // Mode ajout
                Fournisseur fournisseur = new Fournisseur(nom.trim(), email, tel);
                fournisseurService.addFournisseur(fournisseur);
            } else {
                // Mode modification
                fournisseurEnEdition.setNom(nom.trim());
                fournisseurEnEdition.setEmail(email);
                fournisseurEnEdition.setTel(tel);
                fournisseurService.updateFournisseur(fournisseurEnEdition);
            }
            fournisseurAjoute = true;
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

    public boolean isFournisseurAjoute() {
        return fournisseurAjoute;
    }
}