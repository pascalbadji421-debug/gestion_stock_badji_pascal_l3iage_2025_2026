package com.gestionstock.controller;

import com.gestionstock.model.Categorie;
import com.gestionstock.model.Fournisseur;
import com.gestionstock.model.Produit;
import com.gestionstock.service.CategorieService;
import com.gestionstock.service.CategorieServiceImpl;
import com.gestionstock.service.FournisseurService;
import com.gestionstock.service.FournisseurServiceImpl;
import com.gestionstock.service.ProduitService;
import com.gestionstock.service.ProduitServiceImpl;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddProduitDialogController {

    @FXML
    private TextField champNom;
    @FXML
    private TextField champPrix;
    @FXML
    private TextField champStock;
    @FXML
    private TextField champStockMin;
    @FXML
    private ComboBox<Categorie> comboCategorie;
    @FXML
    private ComboBox<Fournisseur> comboFournisseur;
    @FXML
    private Label labelErreur;

    private final ProduitService produitService = new ProduitServiceImpl();
    private final CategorieService categorieService = new CategorieServiceImpl();
    private final FournisseurService fournisseurService = new FournisseurServiceImpl();

    private boolean produitAjoute = false;

    // null = mode ajout, non-null = mode modification
    private Produit produitEnEdition = null;

    @FXML
    private void initialize() {
        comboCategorie.setItems(FXCollections.observableArrayList(categorieService.findAllCategories()));
        comboFournisseur.setItems(FXCollections.observableArrayList(fournisseurService.findAllFournisseurs()));

        comboCategorie.setConverter(new javafx.util.StringConverter<Categorie>() {
            @Override
            public String toString(Categorie c) {
                return c == null ? "" : c.getNom();
            }
            @Override
            public Categorie fromString(String s) {
                return null;
            }
        });

        comboFournisseur.setConverter(new javafx.util.StringConverter<Fournisseur>() {
            @Override
            public String toString(Fournisseur f) {
                return f == null ? "" : f.getNom();
            }
            @Override
            public Fournisseur fromString(String s) {
                return null;
            }
        });
    }

    /**
     * Appelée depuis ProduitController pour passer en mode modification.
     * Pré-remplit les champs avec les données du produit sélectionné.
     */
    public void setProduitAModifier(Produit produit) {
        this.produitEnEdition = produit;
        champNom.setText(produit.getNom());
        champPrix.setText(String.valueOf(produit.getPrix()));
        champStock.setText(String.valueOf(produit.getQuantiteStock()));
        champStockMin.setText(String.valueOf(produit.getQuantiteMin()));
        comboCategorie.setValue(produit.getCategorie());
        comboFournisseur.setValue(produit.getFournisseur());
    }

    @FXML
    private void enregistrer() {
        String nom = champNom.getText();

        if (nom == null || nom.trim().length() < 2) {
            labelErreur.setText("Le nom doit contenir au moins 2 caracteres.");
            return;
        }

        double prix;
        int stock;
        int stockMin;
        try {
            prix = Double.parseDouble(champPrix.getText().trim());
            stock = Integer.parseInt(champStock.getText().trim());
            stockMin = Integer.parseInt(champStockMin.getText().trim());
        } catch (NumberFormatException e) {
            labelErreur.setText("Prix, stock et stock min doivent etre des nombres valides.");
            return;
        }

        Categorie categorie = comboCategorie.getValue();
        Fournisseur fournisseur = comboFournisseur.getValue();

        try {
            if (produitEnEdition == null) {
                // Mode ajout
                Produit produit = new Produit(nom.trim(), stock, stockMin, prix, categorie, fournisseur);
                produitService.addProduit(produit);
            } else {
                // Mode modification
                produitEnEdition.setNom(nom.trim());
                produitEnEdition.setPrix(prix);
                produitEnEdition.setQuantiteStock(stock);
                produitEnEdition.setQuantiteMin(stockMin);
                produitEnEdition.setCategorie(categorie);
                produitEnEdition.setFournisseur(fournisseur);
                produitService.updateProduit(produitEnEdition);
            }
            produitAjoute = true;
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

    public boolean isProduitAjoute() {
        return produitAjoute;
    }
}