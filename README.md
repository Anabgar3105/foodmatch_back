<div align="center">
  
# FoodMatch Backend 🍽️

![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-brightgreen)
![Maven](https://img.shields.io/badge/Maven-4.0.0-red)
![JWT](https://img.shields.io/badge/Security-JWT-orange)
![MySQL](https://img.shields.io/badge/Database-MySQL-blue)
![REST API](https://img.shields.io/badge/API-REST-blueviolet)
</div>

> **API REST potente y segura** que alimenta la experiencia culinaria de FoodMatch. MVP completamente funcional con autenticación JWT, gestión de recetas y sistema de favoritos.

**📱 Frontend:** ¿Buscas la app móvil? Consulta el [README del Frontend](https://github.com/Anabgar3105/foodmatch_app)

**⚠️ Estado:** Actualmente solo funciona **en local**. Consulta la sección de [Instalación](#-instalación-y-ejecución) para configurarlo.

## 🎯 Descripción General

La vida moderna y las limitaciones presupuestarias son barreras significativas para mantener comidas diarias variadas y económicas, generando dependencia de comida rápida o servicios de entrega costosos.

**FoodMatch** aborda este problema desarrollando una aplicación móvil multiplataforma que **conecta a usuarios con poco tiempo o presupuesto ajustado con recetas fáciles, rápidas y económicas**. El objetivo es proporcionar una interfaz intuitiva basada en gestos (swipe cards) que agilice el descubrimiento de recetas, integrado con herramientas de gestión personal.

El backend implementa una **arquitectura Cliente-Servidor robusta**:
- 🔐 Autenticación segura con JWT (tokens stateless)
- 🍳 Catálogo completo de recetas con ingredientes y pasos
- ❤️ Sistema de favoritos para personalizació de experiencia
- 📸 Almacenamiento de imágenes en Cloudinary CDN
- 🔍 Búsqueda y filtrado por categoría y tiempo de preparación
- ⚙️ Endpoints REST para CRUD completo de recetas

## ⭐ Características Implementadas

### 🔐 Autenticación & Usuarios
- ✅ Registro de usuarios con validación de email y username únicos
- ✅ Login con generación de tokens JWT (60 días de validez)
- ✅ Actualización de perfil (nombre, email, avatar)
- ✅ Cambio de contraseña (con verificación de contraseña anterior)
- ✅ Hashing seguro con BCrypt
- ✅ Roles de usuario: USER y ADMIN

### 🍳 Gestión de Recetas
- ✅ Crear recetas con ingredientes y pasos de elaboración
- ✅ Listar todas las recetas disponibles (públicas + propias)
- ✅ Obtener detalles completos de una receta
- ✅ Buscar y filtrar por:
  - 🏷️ Categoría (ENTRANTES, SNACKS, PLATOS_COMPLETOS, POSTRES)
  - ⏱️ Tiempo de preparación máximo
- ✅ Ver solo mis recetas (usuario autenticado)
- ✅ Editar recetas (solo creador)
- ✅ Eliminar recetas (creador o ADMIN)
- ✅ Actualizar imagen de receta

### ❤️ Sistema de Favoritos
- ✅ Guardar recetas como favoritas ("Match")
- ✅ Eliminar de favoritos
- ✅ Listar favoritos con formato de tarjetas para UI
- ✅ Relación many-to-many Usuario-Receta

### 📸 Manejo de Medios
- ✅ Subida de imágenes a Cloudinary
- ✅ Soporte para carpetas: recetas y avatares
- ✅ Validación de tamaño (máx 10MB)
- ✅ Nombrado automático de avatares

## 🏗️ Arquitectura Técnica

### Estructura de Base de Datos

```
users (Usuarios del sistema)
├── id, name, surname1, surname2
├── email (único), username (único)
├── password (BCrypt), role
├── register_date, avatar_url
└── relación 1:N con recipes y M:M con favorites

recipes (Catálogo de recetas)
├── id, title, description
├── preparation_time, category
├── image, user_id (FK)
├── relación 1:N con ingredients
├── relación 1:N con elaboration_steps
└── relación M:M con users (favorites)

ingredients (Ingredientes de receta)
├── id, name, quantity
└── recipe_id (FK)

elaboration_steps (Pasos de elaboración)
├── id, step_number, instruction
└── recipe_id (FK)

favourites (Relación many-to-many)
├── user_id (FK)
└── recipe_id (FK)
```

### Stack Tecnológico

| Componente | Tecnología | Propósito |
|---|---|---|
| **Framework** | Spring Boot 3.2.4 | Desarrollo REST API |
| **ORM** | Spring Data JPA | Persistencia de datos |
| **Seguridad** | Spring Security + JWT | Autenticación stateless |
| **Base de Datos** | MySQL 8+ | Almacenamiento persistente |
| **Storage** | Cloudinary CDN | Hosting de imágenes |
| **Build** | Maven 4.0.0 | Gestión de dependencias |
| **Utilidades** | Lombok | Reducción de boilerplate |

## 📡 Endpoints de la API

### 🔓 Públicos (sin autenticación)

```
POST   /api/users/signup          → Registrar nuevo usuario
POST   /api/users/login           → Iniciar sesión (retorna JWT)
```

### 🔒 Protegidos (requieren JWT válido)

#### Usuarios
```
PUT    /api/users/profile         → Actualizar perfil
PUT    /api/users/password        → Cambiar contraseña
```

#### Recetas
```
GET    /api/recipes               → Listar todas las recetas
GET    /api/recipes/{id}          → Obtener detalles de receta
GET    /api/recipes/my-recipes    → Listar mis recetas
GET    /api/recipes/search        → Buscar con filtros (category, maxTime)
POST   /api/recipes               → Crear nueva receta
PUT    /api/recipes/{id}          → Editar receta (solo creador)
DELETE /api/recipes/{id}          → Eliminar receta (creador o ADMIN)
PATCH  /api/recipes/{id}/image    → Actualizar imagen de receta
```

#### Favoritos
```
GET    /api/favorites             → Listar mis recetas favoritas
POST   /api/favorites/{recipeId}  → Añadir a favoritos
DELETE /api/favorites/{recipeId}  → Eliminar de favoritos
```

#### Medios
```
POST   /api/media/upload          → Subir imagen a Cloudinary
```

## 🔐 Seguridad

- **Autenticación Stateless:** Cada petición valida su JWT token
- **Expiración de Tokens:** 60 días de validez
- **Hashing de Contraseñas:** BCrypt con salt
- **CORS Configurado:** Para comunicación con frontend
- **Endpoints Públicos:** Solo signup y login sin autenticación
- **Control de Acceso:** Usuarios solo pueden editar/eliminar sus propios recursos

## 🛠️ Requisitos Previos

- **JDK 17+** (OpenJDK o Oracle JDK)
- **Maven 3.6+**
- **MySQL 8+** (conexión local o remota)
- **Cloudinary Account** (para image hosting, opcional para desarrollo)

## 🚀 Instalación y Ejecución

### ⚠️ Nota sobre Ambiente

Esta aplicación está configurada para ejecutarse **en ambiente local** únicamente. Si necesitas hacer deploy a producción, requiere configuraciones adicionales de seguridad y escalabilidad.

### 1. Clonar el repositorio
```bash
git clone https://github.com/tu_usuario/FoodMatch.git
cd FoodMatch
```

### 2. Configurar base de datos
```bash
# Crear base de datos
mysql -u root -p
> CREATE DATABASE foodmatch_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Configurar credenciales en `application.properties`
```properties
# Base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/foodmatch_db
spring.datasource.username=root
spring.datasource.password=tu_contraseña

# Cloudinary (opcional)
cloudinary.cloud_name=tu_cloud_name
cloudinary.api_key=tu_api_key
cloudinary.api_secret=tu_api_secret

# JWT
jwt.secret=tu_secret_muy_largo_y_seguro
jwt.expiration=5184000000
```

### 4. Instalar dependencias
```bash
mvn clean install
```

### 5. Ejecutar la aplicación
```bash
mvn spring-boot:run
```

La API estará disponible en: **http://localhost:8080**

## 📚 Documentación API

La documentación Swagger está disponible en:
```
http://localhost:8080/swagger-ui/
```

## 🧪 Testing

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar tests de un módulo específico
mvn test -Dtest=UserControllerTest
```

## 📁 Estructura de Carpetas

```
src/main/java/edu/abga/foodmatch/
├── FoodMatchApplication.java      # Clase principal
├── config/                         # Configuración (JWT, Security, Swagger)
├── controller/                     # Endpoints REST
├── service/                        # Lógica de negocio
├── repository/                     # Acceso a datos (JPA)
├── model/                          # Entidades JPA
├── exception/                      # Manejo de excepciones personalizado
├── security/                       # Configuración JWT y seguridad
└── util/                           # Utilidades
```

## 📦 Dependencias Principales

```xml
<!-- Spring Boot -->
<artifactId>spring-boot-starter-web</artifactId>
<artifactId>spring-boot-starter-data-jpa</artifactId>
<artifactId>spring-boot-starter-security</artifactId>

<!-- Base de Datos -->
<artifactId>mysql-connector-java</artifactId>

<!-- JWT -->
<artifactId>jjwt</artifactId>

<!-- Lombok -->
<artifactId>lombok</artifactId>

<!-- Cloudinary -->
<artifactId>cloudinary-http44</artifactId>

<!-- Swagger/OpenAPI -->
<artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
```

## 🤝 Integración con Frontend

La aplicación Flutter (`foodmatch_app`) consume esta API. El cliente debe:

1. Registrarse/Login para obtener JWT token
2. Incluir token en header: `Authorization: Bearer {token}`
3. Manejar refresh de token antes de expiración (60 días)
4. Usar endpoints de búsqueda para la funcionalidad swipe

## 📋 Notas sobre el MVP

✅ **Implementado:**
- Autenticación JWT con validación de tokens
- CRUD completo de recetas
- Sistema de favoritos funcional
- Búsqueda y filtrado
- Manejo de imágenes con Cloudinary
- Validaciones de entrada
- Manejo de errores personalizado

⚠️ **No incluido en MVP:**
- Paginación automática (implementable fácilmente)
- Búsqueda por título
- Endpoint de borrar usuario
- Gestión avanzada de administrador
- Notificaciones push (config existe, no activado)

## 📄 Licencia

Este proyecto es parte del Trabajo de Fin de Ciclo (TFC).


