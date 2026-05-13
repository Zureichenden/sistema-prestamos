# 🏦 Sistema de Préstamos

API REST desarrollada con Spring Boot para gestión de préstamos financieros.

## Tecnologías
- Java 17
- Spring Boot 4.x
- Spring Security + JWT
- PostgreSQL
- JPA / Hibernate
- Lombok
- Swagger UI
- JUnit 5 + Mockito

## Módulos
- ✅ Clientes
- ✅ Préstamos con cálculo de amortizaciones
- ✅ Pagos
- ✅ Bitácora
- ✅ Reportes por rango de fechas
- ✅ Exportar a PDF y Excel
- ✅ Email de bienvenida
- ✅ Pruebas unitarias

## Instalación

### Requisitos
- Java 17+
- PostgreSQL
- Maven

### Configuración
1. Crear base de datos:
```sql
CREATE DATABASE sistema_prestamos;
```

2. Configurar `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/sistema_prestamos
spring.datasource.username=postgres
spring.datasource.password=TU_PASSWORD
jwt.secret=TU_CLAVE_SECRETA
```

3. Ejecutar:
```bash
mvn spring-boot:run
```

4. Swagger UI disponible en: http://localhost:8080/swagger-ui/index.html

## Credenciales por defecto
| Usuario | Contraseña | Rol |
|---------|-----------|-----|
| admin | admin123 | ADMIN |
| usuario | user123 | USER |