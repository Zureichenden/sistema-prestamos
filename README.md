Aquí está el `README.md` completo y actualizado:

```markdown
# 🏦 Sistema de Préstamos

API REST desarrollada con Spring Boot para gestión de préstamos financieros con frontend en React.

## Tecnologías

### Backend
- Java 17
- Spring Boot 4.x
- Spring Security + JWT
- PostgreSQL
- JPA / Hibernate
- Lombok
- Swagger UI
- JUnit 5 + Mockito
- iTextPDF
- Apache POI (Excel)
- JavaMailSender

### Frontend
- React 18
- React Router DOM
- Axios
- CSS Modules

## Módulos
- ✅ Autenticación con JWT
- ✅ Gestión de usuarios y roles (ADMIN, GESTOR, AUDITOR, VIEWER)
- ✅ Clientes con email de bienvenida
- ✅ Préstamos con cálculo automático de amortizaciones
- ✅ Flujo de contrato: generación de PDF → firma → subida → registro
- ✅ Email de confirmación de préstamo con PDF adjunto
- ✅ Pagos con email de confirmación
- ✅ Exportar tabla de amortización a PDF y Excel
- ✅ Bitácora de acciones del sistema
- ✅ Reportes por rango de fechas
- ✅ Paginación en todos los módulos
- ✅ Pruebas unitarias con JUnit 5 + Mockito

## Instalación

### Requisitos
- Java 17+
- PostgreSQL
- Maven
- Node.js 18+

### Backend

1. Clona el repositorio:
```bash
git clone https://github.com/TU_USUARIO/sistema-prestamos.git
cd sistema-prestamos
```

2. Crea la base de datos:
```sql
CREATE DATABASE sistema_prestamos;
```

3. Copia el archivo de ejemplo:
```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

4. Crea el archivo `src/main/resources/application-local.properties`:
```properties
DB_URL=jdbc:postgresql://localhost:5432/sistema_prestamos
DB_USERNAME=postgres
DB_PASSWORD=tu_password
JWT_SECRET=tu_clave_secreta_minimo_256_bits
JWT_EXPIRATION=86400000
MAIL_USERNAME=tu_correo@gmail.com
MAIL_PASSWORD=tu_app_password_gmail
UPLOAD_DIR=uploads/contratos
```
> ⚠️ Este archivo está en `.gitignore` y nunca debe subirse al repositorio.

5. Ejecuta el proyecto:
```bash
mvn spring-boot:run
```

6. Swagger UI disponible en:
```
http://localhost:8080/swagger-ui/index.html
```

### Frontend

1. Clona el repositorio del frontend:
```bash
git clone https://github.com/TU_USUARIO/sistema-prestamos-frontend.git
cd sistema-prestamos-frontend
```

2. Instala dependencias:
```bash
npm install
```

3. Ejecuta:
```bash
npm start
```

4. Abre en el navegador:
```
http://localhost:3000
```

## Usuarios por defecto

| Usuario | Contraseña | Rol |
|---------|------------|-----|
| admin | admin123 | ADMIN |

## Roles del sistema

| Rol | Clientes | Préstamos | Pagos | Reportes | Bitácora | Config |
|-----|----------|-----------|-------|----------|----------|--------|
| ADMIN | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| GESTOR | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ |
| AUDITOR | ❌ | ❌ | ❌ | ✅ | ✅ | ❌ |
| VIEWER | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |

## Flujo de préstamo

```
1. Registrar cliente → recibe email de bienvenida
2. Llenar datos del préstamo
3. Generar PDF de solicitud con tabla de amortización y sección de firmas
4. Cliente firma el PDF físicamente
5. Subir PDF firmado al sistema
6. Confirmar y guardar préstamo → cliente recibe email con PDF adjunto
7. Registrar pagos → cliente recibe email de confirmación por cada pago
```

## Estructura del proyecto

```
src/
├── main/
│   ├── java/com/prestamos/sistema_prestamos/
│   │   ├── config/          ← Inicialización de datos
│   │   ├── controller/      ← Endpoints REST
│   │   ├── dto/             ← Objetos de transferencia
│   │   ├── entity/          ← Modelos de base de datos
│   │   ├── repository/      ← Acceso a datos JPA
│   │   ├── security/        ← JWT y Spring Security
│   │   └── service/         ← Lógica de negocio
│   └── resources/
│       ├── application.properties          ← Configuración general
│       ├── application.properties.example  ← Ejemplo sin credenciales
│       └── application-local.properties    ← Credenciales locales (ignorado por git)
└── test/
    └── java/                ← Pruebas unitarias
```

## Notas de seguridad
- Nunca subas `application-local.properties` con credenciales reales al repositorio
- El archivo `.gitignore` ya está configurado para ignorarlo
- Genera un App Password específico en Gmail para el envío de correos
- Usa una clave JWT de mínimo 256 bits
- El perfil `local` se activa automáticamente con `spring.profiles.active=local`
```

Haz commit:

```bash
git add README.md
git commit -m "docs: actualizar README con configuracion de perfil local"
git push
```

¿Arrancó Spring Boot sin errores? 😊