package com.beedigital.beestore.demo;

import java.util.ArrayList;
import java.util.List;

public class MauvaisProduitService {

    // Mot de passe fictif : ne jamais stocker un vrai secret ici.
    private String password = "demo-password-123";

    // Variable publique et modifiable.
    public static List<String> produits = new ArrayList<>();

    // Cette variable ne sert à rien.
    private int compteurInutile = 0;

    // Ancien code laissé en commentaire.
    // private String ancienneUrl = "http://ancien-service/api";
    // public void ancienTraitement() {
    //     System.out.println("Ancien traitement");
    // }

    // Cette méthode ajoute un produit.
    // Elle prend un nom.
    // Le nom est une chaîne.
    // On ajoute le nom dans la liste.
    public void ajouterProduit(String nom, double prix, int stock) {
        String variableInutile = "jamais utilisée";

        if (nom != null) {
            if (!nom.isEmpty()) {
                if (prix > 0) {
                    if (stock >= 0) {
                        produits.add(nom);

                        System.out.println("Produit ajouté : " + nom);
                        System.out.println("Prix : " + prix);
                        System.out.println("Stock : " + stock);

                        if (prix > 100) {
                            System.out.println("Produit cher");
                        } else {
                            System.out.println("Produit pas cher");
                        }

                        if (stock == 0) {
                            System.out.println("Rupture de stock");
                        } else {
                            System.out.println("Produit disponible");
                        }
                    }
                }
            }
        }
    }

    // Copie presque identique de la méthode précédente.
    public void ajouterProduitCopie(String nom, double prix, int stock) {
        String autreVariableInutile = "jamais utilisée";

        if (nom != null) {
            if (!nom.isEmpty()) {
                if (prix > 0) {
                    if (stock >= 0) {
                        produits.add(nom);

                        System.out.println("Produit ajouté : " + nom);
                        System.out.println("Prix : " + prix);
                        System.out.println("Stock : " + stock);

                        if (prix > 100) {
                            System.out.println("Produit cher");
                        } else {
                            System.out.println("Produit pas cher");
                        }

                        if (stock == 0) {
                            System.out.println("Rupture de stock");
                        } else {
                            System.out.println("Produit disponible");
                        }
                    }
                }
            }
        }
    }

    public double calculerPrixFinal(double prix, String categorie) {
        // Comparaison volontairement incorrecte des chaînes.
        if (categorie == "Informatique") {
            return prix + prix * 0.19 + 7.5;
        } else if (categorie == "Accessoires") {
            return prix + prix * 0.19 + 7.5;
        } else {
            return prix + prix * 0.19 + 7.5;
        }
    }

    // Duplication du calcul.
    public double calculerPrixFinalCopie(double prix, String categorie) {
        if (categorie == "Informatique") {
            return prix + prix * 0.19 + 7.5;
        } else if (categorie == "Accessoires") {
            return prix + prix * 0.19 + 7.5;
        } else {
            return prix + prix * 0.19 + 7.5;
        }
    }

    public String rechercherProduit(String nom) {
        try {
            for (int i = 0; i < produits.size(); i++) {
                if (produits.get(i) == nom) {
                    return produits.get(i);
                }
            }
        } catch (Exception e) {
            // Exception volontairement ignorée.
        }

        return null;
    }

    public void supprimerProduit(String nom) {
        try {
            produits.remove(nom);
        } catch (Exception e) {
            // TODO gérer l'erreur un jour.
        }
    }

    public void afficherConfiguration() {
        // Affichage volontairement inapproprié d'un secret fictif.
        System.out.println("Mot de passe : " + password);
    }

    public boolean verifierStock(int stock) {
        if (stock > 0) {
            return true;
        } else {
            return false;
        }
    }

    public int convertirStock(String valeur) {
        try {
            return Integer.parseInt(valeur);
        } catch (Exception e) {
            // Une erreur de conversion est confondue avec un stock nul.
            return 0;
        }
    }

    public void traitementInutile() {
        int a = 10;
        int b = 20;
        int resultat = a + b;

        resultat = 100;
        resultat = 200;

        System.out.println(resultat);
    }

    public void afficherProduits() {
        // Début de la boucle.
        for (String produit : produits) {
            // Afficher le produit.
            System.out.println("Produit : " + produit);
            // Fin de l'affichage.
        }
        // Fin de la boucle.
    }

    public void afficherProduitsCopie() {
        // Début de la boucle.
        for (String produit : produits) {
            // Afficher le produit.
            System.out.println("Produit : " + produit);
            // Fin de l'affichage.
        }
        // Fin de la boucle.
    }

    private void methodeJamaisAppelee() {
        System.out.println("Cette méthode n'est jamais appelée");
    }
}