package com.gestionstock.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import com.gestionstock.model.Categorie;
import com.gestionstock.service.CategorieService;
import com.gestionstock.service.CategorieServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class CategorieController {

    @FXML
    private TableView<Categorie> tableCategories;
    @FXML
    private TableColumn<Categorie, String> colonneNom;
    @FXML
    private TableColumn<Categorie, String> colonneDescription;
    @FXML
    private TableColumn<Categorie, Integer> colonneNbProduits;

    private final CategorieService categorieService = new CategorieServiceImpl();

    @FXML
    private void ouvrirDialogueAjout() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/gestionstock/AddCategorieDialog.fxml")
            );
            Parent racine = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter une categorie");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(racine));
            stage.showAndWait();

            AddCategorieDialogController controller = loader.getController();
            if (controller.isCategorieAjoutee()) {
                chargerDonnees();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ouvrirDialogueModification() {
        Categorie categorieSelectionnee = tableCategories.getSelectionModel().getSelectedItem();
        if (categorieSelectionnee == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez selectionner une categorie a modifier.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/gestionstock/AddCategorieDialog.fxml")
            );
            Parent racine = loader.load();

            AddCategorieDialogController controller = loader.getController();
            controller.setCategorieAModifier(categorieSelectionnee);

            Stage stage = new Stage();
            stage.setTitle("Modifier une categorie");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(racine));
            stage.showAndWait();

            if (controller.isCategorieAjoutee()) {
                chargerDonnees();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void supprimerCategorie() {
        Categorie categorieSelectionnee = tableCategories.getSelectionModel().getSelectedItem();
        if (categorieSelectionnee == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez selectionner une categorie a supprimer.");
            alert.showAndWait();
            return;
        }

        long nbProduitsLies = categorieService.countProduitsLies(categorieSelectionnee.getId());
        if (nbProduitsLies > 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING,
                    "Impossible de supprimer : " + nbProduitsLies + " produit(s) sont lies a cette categorie.");
            alert.showAndWait();
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Voulez-vous vraiment supprimer la categorie \"" + categorieSelectionnee.getNom() + "\" ?");
        confirmation.showAndWait().ifPresent(reponse -> {
            if (reponse == javafx.scene.control.ButtonType.OK) {
                try {
                    categorieService.deleteCategorie(categorieSelectionnee.getId());
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
        colonneDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colonneNbProduits.setCellValueFactory(new PropertyValueFactory<>("nbProduits"));

        chargerDonnees();
    }

    private void chargerDonnees() {
        List<Categorie> categories = categorieService.findAllCategories();
        ObservableList<Categorie> data = FXCollections.observableArrayList(categories);
        tableCategories.setItems(data);
    }

}