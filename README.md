# README — BACKEND (`portfolio-api`)

Copiá **todo esto** en `portfolio-api/README.md`

```md
# Portfolio API

API REST desarrollada en Java con Spring Boot.  
Provee los datos utilizados por el frontend de mi portfolio profesional.

🔗 **Repositorio frontend:**  
https://github.com/loxorld/portfolio-web

---

## ✨ Características

- API REST pública
- Listado de proyectos publicados
- Detalle de proyecto por slug
- Validaciones y manejo global de errores
- Documentación con Swagger
- Persistencia con PostgreSQL

---

## 🧱 Stack

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- PostgreSQL
- Maven

---

## 📡 Endpoints principales

| Método | Endpoint | Descripción |
|------|--------|------------|
| GET | `/api/projects` | Lista de proyectos publicados |
| GET | `/api/projects/{slug}` | Detalle de un proyecto |

---

## 📄 Documentación API

Swagger UI: http://localhost:8085/swagger
OpenAPI: http://localhost:8085/api-docs

---

## 🛠️ Desarrollo local (opcional)

### Requisitos
- Java 21
- Maven
- PostgreSQL

### Base de datos

Crear una base de datos llamada `portfolio`.

Ejemplo:
```sql
CREATE DATABASE portfolio;

📌 Notas

Esta API es consumida por un frontend desarrollado en Next.js.
El objetivo del proyecto es demostrar arquitectura backend, buenas prácticas y exposición de datos para un portfolio profesional.