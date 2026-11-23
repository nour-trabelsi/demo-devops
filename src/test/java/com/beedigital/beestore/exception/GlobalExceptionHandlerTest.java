package com.beedigital.beestore.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler();

    // ---------------------------------------------------------
    // VALIDATION : 400
    // ---------------------------------------------------------

    @Test
    @DisplayName("Validation : retourner 400 et les erreurs par champ")
    void handleValidation_doitRetourner400AvecDetails() {
        BindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "produit");

        bindingResult.addError(new FieldError(
                "produit",
                "nom",
                "Le nom est obligatoire"
        ));

        bindingResult.addError(new FieldError(
                "produit",
                "prix",
                "Le prix doit être positif"
        ));

        MethodArgumentNotValidException exception =
                mock(MethodArgumentNotValidException.class);

        when(exception.getBindingResult()).thenReturn(bindingResult);

        LocalDateTime avant = LocalDateTime.now();

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                handler.handleValidation(exception);

        LocalDateTime apres = LocalDateTime.now();

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);

        GlobalExceptionHandler.ErrorResponse body = response.getBody();

        assertThat(body).isNotNull();
        assertThat(body.status()).isEqualTo(400);
        assertThat(body.message()).isEqualTo("Données invalides");
        assertThat(body.timestamp()).isBetween(avant, apres);

        assertThat(body.details()).isEqualTo(Map.of(
                "nom", "Le nom est obligatoire",
                "prix", "Le prix doit être positif"
        ));
    }

    @Test
    @DisplayName("Validation : conserver le dernier message pour un même champ")
    void handleValidation_plusieursErreursMemeChamp_doitConserverDerniere() {
        BindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "produit");

        bindingResult.addError(new FieldError(
                "produit",
                "nom",
                "Le nom est obligatoire"
        ));

        bindingResult.addError(new FieldError(
                "produit",
                "nom",
                "Le nom doit contenir au moins deux caractères"
        ));

        MethodArgumentNotValidException exception =
                mock(MethodArgumentNotValidException.class);

        when(exception.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                handler.handleValidation(exception);

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);

        GlobalExceptionHandler.ErrorResponse body = response.getBody();

        assertThat(body).isNotNull();
        assertThat(body.status()).isEqualTo(400);
        assertThat(body.message()).isEqualTo("Données invalides");
        assertThat(body.timestamp()).isNotNull();

        assertThat(body.details()).isEqualTo(Map.of(
                "nom",
                "Le nom doit contenir au moins deux caractères"
        ));
    }

    // ---------------------------------------------------------
    // PRODUIT INTROUVABLE : 404
    // ---------------------------------------------------------

    @Test
    @DisplayName("Produit introuvable : retourner 404 et le message métier")
    void handleNotFound_doitRetourner404AvecMessage() {
        String message = "Produit non trouvé avec l'ID : 99";

        ProduitNotFoundException exception =
                new ProduitNotFoundException(message);

        LocalDateTime avant = LocalDateTime.now();

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                handler.handleNotFound(exception);

        LocalDateTime apres = LocalDateTime.now();

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);

        GlobalExceptionHandler.ErrorResponse body = response.getBody();

        assertThat(body).isNotNull();
        assertThat(body.status()).isEqualTo(404);
        assertThat(body.message()).isEqualTo(message);
        assertThat(body.timestamp()).isBetween(avant, apres);
        assertThat(body.details()).isNull();
    }

    // ---------------------------------------------------------
    // ERREUR GENERIQUE : 500
    // ---------------------------------------------------------

    @Test
    @DisplayName("Erreur inattendue : retourner 500 sans exposer le détail interne")
    void handleGeneric_doitRetourner500AvecMessageGenerique() {
        String messageInterne =
                "Connexion impossible : mot de passe interne incorrect";

        Exception exception = new RuntimeException(messageInterne);

        LocalDateTime avant = LocalDateTime.now();

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                handler.handleGeneric(exception);

        LocalDateTime apres = LocalDateTime.now();

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        GlobalExceptionHandler.ErrorResponse body = response.getBody();

        assertThat(body).isNotNull();
        assertThat(body.status()).isEqualTo(500);
        assertThat(body.message()).isEqualTo(
                "Une erreur interne est survenue. Contactez l'administrateur."
        );
        assertThat(body.message()).doesNotContain(messageInterne);
        assertThat(body.timestamp()).isBetween(avant, apres);
        assertThat(body.details()).isNull();
    }
}