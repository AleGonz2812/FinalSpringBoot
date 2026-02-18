# 🚀 FinalSpringBoot - Plataforma SaaS

## 📋 Descripción del Proyecto

Plataforma SaaS (Software as a Service) desarrollada con Spring Boot que permite:
- Registro y autenticación de usuarios
- Gestión de planes de suscripción (Basic, Premium, Enterprise)
- Facturación automática cada 30 días
- Cálculo de prorrateo al cambiar de plan
- Cálculo de impuestos según el país del usuario
- Auditoría completa con Hibernate Envers
- Panel de administración

---

## 🛠️ Tecnologías Utilizadas

### Backend
- **Java 17**
- **Spring Boot 3.2.2**
- **Spring Data JPA** - Persistencia de datos
- **Spring Security** - Autenticación y autorización
- **Hibernate Envers** - Auditoría automática
- **MySQL** - Base de datos
- **Lombok** - Reducción de código boilerplate
- **Maven** - Gestión de dependencias

### Frontend
- **Thymeleaf** - Motor de plantillas
- **Bootstrap 5.3** - Framework CSS
- **Thymeleaf Spring Security** - Integración con seguridad

---

## 📁 Estructura del Proyecto

```
FinalSpringBoot/
├── src/
│   ├── main/
│   │   ├── java/com/example/FinalSpringBoot/
│   │   │   ├── config/              # Configuración (Security, DataLoader, etc.)
│   │   │   ├── controller/          # Controladores REST y web
│   │   │   ├── enums/               # Enumeraciones
│   │   │   ├── model/               # Entidades JPA
│   │   │   ├── repository/          # Repositorios JPA
│   │   │   ├── security/            # Seguridad personalizada
│   │   │   ├── service/             # Lógica de negocio
│   │   │   └── FinalSpringBootApplication.java
│   │   └── resources/
│   │       ├── templates/           # Plantillas Thymeleaf
│   │       │   ├── admin/          # Vistas de administración
│   │       │   ├── factura/        # Vistas de facturas
│   │       │   ├── suscripcion/    # Vistas de suscripciones
│   │       │   └── *.html          # Vistas principales
│   │       └── application.properties
│   └── test/
│       └── java/com/example/FinalSpringBoot/
└── pom.xml
```

---

## 🗄️ Modelo de Datos

### Entidades Principales

#### 👤 Usuario
- Información de autenticación
- Relación con Perfil (OneToOne)
- Historial de suscripciones
- Rol (USER, ADMIN)
- **Auditado con @Audited**

#### 👨‍💼 Perfil
- Datos personales del usuario
- País (para cálculo de impuestos)
- **Auditado con @Audited**

#### 📦 Plan
- Tipos: BASIC, PREMIUM, ENTERPRISE
- Precio mensual
- Características

#### 📝 Suscripción
- Usuario y Plan asociados
- Estado: ACTIVA, CANCELADA, MOROSA
- Fechas de inicio y fin
- Relación con facturas
- **Auditado con @Audited** (historial completo de cambios)

#### 💵 Factura
- Generación automática cada 30 días
- Monto bruto, impuestos y total
- Estado de pago
- Fecha de emisión y vencimiento

#### 💳 MetodoPago (Herencia de Tablas)
- Clase abstracta
- Subclases: TarjetaPago, PayPalPago, TransferenciaPago
- Implementa herencia JOINED

---

## ⚙️ Funcionalidades Implementadas

### ✅ Semana 1 - Base de Datos y JPA
- [x] Entidades con relaciones complejas
- [x] Uso de Enums (EstadoSuscripcion, TipoPlan, Rol, TipoMetodoPago)
- [x] Auditoría con Hibernate Envers (@Audited)
- [x] Herencia de tablas (MetodoPago)
- [x] Vistas funcionales básicas

### ✅ Semana 2 - Lógica de Negocio
- [x] **RenovacionService**: Renovación automática cada 30 días con @Scheduled
- [x] **ImpuestoService**: Cálculo de impuestos según país del usuario
- [x] **SuscripcionService**: Cambio de planes con prorrateo
- [x] **FacturaService**: Generación automática de facturas
- [x] Filtrado de facturas por fecha y monto con JPA
- [x] Panel de Administración (Dashboard, Usuarios, Facturas, Auditoría)
- [x] Vista de Facturación con impuestos

### 🔄 Semana 3 - Pruebas y Documentación
- [x] Pruebas unitarias con JUnit (72 tests pasando)
- [x] Diagrama E-R normalizado (docs/diagrama-er.png)
- [x] Tabla de pruebas realizadas (TESTING.md)
- [x] Mejoras UX/UI (validaciones JavaScript, toast notifications, spinners)
- [x] README completo con datos de prueba

---

## 🔧 Configuración

### Requisitos Previos
- Java 17 o superior
- Maven 3.6+
- MySQL 8.0+
- IDE (IntelliJ IDEA, Eclipse, VS Code)

### Base de Datos

1. Crear base de datos MySQL:
```sql
CREATE DATABASE saas_platform;
```

2. Configurar credenciales en `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/saas_platform
spring.datasource.username=root
spring.datasource.password=tu_contraseña
```

### Ejecutar el Proyecto

1. Clonar el repositorio
2. Compilar:
```bash
mvn clean install
```

3. Ejecutar:
```bash
mvn spring-boot:run
```

4. Acceder a: `http://localhost:8080`

---

## 🧪 Casos de Prueba Pendientes

### Casos Críticos para JUnit

1. **UsuarioService**
   - Registro de nuevo usuario
   - Validación de email duplicado
   - Cambio de contraseña

2. **SuscripcionService**
   - Crear suscripción nueva
   - Cambio de plan (upgrade/downgrade)
   - Cálculo de prorrateo
   - Renovación automática
   - Marcado como morosa

3. **FacturaService**
   - Generación automática de factura
   - Cálculo correcto de impuestos
   - Diferentes países  (España 21%, México 16%, etc.)
   - Asociación correcta con suscripción

4. **ImpuestoService**
   - Cálculo de impuesto por país
   - País sin configurar (usar default 15%)
   - Redondeo de decimales

5. **RenovacionService**
   - Detección de suscripciones a renovar
   - Ejecución programada (@Scheduled)
   - Manejo de errores (marcar como morosa)

6. **Auditoría (Envers)**
   - Verificar que se guarda historial de cambios
   - Recuperar versiones anteriores de suscripciones
   - Auditoría de cambios de plan

---

## 👥 Usuarios de Prueba

Al ejecutar la aplicación, se crean automáticamente:

- **Admin**: `admin` / `admin123`
- **Usuario**: `user` / `user123`

---

## 🧪 Datos de Prueba para Validaciones

### 📝 REGISTRO DE USUARIO

#### ✅ Usernames VÁLIDOS
```
testuser
user123
test_user
alejandro2812
maria_garcia
```

#### ❌ Usernames INVÁLIDOS
```
ab                    → Muy corto (mínimo 3 caracteres)
this_is_a_very_long_username_over_20_chars → Muy largo (máximo 20)
user@123              → Contiene caracteres especiales no permitidos
test user             → Contiene espacios
```

#### ✅ Contraseñas VÁLIDAS (mínimo 8 chars, 1 mayúscula, 1 minúscula, 1 número)
```
Password123
MyPass2024
Test1234
SecurePass99
Admin2026
```

#### ❌ Contraseñas INVÁLIDAS
```
pass123               → No tiene mayúscula
PASSWORD123           → No tiene minúscula
Password              → No tiene número
Pass1                 → Muy corta (menos de 8 caracteres)
12345678              → Solo números, sin letras
```

#### ✅ Emails VÁLIDOS
```
test@example.com
user.name@domain.com
contact@empresa.es
info123@company.org
```

#### ❌ Emails INVÁLIDOS
```
invalidemail          → Falta @
test@                 → Falta dominio
@domain.com           → Falta usuario
test@domain           → Falta extensión
```

---

### 💳 PAGOS - TARJETAS DE CRÉDITO

#### ✅ Tarjetas VÁLIDAS (pasan algoritmo de Luhn)
```
4532015112830366      → Visa (16 dígitos)
5425233430109903      → Mastercard (16 dígitos)
374245455400126       → American Express (15 dígitos)
6011000991001201      → Discover (16 dígitos)
4111111111111111      → Visa Test Card
```

#### ❌ Tarjetas INVÁLIDAS (no pasan algoritmo de Luhn)
```
1234567890123456      → Checksum inválido
4111111111111112      → Último dígito incorrecto
9999999999999999      → Checksum inválido
12345678              → Muy corta
```

**Nota:** Al escribir, se formatean automáticamente con espacios:  
`4532015112830366` → `4532 0151 1283 0366`

#### ✅ CVV VÁLIDOS
```
123                   → Visa/Mastercard (3 dígitos)
4567                  → American Express (4 dígitos)
001                   → Válido
```

#### ❌ CVV INVÁLIDOS
```
12                    → Muy corto
12345                 → Muy largo
abc                   → No es numérico
```

#### ✅ Fechas de Expiración VÁLIDAS (formato MM/YY)
```
12/26                 → Diciembre 2026
06/27                 → Junio 2027
01/28                 → Enero 2028
```

**Nota:** Al escribir `1226`, se formatea automáticamente a `12/26`

#### ❌ Fechas de Expiración INVÁLIDAS
```
01/25                 → Expirada (2025 ya pasó)
13/26                 → Mes inválido (>12)
00/26                 → Mes inválido (<01)
12/20                 → Expirada
1/26                  → Formato incorrecto (debe ser MM/YY)
```

---

### 🏦 PAGOS - TRANSFERENCIA BANCARIA (IBAN)

#### ✅ IBANs VÁLIDOS

**España (ES) - 24 caracteres:**
```
ES7921000813610123456789
ES1234567890123456789012
ES9020385778983000760236
```

**Alemania (DE) - 22 caracteres:**
```
DE89370400440532013000
DE12345678901234567890
```

**Francia (FR) - 27 caracteres:**
```
FR1420041010050500013M02606
FR7630006000011234567890189
```

**Italia (IT) - 27 caracteres:**
```
IT60X0542811101000000123456
IT40S0542811101000000123456
```

**Portugal (PT) - 25 caracteres:**
```
PT50000201231234567890154
PT50123456789012345678901
```

#### ❌ IBANs INVÁLIDOS
```
ES123                 → Muy corto
XX1234567890123456789012 → Código país inválido
ES12                  → Incompleto
1234567890            → Sin código país
ESABCDEFGHIJ          → Caracteres no permitidos
```

**Formato IBAN:** `[2 letras país][2 dígitos control][resto alfanumérico]`

---

### 📧 PAGOS - PAYPAL

#### ✅ Emails PayPal VÁLIDOS
```
user@paypal.com
miusuario@gmail.com
contacto@empresa.es
test.user@domain.org
```

#### ❌ Emails PayPal INVÁLIDOS
```
notanemail            → Falta @
test@                 → Falta dominio
@paypal.com           → Falta usuario
invalid.email         → Falta @ y dominio
```

---

### 🌍 PAÍSES DISPONIBLES (para cálculo de impuestos)

| País              | Código | Impuesto (IVA) |
|-------------------|--------|----------------|
| España            | ES     | 21%            |
| México            | MX     | 16%            |
| Argentina         | AR     | 21%            |
| Chile             | CL     | 19%            |
| Colombia          | CO     | 19%            |
| Francia           | FR     | 20%            |
| Alemania          | DE     | 19%            |
| Italia            | IT     | 22%            |
| Portugal          | PT     | 23%            |
| Estados Unidos    | US     | 7%             |
| Canadá            | CA     | 13%            |
| **Otros países**  | -      | **15% (default)** |

---

## 🎯 Escenarios de Prueba Completos

### Escenario 1: Registro Completo
1. Username: `testuser123`
2. Email: `test@example.com`
3. Password: `Password123`
4. Nombre: `Test`
5. Apellido: `User`
6. País: `España`

### Escenario 2: Suscripción con Tarjeta
1. Plan: `BASIC` (9.99€/mes)
2. Método: `Tarjeta de Crédito`
3. Número: `4532015112830366` → se formatea a `4532 0151 1283 0366`
4. Fecha: `12/28`
5. CVV: `123`
6. Titular: `Test User`

**Factura generada:**
- Monto bruto: 9.99€
- Impuesto (España 21%): 2.10€
- Total: **12.09€**

### Escenario 3: Suscripción con PayPal
1. Plan: `PREMIUM` (19.99€/mes)
2. Método: `PayPal`
3. Email: `usuario@paypal.com`

**Factura generada:**
- Monto bruto: 19.99€
- Impuesto (España 21%): 4.20€
- Total: **24.19€**

### Escenario 4: Cambio de Plan (Upgrade con Prorrateo)
1. Plan actual: `BASIC` (9.99€/mes)
2. Nuevo plan: `PREMIUM` (19.99€/mes)
3. Días restantes: 15 días
4. Diferencia: 10.00€
5. Prorrateo: (10.00 × 15) ÷ 30 = **5.00€**
6. Impuesto: 5.00 × 0.21 = **1.05€**
7. Total a pagar: **6.05€**

---

## 📊 Planes Disponibles

| Plan       | Precio Mensual | Características                    |
|------------|----------------|------------------------------------|
| BASIC      | $9.99          | Funcionalidades básicas            |
| PREMIUM    | $29.99         | Funcionalidades avanzadas          |
| ENTERPRISE | $99.99         | Todas las funcionalidades + soporte|

---

## 🔐 Seguridad

- Contraseñas encriptadas con BCrypt
- Roles de usuario (USER, ADMIN)
- Protección CSRF
- Autenticación basada en sesiones
- Thymeleaf Security para vistas protegidas

---

## 📝 Notas del Desarrollo

### Prorrateo de Cambio de Plan

Cuando un usuario cambia de un plan barato a uno caro:
1. Se calcula los días restantes del ciclo actual
2. Se calcula el monto proporcional a pagar
3. Se genera una factura de ajuste
4. Se actualiza la suscripción al nuevo plan

### Renovación Automática

- Tarea programada que se ejecuta diariamente a medianoche
- Busca suscripciones cuya fecha de fin sea hoy
- Genera factura automáticamente
- Si falla el pago, marca la suscripción como MOROSA

### Auditoría con Envers

- Tablas de auditoría: `*_AUD` y `REVINFO`
- Se registra cada cambio en Usuario, Perfil, Suscripción
- Útil para el panel de administración

---

## 🎨 Mejoras UX/UI Implementadas

### ✅ Validaciones JavaScript en Tiempo Real

**Registro:**
- Validación de username (3-20 caracteres alfanuméricos)
- Validación de email con regex
- Validación de contraseña fuerte (8+ chars, mayúscula, minúscula, número)
- Feedback visual instantáneo (borde verde/rojo)

**Suscripciones:**
- **Tarjetas de crédito:**
  - Algoritmo de Luhn para detectar números inválidos
  - Formateo automático con espacios (1234 5678 9012 3456)
  - Validación de CVV (3-4 dígitos)
  - Validación de fecha de expiración (MM/YY)
  - Detección de tarjetas expiradas
  
- **PayPal:**
  - Validación de formato de email
  
- **Transferencia:**
  - Validación de formato IBAN
  - Soporte para múltiples países (ES, DE, FR, IT, PT, etc.)

### 🔔 Toast Notifications

Sistema de notificaciones elegantes que reemplazan los `alert()` nativos:
- **Verde** con ✓ para éxito
- **Rojo** con ✕ para errores
- **Amarillo** con ⚠ para advertencias
- **Azul** con ℹ para información
- Auto-cierre en 4 segundos
- Animaciones suaves de entrada/salida

### ⏳ Loading Spinners

- Spinner modal con fondo oscuro semi-transparente
- Se muestra durante:
  - Envío de formularios de registro/login
  - Procesamiento de pagos
  - Cambios de plan
  - Cancelaciones de suscripción
- Previene doble-submit y mejora percepción de tiempo de carga

### ⚠️ Confirmaciones Mejoradas

- Mensajes descriptivos antes de acciones destructivas
- Cancelación de suscripción con advertencia clara
- Cambio de plan con información de prorrateo

### 📁 Archivos Implementados

- `src/main/resources/static/css/toast.css` - Estilos para notificaciones y validaciones
- `src/main/resources/static/js/utils.js` - Funciones de validación, toast y spinner

Ver documentación completa en: [MEJORAS-UX.md](MEJORAS-UX.md)

---

## 🚧 Tareas Pendientes (Semana 3)

1. ~~Implementar pruebas JUnit~~ ✅ **COMPLETADO** (72 tests)
2. ~~Crear diagrama E-R~~ ✅ **COMPLETADO** (ver docs/diagrama-er.png)
3. ~~Documentar casos de prueba~~ ✅ **COMPLETADO** (TESTING.md)
4. ~~Mejorar UX/UI~~ ✅ **COMPLETADO** (validaciones + toast + spinners)
5. ~~README completo~~ ✅ **COMPLETADO** (con datos de prueba)
6. **Commit y push final a GitHub** ⏳ PENDIENTE

---

## 📧 Autor

Alejandro González
- GitHub: [@AleGonz2812](https://github.com/AleGonz2812)
- Email: agonvel.1307@gmail.com

---

## 📄 Licencia

Este proyecto es parte de un ejercicio académico - ILERNA 2º AÑO

---

**Fecha de entrega**: 20 de febrero de 2026  
**Estado**: ✅ Semana 1, 2 y 3 completadas | 🚀 Listo para entrega
