
# API Documentation (Resumen rápido)

## Servicios y puertos (por defecto)
- User service: `http://localhost:8082` (endpoints bajo `/api/usuarios`)
- Auth service: `http://localhost:8083` (endpoints bajo `/auth`)
- Productos service: `http://localhost:8081` (endpoints bajo `/api/productos`)

> Ajusta las variables de entorno `PORT` o `USER_SERVICE_URL` según corresponda.

---

## Endpoints importantes

### Auth
- `POST /auth/login`  
  Body JSON: `{ "username": "user@example.com", "password":"tuPassword" }`  
  Respuesta: `{ "token": "JWT" }` (usar para Authorization: Bearer <token>)

### Usuarios (User service)
- `GET /api/usuarios` — Listar usuarios
- `GET /api/usuarios/{id}` — Obtener usuario por id
- `GET /api/usuarios/email/{email}` — Obtener usuario por email
- `POST /api/usuarios` — Registrar usuario  
  Body JSON ejemplo:
```json
{
  "name": "Nombre",
  "email": "mail@example.com",
  "password": "contraseña",
  "role": "USER"
}
```

> Nota: la contraseña se guarda hasheada con BCrypt.

### Productos (Productos service)
- CRUD estándar en `/api/productos` (consultar Swagger UI para detalles).

---

## Swagger / OpenAPI
Cada microservicio que incluye `springdoc-openapi` expone su UI:
- Productos: `http://localhost:8081/swagger-ui.html` o `/swagger-ui/index.html`
- User: `http://localhost:8082/swagger-ui.html`
- Auth: si agregas `springdoc` al módulo Auth, también podrá exponer su UI.

---

## Seguridad
- El flujo de autenticación implementado:
  1. Registrar usuario en el servicio `User` (la contraseña se guarda hasheada).
  2. Llamar a `POST /auth/login` con email y contraseña.
  3. `Auth` consulta al `User` por email y compara la contraseña con BCrypt.
  4. Si es correcto, `Auth` devuelve un JWT firmado (24h).
  5. Usar el JWT en `Authorization: Bearer <token>` para proteger endpoints (se debe implementar un filtro de validación en cada servicio).

---
