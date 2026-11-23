# Image de base avec JRE 17
FROM eclipse-temurin:17-jre-jammy
# Métadonnées
LABEL maintainer="contact@beedigital.tn"
LABEL version="1.0"
LABEL description="BeeStore - API de gestion de produits"
# Répertoire de travail
WORKDIR /app
# Copier le JAR déjà construit par Maven
COPY target/beestore-1.0.0.jar app.jar
# Port exposé (documentation, ne publie rien à lui seul)
EXPOSE 8080
RUN apt-get update && apt-get install -y --no-install-recommends curl \
&& rm -rf /var/lib/apt/lists/*
HEALTHCHECK --interval=30s --timeout=3s --start-period=20s --retries=3 \
CMD curl -f http://localhost:8080/actuator/health || exit 1
# Commande de démarrage
ENTRYPOINT ["java", "-jar", "app.jar"]