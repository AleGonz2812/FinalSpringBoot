# 🚀 Plataforma SaaS - Sistema de Suscripciones y Facturación

## 📖 Descripción del Proyecto

Sistema SaaS completo que permite:
- ✅ Registro de usuarios con perfil detallado
- ✅ Elección de planes de suscripción (Basic, Premium, Enterprise)
- ✅ Generación automática de facturas cada 30 días
- ✅ Cálculo de prorrateo al cambiar de plan
- ✅ Auditoría completa con Hibernate Envers
- ✅ Múltiples métodos de pago (Tarjeta, PayPal, Transferencia)

---

## 🏗️ Arquitectura del Proyecto

### Tecnologías Utilizadas

- **Java 21**
- **Spring Boot 4.0.2**
- **Spring Data JPA** - Persistencia
- **Hibernate Envers** - Auditoría
- **MySQL 8** - Base de datos
- **Thymeleaf** - Motor de plantillas
- **Bootstrap 5** - Framework CSS
- **Lombok** - Reducción de código boilerplate
- **Maven** - Gestión de dependencias

### Estructura del Proyecto

```
saasfinal/
├── src/main/java/com/example/saasfinal/
│   ├── model/
│   │   ├── entity/          # Entidades JPA
│   │   │   ├── Usuario.java
│   │   │   ├── Perfil.java
│   │   │   ├── Plan.java
│   │   │   ├── Suscripcion.java
│   │   │   ├── Factura.java
│   │   │   ├── MetodoPago.java (abstracta)
│   │   │   ├── PagoTarjeta.java
│   │   │   ├── PagoPayPal.java
│   │   │   └── PagoTransferencia.java
│   │   └── enums/          # Enumeraciones
│   │       ├── EstadoSuscripcion.java
│   │       ├── TipoPlan.java
│   │       ├── TipoPago.java
│   │       └── EstadoFactura.java
│   ├── repository/         # Capa de acceso a datos
│   ├── service/           # Lógica de negocio
│   └── controller/        # Controladores web
├── src/main/resources/
│   ├── application.properties
│   └── templates/         # Vistas Thymeleaf
├── DIAGRAMA_ER.md        # Diagrama Entidad-Relación
└── pom.xml
```

---

## 🗄️ Modelo de Datos

### Entidades Principales

1. **Usuario** - Información de autenticación
2. **Perfil** - Datos personales del usuario (1:1)
3. **Plan** - Niveles de suscripción (Basic, Premium, Enterprise)
4. **Suscripción** - Relación Usuario-Plan con auditoría
5. **Factura** - Generada automáticamente cada 30 días
6. **MetodoPago** - Herencia de tabla única (SINGLE_TABLE)

### Características JPA Avanzadas

✅ **Relaciones complejas**: `@OneToOne`, `@OneToMany`, `@ManyToOne`
✅ **Herencia**: `@Inheritance(SINGLE_TABLE)` en MetodoPago
✅ **Auditoría**: `@Audited` con Hibernate Envers
✅ **Enums**: Para estados y tipos
✅ **Validaciones**: Bean Validation (`@NotBlank`, `@Email`, etc.)
✅ **Cascadas**: `CascadeType.ALL` y `orphanRemoval`

Ver el diagrama completo en: [DIAGRAMA_ER.md](DIAGRAMA_ER.md)

---

## 🚀 Instalación y Configuración

### Requisitos Previos

- Java 21 o superior
- MySQL 8 o superior
- Maven 3.6+
- IDE (IntelliJ IDEA, Eclipse, VS Code)

### Paso 1: Clonar el repositorio

```bash
git clone <url-del-repositorio>
cd saasfinal
```

### Paso 2: Configurar MySQL

1. Crear la base de datos:
```sql
CREATE DATABASE saas_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. Editar `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/saas_db?createDatabaseIfNotExist=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=tu_password
```

### Paso 3: Compilar y ejecutar

```bash
# Compilar el proyecto
mvn clean install

# Ejecutar la aplicación
mvn spring-boot:run
```

La aplicación estará disponible en: **http://localhost:8080**

---

## 📱 Uso de la Aplicación

### Páginas Disponibles (Semana 1)

- **/** - Página de inicio
- **/planes** - Listado de planes disponibles

### Inicialización Automática

Al arrancar la aplicación, se crean automáticamente los 3 planes:
- **Basic** - $9.99/mes
- **Premium** - $29.99/mes
- **Enterprise** - $99.99/mes

---

## 🗃️ Base de Datos

### Tablas Principales

```
usuarios
perfiles
planes
suscripciones
facturas
metodos_pago
```

### Tablas de Auditoría (Envers)

```
usuarios_AUD
perfiles_AUD
planes_AUD
suscripciones_AUD
facturas_AUD
metodos_pago_AUD
REVINFO
```

---

## 📊 Funcionalidades por Semana

### ✅ Semana 1 (2-6 febrero) - COMPLETADO

- [x] Diseño de base de datos normalizada
- [x] Entidades JPA con relaciones complejas
- [x] Uso de Enums (EstadoSuscripcion, TipoPlan, etc.)
- [x] Auditoría con Envers (@Audited)
- [x] Herencia de tablas (MetodoPago)
- [x] Diagrama E-R documentado
- [x] Vistas funcionales para validar planes

### 🔄 Semana 2 (9-13 febrero) - PENDIENTE

- [ ] Lógica de renovación automática de suscripciones
- [ ] Cálculo de impuestos según país
- [ ] Cálculo de prorrateo al cambiar de plan
- [ ] Vista de facturación dinámica
- [ ] Panel de auditoría para administradores

### 🔄 Semana 3 (16-20 febrero) - PENDIENTE

- [ ] Pruebas unitarias (JUnit)
- [ ] Mejora de vistas UX/UI
- [ ] Documentación completa
- [ ] Tabla de pruebas realizadas

---

## 🧪 Testing

*Pendiente para Semana 3*

---

## 📝 Documentación Adicional

- [Diagrama Entidad-Relación](DIAGRAMA_ER.md)
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/)
- [Hibernate Envers Guide](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#envers)

---

## 👥 Contribuidores

- Desarrollo: [Tu Nombre]
- Proyecto: Final Spring Boot 2026

---

## 📄 Licencia

Este proyecto es de uso académico para la asignatura de Spring Boot.

---

## 📞 Soporte

Para dudas o problemas, contacta a: [tu-email@example.com]

---

**Estado del Proyecto**: Semana 1 ✅ Completada
