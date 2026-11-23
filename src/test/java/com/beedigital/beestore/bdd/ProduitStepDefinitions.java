package com.beedigital.beestore.bdd;
import com.beedigital.beestore.dto.ProduitRequestDTO;
import com.beedigital.beestore.dto.ProduitResponseDTO;
import com.beedigital.beestore.service.ProduitService;
import io.cucumber.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.assertThat;
public class ProduitStepDefinitions {
    @Autowired
    private ProduitService produitService;

    private ProduitRequestDTO requeteEnCours;
    private ProduitResponseDTO produitCree;

    @Given("un produit nommé {string} au prix de {double} avec un stock de {int}")
    public void unProduitAvecCesCaracteristiques(String nom, Double prix, Integer stock) {
        requeteEnCours = new ProduitRequestDTO();
        requeteEnCours.setNom(nom);
        requeteEnCours.setPrix(prix);
        requeteEnCours.setStock(stock);
    }

    @When("je soumets la création du produit")
    public void jeSoumetsLaCreationDuProduit() {
        produitCree = produitService.creerProduit(requeteEnCours);
    }

    @Then("le produit est créé avec succès")
    public void leProduitEstCreeAvecSucces() {
        assertThat(produitCree.getId()).isNotNull();
    }

    @Then("le produit créé a pour nom {string}")
    public void leProduitCreeAPourNom(String nomAttendu) {
        assertThat(produitCree.getNom()).isEqualTo(nomAttendu);
    }
}