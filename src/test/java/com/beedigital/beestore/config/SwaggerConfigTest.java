package com.beedigital.beestore.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SwaggerConfigTest {

    @Test
    @DisplayName("customOpenAPI : configurer les informations de BeeStore API")
    void customOpenAPI_doitConfigurerInformationsEtLicence() {
        // GIVEN
        SwaggerConfig configuration = new SwaggerConfig();

        // WHEN
        OpenAPI openAPI = configuration.customOpenAPI();

        // THEN
        assertThat(openAPI).isNotNull();

        Info info = openAPI.getInfo();

        assertThat(info).isNotNull();
        assertThat(info.getTitle()).isEqualTo("BeeStore API");
        assertThat(info.getDescription()).isEqualTo(
                "API REST de gestion des produits"
                        + " - Formation DevOps avec Spring Boot"
        );
        assertThat(info.getVersion()).isEqualTo("1.0.0");

        License license = info.getLicense();

        assertThat(license).isNotNull();
        assertThat(license.getName()).isEqualTo("MIT");
    }
}