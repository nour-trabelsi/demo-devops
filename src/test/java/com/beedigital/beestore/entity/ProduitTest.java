package com.beedigital.beestore.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ProduitTest {

    private static final LocalDateTime DATE_CREATION =
            LocalDateTime.of(2026, 11, 2, 10, 30);

    // ---------------------------------------------------------
    // CONSTRUCTEURS
    // ---------------------------------------------------------

    @Test
    @DisplayName("Constructeur vide : actif vaut true")
    void constructeurVide_doitInitialiserValeursParDefaut() {
        Produit produit = new Produit();

        assertThat(produit.getId()).isNull();
        assertThat(produit.getNom()).isNull();
        assertThat(produit.getDescription()).isNull();
        assertThat(produit.getPrix()).isNull();
        assertThat(produit.getStock()).isNull();
        assertThat(produit.getCategorie()).isNull();
        assertThat(produit.getActif()).isTrue();
        assertThat(produit.getDateCreation()).isNull();
    }

    @Test
    @DisplayName("Constructeur complet : initialiser tous les champs")
    void constructeurComplet_doitInitialiserTousLesChamps() {
        Produit produit = creerProduit();

        verifierChamps(produit);
    }

    // ---------------------------------------------------------
    // GETTERS ET SETTERS
    // ---------------------------------------------------------

    @Test
    @DisplayName("Getters et setters : conserver les valeurs")
    void gettersEtSetters_doiventConserverValeurs() {
        Produit produit = new Produit();

        produit.setId(1L);
        produit.setNom("Laptop Pro");
        produit.setDescription("Ordinateur portable");
        produit.setPrix(1299.99);
        produit.setStock(10);
        produit.setCategorie("Informatique");
        produit.setActif(true);
        produit.setDateCreation(DATE_CREATION);

        verifierChamps(produit);

        produit.setActif(false);

        assertThat(produit.getActif()).isFalse();
    }

    @Test
    @DisplayName("Setters : accepter les valeurs null en mémoire")
    void setters_doiventAccepterNull() {
        Produit produit = creerProduit();

        produit.setId(null);
        produit.setNom(null);
        produit.setDescription(null);
        produit.setPrix(null);
        produit.setStock(null);
        produit.setCategorie(null);
        produit.setActif(null);
        produit.setDateCreation(null);

        assertThat(produit.getId()).isNull();
        assertThat(produit.getNom()).isNull();
        assertThat(produit.getDescription()).isNull();
        assertThat(produit.getPrix()).isNull();
        assertThat(produit.getStock()).isNull();
        assertThat(produit.getCategorie()).isNull();
        assertThat(produit.getActif()).isNull();
        assertThat(produit.getDateCreation()).isNull();
    }

    // ---------------------------------------------------------
    // BUILDER
    // ---------------------------------------------------------

    @Test
    @DisplayName("Builder : construire un produit complet")
    void builder_doitConstruireProduitComplet() {
        Produit produit = Produit.builder()
                .id(1L)
                .nom("Laptop Pro")
                .description("Ordinateur portable")
                .prix(1299.99)
                .stock(10)
                .categorie("Informatique")
                .actif(true)
                .dateCreation(DATE_CREATION)
                .build();

        verifierChamps(produit);
    }

    @Test
    @DisplayName("Builder vide : les champs sont null sans Builder.Default")
    void builderVide_doitConstruireProduitAvecChampsNull() {
        Produit produit = Produit.builder().build();

        assertThat(produit.getId()).isNull();
        assertThat(produit.getNom()).isNull();
        assertThat(produit.getDescription()).isNull();
        assertThat(produit.getPrix()).isNull();
        assertThat(produit.getStock()).isNull();
        assertThat(produit.getCategorie()).isNull();
        assertThat(produit.getActif()).isNull();
        assertThat(produit.getDateCreation()).isNull();
    }

    @Test
    @DisplayName("Builder : fournir une représentation textuelle")
    void builderToString_doitContenirValeurs() {
        var builder = Produit.builder()
                .id(1L)
                .nom("Laptop Pro")
                .description("Ordinateur portable")
                .prix(1299.99)
                .stock(10)
                .categorie("Informatique")
                .actif(true)
                .dateCreation(DATE_CREATION);

        assertThat(builder.toString()).contains(
                "ProduitBuilder",
                "id=1",
                "nom=Laptop Pro",
                "description=Ordinateur portable",
                "prix=1299.99",
                "stock=10",
                "categorie=Informatique",
                "actif=true",
                "dateCreation=" + DATE_CREATION
        );
    }

    // ---------------------------------------------------------
    // CALLBACK PRE PERSIST
    // ---------------------------------------------------------

    @Test
    @DisplayName("onCreate : initialiser la date et actif si actif est null")
    void onCreate_actifNull_doitInitialiserDateEtActif() {
        Produit produit = new Produit();
        produit.setActif(null);

        LocalDateTime avant = LocalDateTime.now();

        produit.onCreate();

        LocalDateTime apres = LocalDateTime.now();

        assertThat(produit.getDateCreation()).isBetween(avant, apres);
        assertThat(produit.getActif()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @DisplayName("onCreate : conserver actif lorsqu'il est renseigné")
    void onCreate_actifRenseigne_doitConserverValeur(Boolean actif) {
        Produit produit = new Produit();
        produit.setActif(actif);

        LocalDateTime avant = LocalDateTime.now();

        produit.onCreate();

        LocalDateTime apres = LocalDateTime.now();

        assertThat(produit.getDateCreation()).isBetween(avant, apres);
        assertThat(produit.getActif()).isEqualTo(actif);
    }

    @Test
    @DisplayName("onCreate : remplacer une date précédemment renseignée")
    void onCreate_dateExistante_doitRemplacerDate() {
        Produit produit = creerProduit();
        produit.setDateCreation(LocalDateTime.of(2000, 1, 1, 0, 0));

        LocalDateTime avant = LocalDateTime.now();

        produit.onCreate();

        LocalDateTime apres = LocalDateTime.now();

        assertThat(produit.getDateCreation()).isBetween(avant, apres);
    }

    // ---------------------------------------------------------
    // EQUALS ET HASH CODE
    // ---------------------------------------------------------

    @Test
    @DisplayName("Equals : vérifier identité, null et autre type")
    void equals_casDeBase() {
        Produit produit = creerProduit();

        assertThat(produit.equals(produit)).isTrue();
        assertThat(produit.equals(null)).isFalse();
        assertThat(produit.equals("Autre type")).isFalse();
    }

    @Test
    @DisplayName("Equals et hashCode : deux produits identiques")
    void equalsEtHashCode_memesValeurs() {
        Produit premier = creerProduit();
        Produit second = creerProduit();

        assertThat(premier.equals(second)).isTrue();
        assertThat(second.equals(premier)).isTrue();
        assertThat(premier.hashCode()).isEqualTo(second.hashCode());
    }

    @Test
    @DisplayName("Equals et hashCode : tous les champs à null")
    void equalsEtHashCode_champsNull() {
        Produit premier = creerProduitVide();
        Produit second = creerProduitVide();

        assertThat(premier.equals(second)).isTrue();
        assertThat(second.equals(premier)).isTrue();
        assertThat(premier.hashCode()).isEqualTo(second.hashCode());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "id",
            "nom",
            "description",
            "prix",
            "stock",
            "categorie",
            "actif",
            "dateCreation"
    })
    @DisplayName("Equals : chaque champ participe à la comparaison")
    void equals_champDifferent_doitRetournerFalse(String nomChamp)
            throws ReflectiveOperationException {

        Produit premier = creerProduit();
        Produit second = creerProduit();

        modifierChamp(second, nomChamp, valeurDifferente(nomChamp));

        assertThat(premier.equals(second)).isFalse();
        assertThat(second.equals(premier)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "id",
            "nom",
            "description",
            "prix",
            "stock",
            "categorie",
            "actif",
            "dateCreation"
    })
    @DisplayName("Equals : distinguer null d'une valeur renseignée")
    void equals_champNull_doitRetournerFalse(String nomChamp)
            throws ReflectiveOperationException {

        Produit premier = creerProduit();
        Produit second = creerProduit();

        modifierChamp(premier, nomChamp, null);

        assertThat(premier.equals(second)).isFalse();
        assertThat(second.equals(premier)).isFalse();
    }

    @Test
    @DisplayName("Equals : respecter le refus de comparaison d'une sous-classe")
    void equals_canEqualRefuse_doitRetournerFalse() {
        Produit produit = creerProduit();

        Produit sousClasse = new Produit() {
            @Override
            protected boolean canEqual(Object other) {
                return false;
            }
        };

        assertThat(produit.equals(sousClasse)).isFalse();
    }

    @Test
    @DisplayName("canEqual : accepter uniquement un Produit")
    void canEqual_doitVerifierType() {
        Produit produit = creerProduit();

        assertThat(produit.canEqual(creerProduit())).isTrue();
        assertThat(produit.canEqual(null)).isFalse();
        assertThat(produit.canEqual("Autre type")).isFalse();
    }

    // ---------------------------------------------------------
    // TO STRING
    // ---------------------------------------------------------

    @Test
    @DisplayName("toString : afficher les valeurs des champs")
    void toString_doitContenirChamps() {
        assertThat(creerProduit().toString()).contains(
                "Produit",
                "id=1",
                "nom=Laptop Pro",
                "description=Ordinateur portable",
                "prix=1299.99",
                "stock=10",
                "categorie=Informatique",
                "actif=true",
                "dateCreation=" + DATE_CREATION
        );
    }

    @Test
    @DisplayName("toString : accepter tous les champs à null")
    void toString_champsNull_doitFonctionner() {
        assertThat(creerProduitVide().toString()).contains(
                "id=null",
                "nom=null",
                "description=null",
                "prix=null",
                "stock=null",
                "categorie=null",
                "actif=null",
                "dateCreation=null"
        );
    }

    // ---------------------------------------------------------
    // METHODES UTILITAIRES
    // ---------------------------------------------------------

    private Produit creerProduit() {
        return new Produit(
                1L,
                "Laptop Pro",
                "Ordinateur portable",
                1299.99,
                10,
                "Informatique",
                true,
                DATE_CREATION
        );
    }

    private Produit creerProduitVide() {
        Produit produit = new Produit();
        produit.setActif(null);
        return produit;
    }

    private void verifierChamps(Produit produit) {
        assertThat(produit.getId()).isEqualTo(1L);
        assertThat(produit.getNom()).isEqualTo("Laptop Pro");
        assertThat(produit.getDescription())
                .isEqualTo("Ordinateur portable");
        assertThat(produit.getPrix()).isEqualTo(1299.99);
        assertThat(produit.getStock()).isEqualTo(10);
        assertThat(produit.getCategorie()).isEqualTo("Informatique");
        assertThat(produit.getActif()).isTrue();
        assertThat(produit.getDateCreation()).isEqualTo(DATE_CREATION);
    }

    private void modifierChamp(
            Produit produit,
            String nomChamp,
            Object valeur
    ) throws ReflectiveOperationException {

        Field champ = Produit.class.getDeclaredField(nomChamp);
        champ.setAccessible(true);
        champ.set(produit, valeur);
    }

    private Object valeurDifferente(String nomChamp) {
        return switch (nomChamp) {
            case "id" -> 2L;
            case "nom" -> "Autre produit";
            case "description" -> "Autre description";
            case "prix" -> 49.90;
            case "stock" -> 25;
            case "categorie" -> "Accessoires";
            case "actif" -> false;
            case "dateCreation" -> DATE_CREATION.plusDays(1);
            default -> throw new IllegalArgumentException(
                    "Champ inconnu : " + nomChamp
            );
        };
    }
}