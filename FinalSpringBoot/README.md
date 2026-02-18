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

### 🔄 Semana 3 - Pruebas y Documentación (EN CURSO)
- [ ] Pruebas unitarias con JUnit
- [ ] Diagrama E-R normalizado
- [ ] Tabla de pruebas realizadas
- [ ] Mejoras UX/UI
- [ ] README completo

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

## 🚧 Tareas Pendientes (Semana 3)

1. **Implementar pruebas JUnit** para todos los servicios críticos
2. **Crear diagrama E-R** con herramienta (draw.io, Lucidchart)
3. **Documentar casos de prueba** en tabla (Excel/Markdown)
4. **Mejorar UX/UI** de las vistas
5. **Agregar validaciones** frontend adicionales
6. **Commit y push** final a GitHub

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
**Estado**: ✅ Semana 1 y 2 completadas | 🔄 Semana 3 en curso
