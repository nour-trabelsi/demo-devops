package com.beedigital.beestore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProduitControllerTest {

    private static final String BASE_URL = "/api/v1/produits";

    // Identifiants supposés absents de la base de test.
    private static final long ID_INCONNU = Long.MAX_VALUE;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/v1/produits avec des données valides doit retourner 201")
    void creerProduit_donneesValides_doitRetourner201() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(produitJson(
                                "Clavier mécanique",
                                "89.90",
                                25,
                                "Informatique"
                        )))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nom").value("Clavier mécanique"))
                .andExpect(jsonPath("$.prix").value(89.90))
                .andExpect(jsonPath("$.stock").value(25))
                .andExpect(jsonPath("$.categorie").value("Informatique"));
    }

    @Test
    @DisplayName("POST /api/v1/produits avec un nom vide doit retourner 400")
    void creerProduit_nomVide_doitRetourner400() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(produitJson(
                                "",
                                "10.00",
                                1,
                                "Informatique"
                        )))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.nom").exists());
    }

    @Test
    @DisplayName("GET : récupérer tous les produits retourne 200")
    void getTousProduits_doitRetournerLesProduitsCrees() throws Exception {
        long premierId = creerProduit(
                "Produit Liste A",
                "15.00",
                5,
                "ListeTest"
        );

        long secondId = creerProduit(
                "Produit Liste B",
                "25.00",
                8,
                "ListeTest"
        );

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].id").value(
                        hasItem(Math.toIntExact(premierId))
                ))
                .andExpect(jsonPath("$[*].id").value(
                        hasItem(Math.toIntExact(secondId))
                ));
    }

    @Test
    @DisplayName("GET par ID : un produit existant retourne 200")
    void getProduitById_idExistant_doitRetournerProduit() throws Exception {
        long id = creerProduit(
                "Écran test",
                "199.90",
                12,
                "Informatique"
        );

        mockMvc.perform(get(BASE_URL + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nom").value("Écran test"))
                .andExpect(jsonPath("$.prix").value(199.90))
                .andExpect(jsonPath("$.stock").value(12));
    }

    @Test
    @DisplayName("GET par ID : un produit inconnu retourne 404")
    void getProduitById_idInconnu_doitRetourner404() throws Exception {
        mockMvc.perform(get(BASE_URL + "/{id}", ID_INCONNU))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("PUT : modifier un produit existant retourne 200")
    void modifierProduit_donneesValides_doitRetournerProduitModifie()
            throws Exception {

        long id = creerProduit(
                "Souris",
                "19.90",
                50,
                "Informatique"
        );

        mockMvc.perform(put(BASE_URL + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(produitJson(
                                "Souris sans fil",
                                "24.90",
                                40,
                                "Accessoires"
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nom").value("Souris sans fil"))
                .andExpect(jsonPath("$.prix").value(24.90))
                .andExpect(jsonPath("$.stock").value(40))
                .andExpect(jsonPath("$.categorie").value("Accessoires"));

        // Vérifier que la modification est visible à la lecture.
        mockMvc.perform(get(BASE_URL + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Souris sans fil"))
                .andExpect(jsonPath("$.stock").value(40));
    }

    @Test
    @DisplayName("PUT : un nom vide retourne 400")
    void modifierProduit_nomVide_doitRetourner400() throws Exception {
        long id = creerProduit(
                "Produit initial",
                "20.00",
                5,
                "Informatique"
        );

        mockMvc.perform(put(BASE_URL + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(produitJson(
                                "",
                                "20.00",
                                5,
                                "Informatique"
                        )))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.nom").exists());

        // Une requête invalide ne doit pas modifier le produit.
        mockMvc.perform(get(BASE_URL + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Produit initial"));
    }

    @Test
    @DisplayName("PUT : un produit inconnu retourne 404")
    void modifierProduit_idInconnu_doitRetourner404() throws Exception {
        mockMvc.perform(put(BASE_URL + "/{id}", ID_INCONNU)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(produitJson(
                                "Produit absent",
                                "30.00",
                                3,
                                "Informatique"
                        )))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("DELETE : supprimer un produit retourne 204")
    void supprimerProduit_idExistant_doitRetourner204() throws Exception {
        long id = creerProduit(
                "Produit à supprimer",
                "15.00",
                2,
                "Accessoires"
        );

        mockMvc.perform(delete(BASE_URL + "/{id}", id))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        mockMvc.perform(get(BASE_URL + "/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE : un produit inconnu retourne 404")
    void supprimerProduit_idInconnu_doitRetourner404() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/{id}", ID_INCONNU))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("SEARCH : rechercher par catégorie")
    void rechercher_avecCategorie_doitFiltrerParCategorie() throws Exception {
        long attenduId = creerProduit(
                "Produit catégorie cible",
                "50.00",
                10,
                "CategorieCibleIT"
        );

        long excluId = creerProduit(
                "Produit autre catégorie",
                "30.00",
                5,
                "CategorieAutreIT"
        );

        mockMvc.perform(get(BASE_URL + "/search")
                        .param("categorie", "CategorieCibleIT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].id").value(
                        hasItem(Math.toIntExact(attenduId))
                ))
                .andExpect(jsonPath("$[*].id").value(
                        not(hasItem(Math.toIntExact(excluId)))
                ));
    }

    @Test
    @DisplayName("SEARCH : rechercher par nom")
    void rechercher_avecNom_doitFiltrerParNom() throws Exception {
        long attenduId = creerProduit(
                "RechercheNomUniqueIT",
                "70.00",
                4,
                "Informatique"
        );

        long excluId = creerProduit(
                "AutreProduitSansCorrespondanceIT",
                "40.00",
                6,
                "Informatique"
        );

        mockMvc.perform(get(BASE_URL + "/search")
                        .param("nom", "RechercheNomUniqueIT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].id").value(
                        hasItem(Math.toIntExact(attenduId))
                ))
                .andExpect(jsonPath("$[*].id").value(
                        not(hasItem(Math.toIntExact(excluId)))
                ));
    }

    @Test
    @DisplayName("SEARCH : sans filtre, retourner tous les produits")
    void rechercher_sansFiltre_doitRetournerTousLesProduits()
            throws Exception {

        long premierId = creerProduit(
                "Sans filtre A",
                "10.00",
                2,
                "CategorieA"
        );

        long secondId = creerProduit(
                "Sans filtre B",
                "20.00",
                3,
                "CategorieB"
        );

        mockMvc.perform(get(BASE_URL + "/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].id").value(
                        hasItem(Math.toIntExact(premierId))
                ))
                .andExpect(jsonPath("$[*].id").value(
                        hasItem(Math.toIntExact(secondId))
                ));
    }

    @Test
    @DisplayName("SEARCH : la catégorie est prioritaire sur le nom")
    void rechercher_avecCategorieEtNom_doitPrioriserCategorie()
            throws Exception {

        long categorieId = creerProduit(
                "Produit catégorie prioritaire",
                "80.00",
                7,
                "CategoriePrioritaireIT"
        );

        long nomId = creerProduit(
                "NomAutreCategorieIT",
                "90.00",
                9,
                "CategorieSecondaireIT"
        );

        mockMvc.perform(get(BASE_URL + "/search")
                        .param("categorie", "CategoriePrioritaireIT")
                        .param("nom", "NomAutreCategorieIT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id").value(
                        hasItem(Math.toIntExact(categorieId))
                ))
                .andExpect(jsonPath("$[*].id").value(
                        not(hasItem(Math.toIntExact(nomId)))
                ));
    }

    @Test
    @DisplayName("GET rupture-stock : retourner les produits avec stock zéro")
    void getProduitsEnRuptureDeStock_doitRetournerProduitsSansStock()
            throws Exception {

        long ruptureId = creerProduit(
                "Produit en rupture",
                "60.00",
                0,
                "Informatique"
        );

        long disponibleId = creerProduit(
                "Produit disponible",
                "65.00",
                15,
                "Informatique"
        );

        mockMvc.perform(get(BASE_URL + "/rupture-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].id").value(
                        hasItem(Math.toIntExact(ruptureId))
                ))
                .andExpect(jsonPath("$[*].id").value(
                        not(hasItem(Math.toIntExact(disponibleId)))
                ));
    }

    private long creerProduit(
            String nom,
            String prix,
            int stock,
            String categorie
    ) throws Exception {

        String reponse = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(produitJson(nom, prix, stock, categorie)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(reponse).get("id").asLong();
    }

    private String produitJson(
            String nom,
            String prix,
            int stock,
            String categorie
    ) throws Exception {

        return objectMapper.writeValueAsString(Map.of(
                "nom", nom,
                "prix", new BigDecimal(prix),
                "stock", stock,
                "categorie", categorie
        ));
    }
}