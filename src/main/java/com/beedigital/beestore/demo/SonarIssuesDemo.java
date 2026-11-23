package com.beedigital.beestore.demo;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

public class SonarIssuesDemo {

    /*
     * DEMONSTRATION UNIQUEMENT.
     * Cette implémentation accepte les certificats sans vérification.
     * Elle ne doit jamais être utilisée par l'application.
     */
    public X509TrustManager creerTrustManagerNonSecurise() {
        return new X509TrustManager() {

            @Override
            public void checkClientTrusted(
                    X509Certificate[] chain,
                    String authType
            ) {
                // Volontairement incorrect : aucune vérification.
            }

            @Override
            public void checkServerTrusted(
                    X509Certificate[] chain,
                    String authType
            ) {
                // Volontairement incorrect : aucune vérification.
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    }

    public void afficherProduit(String nom) {
        String variableInutilisee = "Valeur inutile";
        int compteurInutilise = 42;

        System.out.println("Produit : " + nom);
        System.out.println("Produit : " + nom);
        System.out.println("Produit : " + nom);
    }

    private void methodeJamaisUtilisee() {
        System.out.println("Méthode privée inutilisée");
    }
}