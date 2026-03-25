# FoodMatch 🍽️

![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-brightgreen)
![Maven](https://img.shields.io/badge/Maven-4.0.0-red)

**⚠️ Proyecto en Construcción ⚠️**

> Este proyecto es la parte del **backend** de FoodMatch y se encuentra en desarrollo activo. Futuras actualizaciones incluirán más endpoints, implementación de seguridad con JWT y funcionalidades adicionales para dar soporte a la aplicación móvil.

## 📜 Sobre el Proyecto

FoodMatch es un sistema integral de software diseñado para los amantes de la cocina, que combina una API REST potente con una aplicación móvil intuitiva.

El objetivo es transformar el descubrimiento de recetas en una experiencia ágil e interactiva, basada en un sistema de tarjetas visuales (swipe cards):

*   **Mecánica Swipe:** Los usuarios pueden deslizar para indicar si les gusta o no una receta, creando una lista de favoritos personalizada.
*   **Descubrimiento y Cocina:** Explora recetas guardadas con información detallada y un modo de cocinado "paso a paso".
*   **Comunidad:** Los usuarios registrados pueden subir sus propias recetas y gestionar su perfil.

## 🏛️ Arquitectura

El sistema se compone de dos partes principales:

1.  **Backend (Este Repositorio):** Una API REST desarrollada con **Spring Boot** que se encarga de toda la lógica de negocio, la gestión de datos y la seguridad.
2.  **Frontend (App Móvil):** Una aplicación desarrollada en **Flutter** que consume esta API para ofrecer una experiencia de usuario fluida e interactiva.

## ✨ Características Actuales del Backend

*   **Gestión de usuarios:** Endpoints para registro e inicio de sesión.
*   **Catálogo de recetas:** Operaciones CRUD para recetas, incluyendo ingredientes y pasos de elaboración.
*   **Búsqueda y filtrado:** Búsqueda de recetas por categoría y tiempo de preparación.

## 🔮 Próximas Características

*   **Seguridad con JWT:** Implementación de JSON Web Tokens para proteger los endpoints.
*   **Gestión de Favoritos:** Endpoints para que los usuarios guarden y gestionen sus recetas favoritas.
*   **Subida de Recetas:** Funcionalidad para que los usuarios aporten sus propias recetas.
*   **Paginación y Búsquedas Avanzadas:** Mejoras en la consulta de datos para un rendimiento óptimo.

## 🛠️ Construido Con

*   **Spring Boot:** Framework principal de la aplicación.
*   **Spring Data JPA:** Para la persistencia de datos.
*   **Spring Security:** Para la futura implementación de seguridad.
*   **H2 Database:** Base de datos en memoria para desarrollo y pruebas.
*   **Maven:** Gestor de dependencias y construcción.
*   **Lombok:** Para reducir el código repetitivo (getters, setters, constructores, etc.).

## 🚀 Cómo Empezar

Para obtener una copia local del backend y ponerla en funcionamiento:

### ✅ Prerrequisitos

*   JDK 17 o superior.
*   Maven 3.6 o superior.

### ⚙️ Instalación

1.  Clona el repositorio:
    ```sh
    git clone https://github.com/tu_usuario/FoodMatch.git
    ```
2.  Navega al directorio del proyecto:
    ```sh
    cd FoodMatch
    ```
3.  Instala las dependencias:
    ```sh
    mvn install
    ```
4.  Ejecuta la aplicación:
    ```sh
    mvn spring-boot:run
    ```

La API estará disponible en `http://localhost:8080`.

## 📡 Endpoints de la API (Actuales)

### Usuarios

*   `POST /api/users/signup`: Registrar un nuevo usuario.
*   `POST /api/users/login`: Iniciar sesión.

### Recetas

*   `POST /api/recipes`: Crear una nueva receta.
*   `GET /api/recipes`: Obtener todas las recetas.
*   `GET /api/recipes/search`: Buscar recetas por filtros.


