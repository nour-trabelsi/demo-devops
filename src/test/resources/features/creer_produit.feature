Feature: Création d'un produit
  En tant que gestionnaire de catalogue
  Je veux créer un nouveau produit
  Afin de le rendre disponible à la vente

  Scenario: Création d'un produit avec des données valides
    Given un produit nommé "Clavier mécanique" au prix de 89.90 avec un stock de 25
    When je soumets la création du produit
    Then le produit est créé avec succès
    And le produit créé a pour nom "Clavier mécanique"