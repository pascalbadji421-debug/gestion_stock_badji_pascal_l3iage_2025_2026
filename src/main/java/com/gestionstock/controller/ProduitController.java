package com.gestionstock.controller;

import com.gestionstock.model.Categorie;
import com.gestionstock.model.Fournisseur;
import com.gestionstock.service.ProduitService;
import com.gestionstock.service.ProduitServiceImpl;
import com.gestionstock.model.Produit;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ProduitController {
    @FXML
    TableView<Produit> tableProduits;
    @FXML
    TableColumn<Produit, Integer> colonneNom;
    @FXML
    TableColumn<Produit, Double> colonnePrix;
    @FXML
    TableColumn<Produit, Integer> colonneStock;
    @FXML
    TableColumn<Produit, Integer> colonneStockMin;
    @FXML
    TableColumn<Produit, String> colonneCategorie;
    @FXML
    TableColumn<Produit, String> colonneFournisseur;
    @FXML
    TextField champRecherche;

    private final ProduitService produitService = new ProduitServiceImpl();

    // Liste complète chargée depuis la base, utilisée comme référence pour la recherche
    private ObservableList<Produit> listeProduits;

    @FXML
    public void initialize() {
        configurerColones();
        chargerDonnees();
    }

    private void configurerColones() {
        // Lier chaque colonne à un attribut de la classe Produit
        colonneNom.setCellValueFactory( new PropertyValueFactory<>("nom"));
        colonnePrix.setCellValueFactory( new PropertyValueFactory<>("prix"));
        colonneStock.setCellValueFactory( new PropertyValueFactory<>("quantiteStock"));
        colonneStockMin.setCellValueFactory( new PropertyValueFactory<>("quantiteMin"));
        colonneCategorie.setCellValueFactory( data -> {
            Categorie cat = data.getValue().getCategorie();
            return new SimpleStringProperty(cat != null ? cat.getNom() : "");
        });
        colonneFournisseur.setCellValueFactory( data -> {
            Fournisseur fournisseur = data.getValue().getFournisseur();
            return new SimpleStringProperty(fournisseur != null ? fournisseur.getNom() : "");
        });
    }

    private void chargerDonnees() {
        // Charger des données depuis la base via JDBC API
        List<Produit> produits = produitService.findAllProduits();

        listeProduits = FXCollections.observableArrayList(produits);

        tableProduits.setItems(listeProduits);
    }

    @FXML
    private void rechercherProduits() {
        String recherche = champRecherche.getText();

        if (recherche == null || recherche.isBlank()) {
            tableProduits.setItems(listeProduits);
            return;
        }

        String rechercheMinuscule = recherche.trim().toLowerCase();

        ObservableList<Produit> resultats = listeProduits.filtered(produit ->
                (produit.getNom() != null && produit.getNom().toLowerCase().contains(rechercheMinuscule))
        );

        tableProduits.setItems(resultats);
    }

    @FXML
    private void supprimerProduit() {
        Produit produitSelectionne = tableProduits.getSelectionModel().getSelectedItem();

        if (produitSelectionne == null) {
            Alert alerteInfo = new Alert(Alert.AlertType.INFORMATION);
            alerteInfo.setTitle("Aucune sélection");
            alerteInfo.setHeaderText(null);
            alerteInfo.setContentText("Veuillez sélectionner un produit à supprimer.");
            alerteInfo.showAndWait();
            return;
        }

        Alert alerteConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
        alerteConfirmation.setTitle("Confirmation de suppression");
        alerteConfirmation.setHeaderText(null);
        alerteConfirmation.setContentText("Voulez-vous vraiment supprimer le produit \"" + produitSelectionne.getNom() + "\" ?");

        Optional<ButtonType> reponse = alerteConfirmation.showAndWait();

        if (reponse.isPresent() && reponse.get() == ButtonType.OK) {
            produitService.deleteProduit(produitSelectionne.getId());
            chargerDonnees();
        }
    }

    @FXML
    private void ouvrirDialogueAjout() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/gestionstock/AddProduitDialog.fxml")
            );
            Parent racine = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter un produit");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(racine));
            stage.showAndWait();

            AddProduitDialogController controller = loader.getController();
            if (controller.isProduitAjoute()) {
                chargerDonnees();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}