package com.gestionstock.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import com.gestionstock.model.Fournisseur;
import com.gestionstock.service.FournisseurService;
import com.gestionstock.service.FournisseurServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class FournisseurController {

    @FXML
    private TableView<Fournisseur> tableFournisseurs;
    @FXML
    private TableColumn<Fournisseur, String> colonneNom;
    @FXML
    private TableColumn<Fournisseur, String> colonneEmail;
    @FXML
    private TableColumn<Fournisseur, String> colonneTel;

    private final FournisseurService fournisseurService = new FournisseurServiceImpl();

    @FXML
    private void ouvrirDialogueAjout() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/gestionstock/AddFournisseurDialog.fxml")
            );
            Parent racine = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter un fournisseur");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(racine));
            stage.showAndWait();

            AddFournisseurDialogController controller = loader.getController();
            if (controller.isFournisseurAjoute()) {
                chargerDonnees();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ouvrirDialogueModification() {
        Fournisseur fournisseurSelectionne = tableFournisseurs.getSelectionModel().getSelectedItem();
        if (fournisseurSelectionne == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez selectionner un fournisseur a modifier.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/gestionstock/AddFournisseurDialog.fxml")
            );
            Parent racine = loader.load();

            AddFournisseurDialogController controller = loader.getController();
            controller.setFournisseurAModifier(fournisseurSelectionne);

            Stage stage = new Stage();
            stage.setTitle("Modifier un fournisseur");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(racine));
            stage.showAndWait();

            if (controller.isFournisseurAjoute()) {
                chargerDonnees();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void supprimerFournisseur() {
        Fournisseur fournisseurSelectionne = tableFournisseurs.getSelectionModel().getSelectedItem();
        if (fournisseurSelectionne == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez selectionner un fournisseur a supprimer.");
            alert.showAndWait();
            return;
        }

        long nbProduitsLies = fournisseurService.countProduitsLies(fournisseurSelectionne.getId());
        if (nbProduitsLies > 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING,
                    "Impossible de supprimer : " + nbProduitsLies + " produit(s) sont lies a ce fournisseur.");
            alert.showAndWait();
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Voulez-vous vraiment supprimer le fournisseur \"" + fournisseurSelectionne.getNom() + "\" ?");
        confirmation.showAndWait().ifPresent(reponse -> {
            if (reponse == javafx.scene.control.ButtonType.OK) {
                try {
                    fournisseurService.deleteFournisseur(fournisseurSelectionne.getId());
                    chargerDonnees();
                } catch (Exception e) {
                    Alert erreur = new Alert(Alert.AlertType.ERROR,
                            "Erreur lors de la suppression : " + e.getMessage());
                    erreur.showAndWait();
                }
            }
        });
    }

    @FXML
    private void initialize() {
        colonneNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colonneEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colonneTel.setCellValueFactory(new PropertyValueFactory<>("tel"));

        chargerDonnees();
    }

    private void chargerDonnees() {
        List<Fournisseur> fournisseurs = fournisseurService.findAllFournisseurs();
        ObservableList<Fournisseur> data = FXCollections.observableArrayList(fournisseurs);
        tableFournisseurs.setItems(data);
    }

}