# Sistema de Gestión de Tarjetas Bancarias

Sistema que permite administrar tarjetas bancarias y sus transacciones, incluyendo generación de tarjetas, activación, manejo de saldo y procesamiento de transacciones.

## Requisitos Previos

- Java JDK 21
- Maven 3.9.x
- PostgreSQL 15 o superior
- IDE (Visual Studio Code, IntelliJ IDEA, Eclipse)

## Configuración del Entorno

### 1. Base de Datos
```sql
-- Crear la base de datos
CREATE DATABASE db_bank;
```

### 2. Variables de Entorno
Crear archivo `.env` en la raíz del proyecto:
```properties
DB_URL=jdbc:postgresql://localhost:5433/db_bank
DB_USERNAME=postgres
DB_PASSWORD=your_password
```

### 3. Configuración del Proyecto

1. Clonar el repositorio:
```bash
git clone <url-repositorio>
cd bank-card-system
```

2. Instalar dependencias:
```bash
mvn clean install
```

3. Verificar la configuración en `application.properties`:
```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update
```

## Ejecutar la Aplicación

1. Compilar el proyecto:
```bash
mvn clean compile
```

2. Ejecutar la aplicación:
```bash
mvn spring-boot:run
```

3. Verificar que la aplicación está corriendo:
- Aplicación: http://localhost:8080

## Endpoints Principales

### Tarjetas
- `GET /card/{productId}/number` - Generar número de tarjeta
- `POST /card/enroll` - Activar tarjeta
- `DELETE /card/{cardId}` - Bloquear tarjeta
- `POST /card/balance` - Recargar saldo
- `GET /card/balance/{cardId}` - Consultar saldo

### Transacciones
- `POST /transaction/purchase` - Realizar compra
- `GET /transaction/{transactionId}` - Consultar transacción
- `POST /transaction/anulation` - Anular transacción

## Ejemplos de Uso

### 1. Generar y Activar Tarjeta
```bash
# Generar número de tarjeta
curl -X GET http://localhost:8080/card/123456/number

# Activar tarjeta
curl -X POST http://localhost:8080/card/enroll \
  -H "Content-Type: application/json" \
  -d '{"cardId":"1234567890123456"}'
```

### 2. Recargar y Verificar Saldo
```bash
# Recargar saldo
curl -X POST http://localhost:8080/card/balance \
  -H "Content-Type: application/json" \
  -d '{
    "cardId": "1234567890123456",
    "balance": 1000.00
  }'

# Verificar saldo
curl -X GET http://localhost:8080/card/balance/1234567890123456
```

### 3. Realizar Compra
```bash
curl -X POST http://localhost:8080/transaction/purchase \
  -H "Content-Type: application/json" \
  -d '{
    "cardId": "1234567890123456",
    "price": 100.00
  }'
```

## Pruebas

Ejecutar pruebas unitarias:
```bash
mvn test
```

## Reglas de Negocio

1. Tarjetas:
   - Validez de 3 años desde creación
   - 16 dígitos (6 primeros son ID de producto)
   - Operaciones solo en dólares

2. Transacciones:
   - Requieren saldo suficiente
   - Tarjeta debe estar activa y no bloqueada
   - Anulaciones solo dentro de 24 horas

## Soporte

Para reportar problemas o solicitar ayuda:
1. Abrir un issue en el repositorio
2. Contactar al equipo de desarrollo

## Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo LICENSE para más detalles.


