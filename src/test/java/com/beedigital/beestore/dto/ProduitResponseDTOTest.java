package com.beedigital.beestore.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ProduitResponseDTOTest {

    private static final LocalDateTime DATE_CREATION =
            LocalDateTime.of(2026, 11, 2, 10, 30);

    // ---------------------------------------------------------
    // CONSTRUCTEUR SANS ARGUMENT
    // ---------------------------------------------------------

    @Test
    @DisplayName("Constructeur vide : tous les champs sont null")
    void constructeurSansArgument_doitInitialiserChampsANull() {
        ProduitResponseDTO dto = new ProduitResponseDTO();

        assertThat(dto.getId()).isNull();
        assertThat(dto.getNom()).isNull();
        assertThat(dto.getDescription()).isNull();
        assertThat(dto.getPrix()).isNull();
        assertThat(dto.getStock()).isNull();
        assertThat(dto.getCategorie()).isNull();
        assertThat(dto.getActif()).isNull();
        assertThat(dto.getDateCreation()).isNull();
    }

    // ---------------------------------------------------------
    // CONSTRUCTEUR AVEC TOUS LES ARGUMENTS
    // ---------------------------------------------------------

    @Test
    @DisplayName("Constructeur complet : initialiser tous les champs")
    void constructeurComplet_doitInitialiserTousLesChamps() {
        ProduitResponseDTO dto = creerDTO();

        verifierChamps(dto);
    }

    // ---------------------------------------------------------
    // GETTERS ET SETTERS
    // ---------------------------------------------------------

    @Test
    @DisplayName("Setters et getters : conserver les valeurs affectées")
    void settersEtGetters_doiventConserverValeurs() {
        ProduitResponseDTO dto = new ProduitResponseDTO();

        dto.setId(1L);
        dto.setNom("Laptop Pro");
        dto.setDescription("Ordinateur portable");
        dto.setPrix(1299.99);
        dto.setStock(10);
        dto.setCategorie("Informatique");
        dto.setActif(true);
        dto.setDateCreation(DATE_CREATION);

        verifierChamps(dto);
    }

    @Test
    @DisplayName("Setters : accepter des valeurs null")
    void setters_doiventAccepterNull() {
        ProduitResponseDTO dto = creerDTO();

        dto.setId(null);
        dto.setNom(null);
        dto.setDescription(null);
        dto.setPrix(null);
        dto.setStock(null);
        dto.setCategorie(null);
        dto.setActif(null);
        dto.setDateCreation(null);

        assertThat(dto.getId()).isNull();
        assertThat(dto.getNom()).isNull();
        assertThat(dto.getDescription()).isNull();
        assertThat(dto.getPrix()).isNull();
        assertThat(dto.getStock()).isNull();
        assertThat(dto.getCategorie()).isNull();
        assertThat(dto.getActif()).isNull();
        assertThat(dto.getDateCreation()).isNull();
    }

    // ---------------------------------------------------------
    // BUILDER
    // ---------------------------------------------------------

    @Test
    @DisplayName("Builder : construire un DTO complet")
    void builder_doitConstruireDTOComplet() {
        ProduitResponseDTO dto = ProduitResponseDTO.builder()
                .id(1L)
                .nom("Laptop Pro")
                .description("Ordinateur portable")
                .prix(1299.99)
                .stock(10)
                .categorie("Informatique")
                .actif(true)
                .dateCreation(DATE_CREATION)
                .build();

        verifierChamps(dto);
    }

    @Test
    @DisplayName("Builder vide : construire un DTO avec des champs null")
    void builderVide_doitConstruireDTOVide() {
        ProduitResponseDTO dto = ProduitResponseDTO.builder().build();

        assertThat(dto).isEqualTo(new ProduitResponseDTO());
    }

    @Test
    @DisplayName("Builder : fournir une représentation textuelle")
    void builderToString_doitContenirLesValeurs() {
        var builder = ProduitResponseDTO.builder()
                .id(1L)
                .nom("Laptop Pro")
                .description("Ordinateur portable")
                .prix(1299.99)
                .stock(10)
                .categorie("Informatique")
                .actif(true)
                .dateCreation(DATE_CREATION);

        assertThat(builder.toString())
                .contains(
                        "ProduitResponseDTOBuilder",
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
    // EQUALS
    // ---------------------------------------------------------

    @Test
    @DisplayName("Equals : un DTO est égal à lui-même")
    void equals_memeInstance_doitRetournerTrue() {
        ProduitResponseDTO dto = creerDTO();

        assertThat(dto.equals(dto)).isTrue();
    }

    @Test
    @DisplayName("Equals : null et un autre type ne sont pas égaux au DTO")
    void equals_nullOuAutreType_doitRetournerFalse() {
        ProduitResponseDTO dto = creerDTO();

        assertThat(dto.equals(null)).isFalse();
        assertThat(dto.equals("Laptop Pro")).isFalse();
    }

    @Test
    @DisplayName("Equals : deux DTO identiques sont égaux")
    void equals_memesValeurs_doitRetournerTrue() {
        ProduitResponseDTO premier = creerDTO();
        ProduitResponseDTO second = creerDTO();

        assertThat(premier.equals(second)).isTrue();
        assertThat(second.equals(premier)).isTrue();
        assertThat(premier.hashCode()).isEqualTo(second.hashCode());
    }

    @Test
    @DisplayName("Equals : deux DTO vides sont égaux")
    void equals_champsNull_doitRetournerTrue() {
        ProduitResponseDTO premier = new ProduitResponseDTO();
        ProduitResponseDTO second = new ProduitResponseDTO();

        assertThat(premier.equals(second)).isTrue();
        assertThat(second.equals(premier)).isTrue();
        assertThat(premier.hashCode()).isEqualTo(second.hashCode());
    }

    @ParameterizedTest(name = "Champ différent : {0}")
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

        ProduitResponseDTO premier = creerDTO();
        ProduitResponseDTO second = creerDTO();

        modifierChamp(second, nomChamp, valeurDifferente(nomChamp));

        assertThat(premier.equals(second)).isFalse();
        assertThat(second.equals(premier)).isFalse();
    }

    @ParameterizedTest(name = "Champ null : {0}")
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
    @DisplayName("Equals : distinguer un champ null d'un champ renseigné")
    void equals_champNullContreRenseigne_doitRetournerFalse(
            String nomChamp
    ) throws ReflectiveOperationException {

        ProduitResponseDTO premier = creerDTO();
        ProduitResponseDTO second = creerDTO();

        modifierChamp(premier, nomChamp, null);

        assertThat(premier.equals(second)).isFalse();
        assertThat(second.equals(premier)).isFalse();
    }

    @Test
    @DisplayName("Equals : respecter le refus de comparaison d'une sous-classe")
    void equals_canEqualRefuse_doitRetournerFalse() {
        ProduitResponseDTO dto = creerDTO();

        ProduitResponseDTO sousClasse = new ProduitResponseDTO() {
            @Override
            protected boolean canEqual(Object other) {
                return false;
            }
        };

        assertThat(dto.equals(sousClasse)).isFalse();
    }

    // ---------------------------------------------------------
    // CAN EQUAL
    // ---------------------------------------------------------

    @Test
    @DisplayName("canEqual : accepter un DTO et refuser les autres types")
    void canEqual_doitVerifierLeType() {
        ProduitResponseDTO dto = creerDTO();

        assertThat(dto.canEqual(creerDTO())).isTrue();
        assertThat(dto.canEqual(null)).isFalse();
        assertThat(dto.canEqual("Autre type")).isFalse();
    }

    // ---------------------------------------------------------
    // TO STRING
    // ---------------------------------------------------------

    @Test
    @DisplayName("toString : contenir les noms et valeurs des champs")
    void toString_doitContenirTousLesChamps() {
        ProduitResponseDTO dto = creerDTO();

        assertThat(dto.toString())
                .contains(
                        "ProduitResponseDTO",
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
    @DisplayName("toString : accepter les champs null")
    void toString_champsNull_doitFonctionner() {
        ProduitResponseDTO dto = new ProduitResponseDTO();

        assertThat(dto.toString())
                .contains(
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

    private ProduitResponseDTO creerDTO() {
        return new ProduitResponseDTO(
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

    private void verifierChamps(ProduitResponseDTO dto) {
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getNom()).isEqualTo("Laptop Pro");
        assertThat(dto.getDescription())
                .isEqualTo("Ordinateur portable");
        assertThat(dto.getPrix()).isEqualTo(1299.99);
        assertThat(dto.getStock()).isEqualTo(10);
        assertThat(dto.getCategorie()).isEqualTo("Informatique");
        assertThat(dto.getActif()).isTrue();
        assertThat(dto.getDateCreation()).isEqualTo(DATE_CREATION);
    }

    private void modifierChamp(
            ProduitResponseDTO dto,
            String nomChamp,
            Object valeur
    ) throws ReflectiveOperationException {

        Field champ = ProduitResponseDTO.class.getDeclaredField(nomChamp);
        champ.setAccessible(true);
        champ.set(dto, valeur);
    }

    private Object valeurDifferente(String nomChamp) {
        return switch (nomChamp) {
            case "id" -> 2L;
            case "nom" -> "Autre produit";
            case "description" -> "Autre description";
            case "prix" -> 99.90;
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