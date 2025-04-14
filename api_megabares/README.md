# Proyecto Spring Boot con Vaadin

Este es un proyecto basado en **Spring Boot** y **Vaadin**, que proporciona una aplicación web con una interfaz rica y dinámica sin necesidad de escribir código JavaScript.

## Tecnologías utilizadas

- **Java 17+**
- **Spring Boot 3+**
- **Vaadin 24+**
- **Maven o Gradle**
- **H2 / PostgreSQL / MySQL (según configuración)**
- **Lombok (opcional)**

## Requisitos previos

Antes de ejecutar el proyecto, asegúrate de tener instalado:

- **Java 17 o superior**
- **Maven 3+** o **Gradle 7+**
- **Docker (opcional, si deseas ejecutar la base de datos en contenedor)**

## Instalación y ejecución

### 1. Clonar el repositorio
```sh
 git clone https://github.com/fdieguez/api_megabares
 cd proyecto-springboot-vaadin
```

### 2. Configurar el entorno
Crea un archivo `application.properties` o `application.yml` en `src/main/resources/` con la configuración de la base de datos:
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```

### 3. Ejecutar el proyecto con Maven
```sh
 mvn spring-boot:run
```

O con Gradle:
```sh
 ./gradlew bootRun
```

### 4. Acceder a la aplicación
Una vez iniciado el proyecto, accede a la aplicación en:
```
http://localhost:8080
```


## Creación de una Vista en Vaadin
Un ejemplo básico de una vista en Vaadin:
```java
@Route("/")
@PageTitle("Inicio")
public class MainView extends VerticalLayout {
    public MainView() {
        add(new H1("¡Bienvenido a la aplicación con Vaadin y Spring Boot!"));
    }
}
```

## Ejecutar con Docker
ir a path donde esta docker-compose.yml
ejecutar el comando docker-compose build
luego docker-compose up

## Desplegar en producción
Para generar un **JAR ejecutable**, usa:
```sh
mvn clean package
java -jar target/proyecto.jar
```




