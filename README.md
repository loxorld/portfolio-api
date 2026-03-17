# Portfolio API

Backend del portfolio personal. Expone endpoints publicos para listar proyectos
y endpoints admin protegidos por token para gestionarlos desde el panel web.

Repositorio frontend: https://github.com/loxorld/portfolio-web

## Stack

- Java 21
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- Spring Security
- PostgreSQL
- Maven

## Endpoints principales

| Metodo | Endpoint | Uso |
| --- | --- | --- |
| GET | `/api/projects` | Lista proyectos publicados |
| GET | `/api/projects/{slug}` | Devuelve el detalle de un proyecto publicado |
| GET | `/api/admin/projects` | Lista proyectos para el panel admin |
| GET | `/api/admin/projects/{slug}` | Devuelve el detalle completo para edicion |
| POST | `/api/admin/projects` | Crea un proyecto |
| PUT | `/api/admin/projects/{slug}` | Actualiza un proyecto |
| DELETE | `/api/admin/projects/{slug}` | Elimina un proyecto |

Swagger UI: `http://localhost:8085/swagger`
OpenAPI: `http://localhost:8085/api-docs`

## Desarrollo local

Requisitos:

- Java 21
- PostgreSQL

Configuracion minima:

1. Crear una base llamada `portfolio`.
2. Ejecutar la app con el perfil `dev`.
3. Definir `ADMIN_TOKEN` si vas a usar el panel admin.

Inicio rapido:

```bash
./mvnw spring-boot:run
```

En desarrollo la API queda disponible en `http://localhost:8085`.
