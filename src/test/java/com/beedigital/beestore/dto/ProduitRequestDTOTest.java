package com.beedigital.beestore.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ProduitRequestDTOTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void initialiserValidation() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void fermerValidation() {
        if (validatorFactory != null) {
            validatorFactory.close();
        }
    }

    // ---------------------------------------------------------
    // CONSTRUCTEUR ET VALEURS PAR DEFAUT
    // ---------------------------------------------------------

    @Test
    @DisplayName("Constructeur : actif vaut true et les autres champs sont null")
    void constructeur_doitInitialiserValeursParDefaut() {
        ProduitRequestDTO dto = new ProduitRequestDTO();

        assertThat(dto.getNom()).isNull();
        assertThat(dto.getDescription()).isNull();
        assertThat(dto.getPrix()).isNull();
        assertThat(dto.getStock()).isNull();
        assertThat(dto.getCategorie()).isNull();
        assertThat(dto.getActif()).isTrue();
    }

    // ---------------------------------------------------------
    // GETTERS ET SETTERS
    // ---------------------------------------------------------

    @Test
    @DisplayName("Getters et setters : conserver toutes les valeurs")
    void gettersEtSetters_doiventConserverValeurs() {
        ProduitRequestDTO dto = creerDTOValide();

        assertThat(dto.getNom()).isEqualTo("Laptop Pro");
        assertThat(dto.getDescription()).isEqualTo("Ordinateur portable");
        assertThat(dto.getPrix()).isEqualTo(1299.99);
        assertThat(dto.getStock()).isEqualTo(10);
        assertThat(dto.getCategorie()).isEqualTo("Informatique");
        assertThat(dto.getActif()).isTrue();

        dto.setActif(false);

        assertThat(dto.getActif()).isFalse();
    }

    @Test
    @DisplayName("Setters : accepter null sans déclencher automatiquement la validation")
    void setters_doiventAccepterNull() {
        ProduitRequestDTO dto = creerDTOValide();

        dto.setNom(null);
        dto.setDescription(null);
        dto.setPrix(null);
        dto.setStock(null);
        dto.setCategorie(null);
        dto.setActif(null);

        assertThat(dto.getNom()).isNull();
        assertThat(dto.getDescription()).isNull();
        assertThat(dto.getPrix()).isNull();
        assertThat(dto.getStock()).isNull();
        assertThat(dto.getCategorie()).isNull();
        assertThat(dto.getActif()).isNull();
    }

    // ---------------------------------------------------------
    // VALIDATION : CAS VALIDES
    // ---------------------------------------------------------

    @Test
    @DisplayName("Validation : accepter un DTO valide")
    void validation_donneesValides_aucuneViolation() {
        assertThat(validator.validate(creerDTOValide())).isEmpty();
    }

    @Test
    @DisplayName("Validation : accepter les champs optionnels à null")
    void validation_champsOptionnelsNull_aucuneViolation() {
        ProduitRequestDTO dto = creerDTOValide();

        dto.setDescription(null);
        dto.setCategorie(null);
        dto.setActif(null);

        assertThat(validator.validate(dto)).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 100})
    @DisplayName("Validation : accepter les longueurs limites du nom")
    void validation_nomAuxLimites_aucuneViolation(int longueur) {
        ProduitRequestDTO dto = creerDTOValide();
        dto.setNom("A".repeat(longueur));

        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    @DisplayName("Validation : accepter les tailles maximales et le stock zéro")
    void validation_limitesValides_aucuneViolation() {
        ProduitRequestDTO dto = creerDTOValide();

        dto.setDescription("D".repeat(500));
        dto.setCategorie("C".repeat(100));
        dto.setPrix(0.01);
        dto.setStock(0);

        assertThat(validator.validate(dto)).isEmpty();
    }

    // ---------------------------------------------------------
    // VALIDATION : CAS INVALIDES
    // ---------------------------------------------------------

    @ParameterizedTest(name = "{0} : {2}")
    @MethodSource("donneesInvalides")
    @DisplayName("Validation : signaler le champ et le message attendus")
    void validation_donneesInvalides_doitSignalerViolation(
            String nomChamp,
            Object valeur,
            String messageAttendu
    ) throws ReflectiveOperationException {

        ProduitRequestDTO dto = creerDTOValide();
        modifierChamp(dto, nomChamp, valeur);

        Set<ConstraintViolation<ProduitRequestDTO>> violations =
                validator.validate(dto);

        assertThat(violations).anySatisfy(violation -> {
            assertThat(violation.getPropertyPath().toString())
                    .isEqualTo(nomChamp);
            assertThat(violation.getMessage()).isEqualTo(messageAttendu);
        });
    }

    private static Stream<Arguments> donneesInvalides() {
        return Stream.of(
                Arguments.of(
                        "nom",
                        null,
                        "Le nom du produit est obligatoire"
                ),
                Arguments.of(
                        "nom",
                        "",
                        "Le nom du produit est obligatoire"
                ),
                Arguments.of(
                        "nom",
                        "   ",
                        "Le nom du produit est obligatoire"
                ),
                Arguments.of(
                        "nom",
                        "A",
                        "Le nom doit contenir entre 2 et 100 caractères"
                ),
                Arguments.of(
                        "nom",
                        "A".repeat(101),
                        "Le nom doit contenir entre 2 et 100 caractères"
                ),
                Arguments.of(
                        "description",
                        "D".repeat(501),
                        "La description ne peut pas dépasser 500 caractères"
                ),
                Arguments.of(
                        "prix",
                        null,
                        "Le prix est obligatoire"
                ),
                Arguments.of(
                        "prix",
                        0.0,
                        "Le prix doit être strictement positif"
                ),
                Arguments.of(
                        "prix",
                        -10.0,
                        "Le prix doit être strictement positif"
                ),
                Arguments.of(
                        "stock",
                        null,
                        "Le stock est obligatoire"
                ),
                Arguments.of(
                        "stock",
                        -1,
                        "Le stock ne peut pas être négatif"
                ),
                Arguments.of(
                        "categorie",
                        "C".repeat(101),
                        "La catégorie ne peut pas dépasser 100 caractères"
                )
        );
    }

    // ---------------------------------------------------------
    // EQUALS ET HASH CODE
    // ---------------------------------------------------------

    @Test
    @DisplayName("Equals : vérifier identité, null et autre type")
    void equals_casDeBase() {
        ProduitRequestDTO dto = creerDTOValide();

        assertThat(dto.equals(dto)).isTrue();
        assertThat(dto.equals(null)).isFalse();
        assertThat(dto.equals("Autre type")).isFalse();
    }

    @Test
    @DisplayName("Equals et hashCode : deux DTO identiques")
    void equalsEtHashCode_memesValeurs() {
        ProduitRequestDTO premier = creerDTOValide();
        ProduitRequestDTO second = creerDTOValide();

        assertThat(premier.equals(second)).isTrue();
        assertThat(second.equals(premier)).isTrue();
        assertThat(premier.hashCode()).isEqualTo(second.hashCode());
    }

    @Test
    @DisplayName("Equals et hashCode : tous les champs à null")
    void equalsEtHashCode_champsNull() {
        ProduitRequestDTO premier = creerDTOVide();
        ProduitRequestDTO second = creerDTOVide();

        assertThat(premier.equals(second)).isTrue();
        assertThat(second.equals(premier)).isTrue();
        assertThat(premier.hashCode()).isEqualTo(second.hashCode());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "nom", "description", "prix", "stock", "categorie", "actif"
    })
    @DisplayName("Equals : chaque champ participe à la comparaison")
    void equals_champDifferent_doitRetournerFalse(String nomChamp)
            throws ReflectiveOperationException {

        ProduitRequestDTO premier = creerDTOValide();
        ProduitRequestDTO second = creerDTOValide();

        modifierChamp(second, nomChamp, valeurDifferente(nomChamp));

        assertThat(premier.equals(second)).isFalse();
        assertThat(second.equals(premier)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "nom", "description", "prix", "stock", "categorie", "actif"
    })
    @DisplayName("Equals : distinguer null d'une valeur renseignée")
    void equals_champNull_doitRetournerFalse(String nomChamp)
            throws ReflectiveOperationException {

        ProduitRequestDTO premier = creerDTOValide();
        ProduitRequestDTO second = creerDTOValide();

        modifierChamp(premier, nomChamp, null);

        assertThat(premier.equals(second)).isFalse();
        assertThat(second.equals(premier)).isFalse();
    }

    @Test
    @DisplayName("Equals : respecter le refus de comparaison d'une sous-classe")
    void equals_canEqualRefuse_doitRetournerFalse() {
        ProduitRequestDTO dto = creerDTOValide();

        ProduitRequestDTO sousClasse = new ProduitRequestDTO() {
            @Override
            protected boolean canEqual(Object other) {
                return false;
            }
        };

        assertThat(dto.equals(sousClasse)).isFalse();
    }

    @Test
    @DisplayName("canEqual : accepter uniquement un ProduitRequestDTO")
    void canEqual_doitVerifierType() {
        ProduitRequestDTO dto = creerDTOValide();

        assertThat(dto.canEqual(creerDTOValide())).isTrue();
        assertThat(dto.canEqual(null)).isFalse();
        assertThat(dto.canEqual("Autre type")).isFalse();
    }

    // ---------------------------------------------------------
    // TO STRING
    // ---------------------------------------------------------

    @Test
    @DisplayName("toString : afficher les champs renseignés")
    void toString_doitContenirLesChamps() {
        ProduitRequestDTO dto = creerDTOValide();

        assertThat(dto.toString()).contains(
                "ProduitRequestDTO",
                "nom=Laptop Pro",
                "description=Ordinateur portable",
                "prix=1299.99",
                "stock=10",
                "categorie=Informatique",
                "actif=true"
        );
    }

    @Test
    @DisplayName("toString : accepter tous les champs à null")
    void toString_champsNull_doitFonctionner() {
        assertThat(creerDTOVide().toString()).contains(
                "nom=null",
                "description=null",
                "prix=null",
                "stock=null",
                "categorie=null",
                "actif=null"
        );
    }

    // ---------------------------------------------------------
    // METHODES UTILITAIRES
    // ---------------------------------------------------------

    private ProduitRequestDTO creerDTOValide() {
        ProduitRequestDTO dto = new ProduitRequestDTO();

        dto.setNom("Laptop Pro");
        dto.setDescription("Ordinateur portable");
        dto.setPrix(1299.99);
        dto.setStock(10);
        dto.setCategorie("Informatique");
        dto.setActif(true);

        return dto;
    }

    private ProduitRequestDTO creerDTOVide() {
        ProduitRequestDTO dto = new ProduitRequestDTO();
        dto.setActif(null);
        return dto;
    }

    private void modifierChamp(
            ProduitRequestDTO dto,
            String nomChamp,
            Object valeur
    ) throws ReflectiveOperationException {

        Field champ = ProduitRequestDTO.class.getDeclaredField(nomChamp);
        champ.setAccessible(true);
        champ.set(dto, valeur);
    }

    private Object valeurDifferente(String nomChamp) {
        return switch (nomChamp) {
            case "nom" -> "Autre produit";
            case "description" -> "Autre description";
            case "prix" -> 49.90;
            case "stock" -> 25;
            case "categorie" -> "Accessoires";
            case "actif" -> false;
            default -> throw new IllegalArgumentException(
                    "Champ inconnu : " + nomChamp
            );
        };
    }
}