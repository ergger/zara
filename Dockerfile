# tzdata es obligatoria: la imagen alpine no la trae y sin ella la JVM cae a
# UTC ignorando TZ, lo que hace que los tests de zona horaria fallen (el patron
# %d{...XXX} imprime "Z" en vez de "+02:00").
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
ENV TZ=Europe/Madrid
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
COPY pom.xml .
COPY src ./src
RUN mvn clean verify

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Se instala tzdata para que TZ tenga efecto. docker-compose.yml fija TZ,
# pero sin este paquete la JVM la ignora y arranca en UTC.
RUN apk add --no-cache tzdata
ENV TZ=Europe/Madrid
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]