package com.beedigital.beestore.service;

import com.beedigital.beestore.dto.ProduitRequestDTO;
import com.beedigital.beestore.dto.ProduitResponseDTO;
import com.beedigital.beestore.entity.Produit;
import com.beedigital.beestore.exception.ProduitNotFoundException;
import com.beedigital.beestore.repository.ProduitRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProduitServiceImplTest {

    @Mock
    private ProduitRepository produitRepository;

    @InjectMocks
    private ProduitServiceImpl produitService;

    // ---------------------------------------------------------
    // CREATION
    // ---------------------------------------------------------

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @DisplayName("Création : sauvegarder les champs et conserver actif")
    void creerProduit_actifRenseigne_doitSauvegarderEtRetournerProduit(
            Boolean actif
    ) {
        ProduitRequestDTO request = creerRequest(actif);

        when(produitRepository.save(any(Produit.class)))
                .thenAnswer(invocation -> {
                    Produit produit = invocation.getArgument(0);
                    produit.setId(1L);
                    return produit;
                });

        ProduitResponseDTO resultat =
                produitService.creerProduit(request);

        ArgumentCaptor<Produit> captor =
                ArgumentCaptor.forClass(Produit.class);

        verify(produitRepository).save(captor.capture());
        verifyNoMoreInteractions(produitRepository);

        Produit sauvegarde = captor.getValue();

        assertThat(sauvegarde.getId()).isEqualTo(1L);
        assertThat(sauvegarde.getNom()).isEqualTo(request.getNom());
        assertThat(sauvegarde.getDescription())
                .isEqualTo(request.getDescription());
        assertThat(sauvegarde.getPrix()).isEqualTo(request.getPrix());
        assertThat(sauvegarde.getStock()).isEqualTo(request.getStock());
        assertThat(sauvegarde.getCategorie())
                .isEqualTo(request.getCategorie());
        assertThat(sauvegarde.getActif()).isEqualTo(actif);

        verifierMapping(resultat, sauvegarde);
    }

    @Test
    @DisplayName("Création : utiliser actif=true si actif est null")
    void creerProduit_actifNull_doitUtiliserTrueParDefaut() {
        ProduitRequestDTO request = creerRequest(null);

        when(produitRepository.save(any(Produit.class)))
                .thenAnswer(invocation -> {
                    Produit produit = invocation.getArgument(0);
                    produit.setId(1L);
                    return produit;
                });

        ProduitResponseDTO resultat =
                produitService.creerProduit(request);

        ArgumentCaptor<Produit> captor =
                ArgumentCaptor.forClass(Produit.class);

        verify(produitRepository).save(captor.capture());
        verifyNoMoreInteractions(produitRepository);

        Produit sauvegarde = captor.getValue();

        assertThat(sauvegarde.getActif()).isTrue();
        assertThat(resultat.getActif()).isTrue();

        verifierMapping(resultat, sauvegarde);
    }

    // ---------------------------------------------------------
    // LECTURE DE TOUS LES PRODUITS
    // ---------------------------------------------------------

    @Test
    @DisplayName("Lecture : retourner tous les produits mappés en DTO")
    void getTousProduits_doitRetournerListeDeDTO() {
        Produit premier = creerProduit(1L, "Laptop Pro", 10, true);
        Produit second = creerProduit(2L, "Clavier", 5, false);

        when(produitRepository.findAll())
                .thenReturn(List.of(premier, second));

        List<ProduitResponseDTO> resultat =
                produitService.getTousProduits();

        assertThat(resultat).hasSize(2);

        verifierMapping(resultat.get(0), premier);
        verifierMapping(resultat.get(1), second);

        verify(produitRepository).findAll();
        verifyNoMoreInteractions(produitRepository);
    }

    @Test
    @DisplayName("Lecture : retourner une liste vide si aucun produit")
    void getTousProduits_aucunProduit_doitRetournerListeVide() {
        when(produitRepository.findAll()).thenReturn(List.of());

        List<ProduitResponseDTO> resultat =
                produitService.getTousProduits();

        assertThat(resultat).isEmpty();

        verify(produitRepository).findAll();
        verifyNoMoreInteractions(produitRepository);
    }

    // ---------------------------------------------------------
    // LECTURE PAR ID
    // ---------------------------------------------------------

    @Test
    @DisplayName("Lecture par ID : retourner le produit existant")
    void getProduitById_produitExistant_doitRetournerProduit() {
        Produit produit = creerProduit(1L, "Laptop Pro", 10, true);

        when(produitRepository.findById(1L))
                .thenReturn(Optional.of(produit));

        ProduitResponseDTO resultat =
                produitService.getProduitById(1L);

        verifierMapping(resultat, produit);

        verify(produitRepository).findById(1L);
        verifyNoMoreInteractions(produitRepository);
    }

    @Test
    @DisplayName("Lecture par ID : lever une exception si le produit est absent")
    void getProduitById_produitAbsent_doitLeverException() {
        Long id = 99L;

        when(produitRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> produitService.getProduitById(id))
                .isInstanceOf(ProduitNotFoundException.class)
                .hasMessage("Produit non trouvé avec l'ID : " + id);

        verify(produitRepository).findById(id);
        verifyNoMoreInteractions(produitRepository);
    }

    // ---------------------------------------------------------
    // MODIFICATION
    // ---------------------------------------------------------

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @DisplayName("Modification : mettre à jour les champs et actif")
    void modifierProduit_actifRenseigne_doitMettreAJourProduit(
            Boolean actif
    ) {
        Long id = 1L;

        // Partir de la valeur opposée pour vérifier le changement.
        Produit existant = creerProduit(
                id,
                "Ancien nom",
                2,
                !actif
        );

        ProduitRequestDTO request = creerRequest(actif);

        when(produitRepository.findById(id))
                .thenReturn(Optional.of(existant));

        when(produitRepository.save(any(Produit.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProduitResponseDTO resultat =
                produitService.modifierProduit(id, request);

        ArgumentCaptor<Produit> captor =
                ArgumentCaptor.forClass(Produit.class);

        verify(produitRepository).findById(id);
        verify(produitRepository).save(captor.capture());
        verifyNoMoreInteractions(produitRepository);

        Produit sauvegarde = captor.getValue();

        assertThat(sauvegarde).isSameAs(existant);
        assertThat(sauvegarde.getId()).isEqualTo(id);
        assertThat(sauvegarde.getNom()).isEqualTo(request.getNom());
        assertThat(sauvegarde.getDescription())
                .isEqualTo(request.getDescription());
        assertThat(sauvegarde.getPrix()).isEqualTo(request.getPrix());
        assertThat(sauvegarde.getStock()).isEqualTo(request.getStock());
        assertThat(sauvegarde.getCategorie())
                .isEqualTo(request.getCategorie());
        assertThat(sauvegarde.getActif()).isEqualTo(actif);

        verifierMapping(resultat, sauvegarde);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @DisplayName("Modification : conserver actif si le DTO contient null")
    void modifierProduit_actifNull_doitConserverValeurExistante(
            Boolean actifInitial
    ) {
        Long id = 1L;

        Produit existant = creerProduit(
                id,
                "Ancien nom",
                2,
                actifInitial
        );

        ProduitRequestDTO request = creerRequest(null);

        when(produitRepository.findById(id))
                .thenReturn(Optional.of(existant));

        when(produitRepository.save(any(Produit.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProduitResponseDTO resultat =
                produitService.modifierProduit(id, request);

        assertThat(existant.getActif()).isEqualTo(actifInitial);
        assertThat(existant.getNom()).isEqualTo(request.getNom());
        assertThat(existant.getDescription())
                .isEqualTo(request.getDescription());
        assertThat(existant.getPrix()).isEqualTo(request.getPrix());
        assertThat(existant.getStock()).isEqualTo(request.getStock());
        assertThat(existant.getCategorie())
                .isEqualTo(request.getCategorie());

        verifierMapping(resultat, existant);

        verify(produitRepository).findById(id);
        verify(produitRepository).save(existant);
        verifyNoMoreInteractions(produitRepository);
    }

    @Test
    @DisplayName("Modification : lever une exception si le produit est absent")
    void modifierProduit_produitAbsent_doitLeverException() {
        Long id = 99L;
        ProduitRequestDTO request = creerRequest(true);

        when(produitRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> produitService.modifierProduit(id, request)
        )
                .isInstanceOf(ProduitNotFoundException.class)
                .hasMessage("Produit non trouvé avec l'ID : " + id);

        verify(produitRepository).findById(id);
        verify(produitRepository, never()).save(any(Produit.class));
        verifyNoMoreInteractions(produitRepository);
    }

    // ---------------------------------------------------------
    // SUPPRESSION
    // ---------------------------------------------------------

    @Test
    @DisplayName("Suppression : supprimer le produit existant")
    void supprimerProduit_produitExistant_doitSupprimerProduit() {
        Long id = 1L;

        when(produitRepository.existsById(id)).thenReturn(true);

        produitService.supprimerProduit(id);

        verify(produitRepository).existsById(id);
        verify(produitRepository).deleteById(id);
        verifyNoMoreInteractions(produitRepository);
    }

    @Test
    @DisplayName("Suppression : lever une exception si le produit est absent")
    void supprimerProduit_produitAbsent_doitLeverException() {
        Long id = 42L;

        when(produitRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> produitService.supprimerProduit(id))
                .isInstanceOf(ProduitNotFoundException.class)
                .hasMessage("Produit non trouvé avec l'ID : " + id);

        verify(produitRepository).existsById(id);
        verify(produitRepository, never()).deleteById(any());
        verifyNoMoreInteractions(produitRepository);
    }

    // ---------------------------------------------------------
    // RECHERCHE PAR CATEGORIE
    // ---------------------------------------------------------

    @Test
    @DisplayName("Recherche par catégorie : retourner les produits en DTO")
    void rechercherParCategorie_doitRetournerListeDeDTO() {
        String categorie = "Informatique";

        Produit produit = creerProduit(1L, "Laptop Pro", 10, true);

        when(produitRepository.findByCategorieIgnoreCase(categorie))
                .thenReturn(List.of(produit));

        List<ProduitResponseDTO> resultat =
                produitService.rechercherParCategorie(categorie);

        assertThat(resultat).hasSize(1);
        verifierMapping(resultat.get(0), produit);

        verify(produitRepository).findByCategorieIgnoreCase(categorie);
        verifyNoMoreInteractions(produitRepository);
    }

    @Test
    @DisplayName("Recherche par catégorie : retourner une liste vide")
    void rechercherParCategorie_aucunResultat_doitRetournerListeVide() {
        String categorie = "Inconnue";

        when(produitRepository.findByCategorieIgnoreCase(categorie))
                .thenReturn(List.of());

        List<ProduitResponseDTO> resultat =
                produitService.rechercherParCategorie(categorie);

        assertThat(resultat).isEmpty();

        verify(produitRepository).findByCategorieIgnoreCase(categorie);
        verifyNoMoreInteractions(produitRepository);
    }

    // ---------------------------------------------------------
    // RECHERCHE PAR NOM
    // ---------------------------------------------------------

    @Test
    @DisplayName("Recherche par nom : retourner les produits en DTO")
    void rechercherParNom_doitRetournerListeDeDTO() {
        String keyword = "Laptop";

        Produit produit = creerProduit(1L, "Laptop Pro", 10, true);

        when(produitRepository.findByNomContainingIgnoreCase(keyword))
                .thenReturn(List.of(produit));

        List<ProduitResponseDTO> resultat =
                produitService.rechercherParNom(keyword);

        assertThat(resultat).hasSize(1);
        verifierMapping(resultat.get(0), produit);

        verify(produitRepository)
                .findByNomContainingIgnoreCase(keyword);
        verifyNoMoreInteractions(produitRepository);
    }

    @Test
    @DisplayName("Recherche par nom : retourner une liste vide")
    void rechercherParNom_aucunResultat_doitRetournerListeVide() {
        String keyword = "Introuvable";

        when(produitRepository.findByNomContainingIgnoreCase(keyword))
                .thenReturn(List.of());

        List<ProduitResponseDTO> resultat =
                produitService.rechercherParNom(keyword);

        assertThat(resultat).isEmpty();

        verify(produitRepository)
                .findByNomContainingIgnoreCase(keyword);
        verifyNoMoreInteractions(produitRepository);
    }

    // ---------------------------------------------------------
    // RUPTURE DE STOCK
    // ---------------------------------------------------------

    @Test
    @DisplayName("Rupture de stock : demander les produits à stock zéro")
    void getProduitsEnRuptureDeStock_doitRetournerListeDeDTO() {
        Produit produit = creerProduit(1L, "Souris", 0, true);

        when(produitRepository.findByStock(0))
                .thenReturn(List.of(produit));

        List<ProduitResponseDTO> resultat =
                produitService.getProduitsEnRuptureDeStock();

        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getStock()).isZero();

        verifierMapping(resultat.get(0), produit);

        verify(produitRepository).findByStock(0);
        verifyNoMoreInteractions(produitRepository);
    }

    @Test
    @DisplayName("Rupture de stock : retourner une liste vide")
    void getProduitsEnRuptureDeStock_aucunResultat_doitRetournerListeVide() {
        when(produitRepository.findByStock(0))
                .thenReturn(List.of());

        List<ProduitResponseDTO> resultat =
                produitService.getProduitsEnRuptureDeStock();

        assertThat(resultat).isEmpty();

        verify(produitRepository).findByStock(0);
        verifyNoMoreInteractions(produitRepository);
    }

    // ---------------------------------------------------------
    // MAPPING DES VALEURS OPTIONNELLES
    // ---------------------------------------------------------

    @ParameterizedTest
    @NullSource
    @ValueSource(booleans = {true, false})
    @DisplayName("Mapping : restituer la valeur actif de l'entité")
    void getProduitById_doitRestituerActifDeLEntite(Boolean actif) {
        Produit produit = creerProduit(1L, "Produit test", 5, actif);

        when(produitRepository.findById(1L))
                .thenReturn(Optional.of(produit));

        ProduitResponseDTO resultat =
                produitService.getProduitById(1L);

        assertThat(resultat.getActif()).isEqualTo(actif);
        verifierMapping(resultat, produit);

        verify(produitRepository).findById(1L);
        verifyNoMoreInteractions(produitRepository);
    }

    // ---------------------------------------------------------
    // METHODES UTILITAIRES
    // ---------------------------------------------------------

    private ProduitRequestDTO creerRequest(Boolean actif) {
        ProduitRequestDTO request = new ProduitRequestDTO();

        request.setNom("Laptop Pro");
        request.setDescription("Ordinateur portable professionnel");
        request.setPrix(1299.99);
        request.setStock(10);
        request.setCategorie("Informatique");
        request.setActif(actif);

        return request;
    }

    private Produit creerProduit(
            Long id,
            String nom,
            int stock,
            Boolean actif
    ) {
        return Produit.builder()
                .id(id)
                .nom(nom)
                .description("Description du produit")
                .prix(1299.99)
                .stock(stock)
                .categorie("Informatique")
                .actif(actif)
                .build();
    }

    private void verifierMapping(
            ProduitResponseDTO resultat,
            Produit produit
    ) {
        assertThat(resultat).isNotNull();
        assertThat(resultat.getId()).isEqualTo(produit.getId());
        assertThat(resultat.getNom()).isEqualTo(produit.getNom());
        assertThat(resultat.getDescription())
                .isEqualTo(produit.getDescription());
        assertThat(resultat.getPrix()).isEqualTo(produit.getPrix());
        assertThat(resultat.getStock()).isEqualTo(produit.getStock());
        assertThat(resultat.getCategorie())
                .isEqualTo(produit.getCategorie());
        assertThat(resultat.getActif()).isEqualTo(produit.getActif());
        assertThat(resultat.getDateCreation())
                .isEqualTo(produit.getDateCreation());
    }
}