package com.beedigital.beestore;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class BeeStoreApplicationTest {

    @Test
    @DisplayName("Le constructeur permet de créer une instance")
    void constructeur_doitCreerInstance() {
        BeeStoreApplication application = new BeeStoreApplication();

        assertNotNull(application);
    }

    @Test
    @DisplayName("main transmet la classe et les arguments à SpringApplication")
    void main_doitAppelerSpringApplicationRun() {
        String[] args = {"--spring.profiles.active=test"};

        ConfigurableApplicationContext context =
                mock(ConfigurableApplicationContext.class);

        try (MockedStatic<SpringApplication> springApplication =
                     mockStatic(SpringApplication.class)) {

            springApplication
                    .when(() -> SpringApplication.run(
                            BeeStoreApplication.class,
                            args
                    ))
                    .thenReturn(context);

            BeeStoreApplication.main(args);

            springApplication.verify(
                    () -> SpringApplication.run(
                            BeeStoreApplication.class,
                            args
                    ),
                    times(1)
            );

            springApplication.verifyNoMoreInteractions();
        }
    }
}