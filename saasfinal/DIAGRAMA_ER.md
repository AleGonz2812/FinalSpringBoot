# Diagrama Entidad-Relación (E-R) - Plataforma SaaS

## Diseño de Base de Datos Normalizada

### Resumen de Entidades

```
┌─────────────────────────────────────────────────────────────────────┐
│                    PLATAFORMA SaaS - MODELO E-R                     │
└─────────────────────────────────────────────────────────────────────┘

┌──────────────┐         ┌──────────────┐         ┌──────────────┐
│   USUARIO    │1──────1 │    PERFIL    │         │     PLAN     │
│──────────────│         │──────────────│         │──────────────│
│ id (PK)      │         │ id (PK)      │         │ id (PK)      │
│ email        │         │ nombre       │         │ tipo (ENUM)  │
│ password     │         │ apellidos    │         │ precio       │
│ fechaRegistro│         │ telefono     │         │ descripcion  │
│ activo       │         │ empresa      │         │ maxUsuarios  │
└──────────────┘         │ pais         │         │ almacenamiento│
       │                 │ direccion    │         │ soporte      │
       │                 │ usuario_id(FK)│        └──────────────┘
       │                 └──────────────┘                │
       │                                                 │
       │1                                               1│
       │                                                 │
       │N              ┌──────────────┐                N│
       └──────────────►│ SUSCRIPCION  │◄────────────────┘
                       │──────────────│
                       │ id (PK)      │
                       │ usuario_id(FK)│
                       │ plan_id (FK) │
                       │ estado (ENUM)│ ◄── @Audited (Envers)
                       │ fechaInicio  │     Rastrea cambios
                       │ fechaFin     │     de plan
                       │ fechaProxRen │
                       │ precioActual │
                       │ autoRenovacion│
                       └──────────────┘
                              │1
                              │
                              │N
                       ┌──────────────┐
                       │   FACTURA    │
                       │──────────────│
                       │ id (PK)      │
                       │ numeroFactura│
                       │ suscripcion(FK)│
                       │ fechaEmision │
                       │ fechaVencim. │
                       │ fechaPago    │
                       │ montoBase    │
                       │ impuestos    │
                       │ prorrateo    │
                       │ montoTotal   │
                       │ estado (ENUM)│
                       │ concepto     │
                       │ metodoPago(FK)│
                       └──────────────┘
                              │N
                              │
                              │1
                       ┌──────────────┐
                       │ METODO_PAGO  │ ◄── Herencia SINGLE_TABLE
                       │──────────────│
                       │ id (PK)      │
                       │ tipo (ENUM)  │ ◄── Discriminador
                       │ usuario_id(FK)│
                       │ fechaRegistro│
                       │ activo       │
                       │ predeterminado│
                       └──────────────┘
                              △
                    ┌─────────┼─────────┐
                    │         │         │
            ┌───────────┐ ┌───────────┐ ┌────────────────┐
            │  TARJETA  │ │  PAYPAL   │ │ TRANSFERENCIA  │
            │───────────│ │───────────│ │────────────────│
            │numeroTarj.│ │emailPayPal│ │ nombreBanco    │
            │titular    │ │idCuenta   │ │ numeroCuenta   │
            │fechaVenc. │ │verificado │ │ titularCuenta  │
            │cvv        │ └───────────┘ │ codigoSwift    │
            └───────────┘               └────────────────┘
```

---

## Descripción de Relaciones

### 1. Usuario - Perfil (1:1)
- **Cardinalidad**: Un usuario tiene un perfil, un perfil pertenece a un usuario
- **Implementación**: `@OneToOne` con `mappedBy` en Usuario
- **Clave Foránea**: `usuario_id` en tabla `perfiles`

### 2. Usuario - Suscripción (1:N)
- **Cardinalidad**: Un usuario puede tener múltiples suscripciones (históricas)
- **Implementación**: `@OneToMany` en Usuario, `@ManyToOne` en Suscripción
- **Clave Foránea**: `usuario_id` en tabla `suscripciones`

### 3. Plan - Suscripción (1:N)
- **Cardinalidad**: Un plan puede tener muchas suscripciones
- **Implementación**: `@OneToMany` en Plan, `@ManyToOne` en Suscripción
- **Clave Foránea**: `plan_id` en tabla `suscripciones`

### 4. Suscripción - Factura (1:N)
- **Cardinalidad**: Una suscripción genera múltiples facturas (mensualmente)
- **Implementación**: `@OneToMany` en Suscripción, `@ManyToOne` en Factura
- **Clave Foránea**: `suscripcion_id` en tabla `facturas`

### 5. Usuario - MetodoPago (1:N)
- **Cardinalidad**: Un usuario puede tener múltiples métodos de pago
- **Implementación**: `@ManyToOne` en MetodoPago
- **Clave Foránea**: `usuario_id` en tabla `metodos_pago`

### 6. MetodoPago - Factura (1:N)
- **Cardinalidad**: Un método de pago puede usarse para pagar múltiples facturas
- **Implementación**: `@ManyToOne` en Factura
- **Clave Foránea**: `metodo_pago_id` en tabla `facturas`

---

## Tipos de Datos (Enums)

### EstadoSuscripcion
```java
- ACTIVA
- CANCELADA
- MOROSA
```

### TipoPlan
```java
- BASIC ($9.99)
- PREMIUM ($29.99)
- ENTERPRISE ($99.99)
```

### TipoPago
```java
- TARJETA
- PAYPAL
- TRANSFERENCIA
```

### EstadoFactura
```java
- PENDIENTE
- PAGADA
- VENCIDA
- CANCELADA
```

---

## Herencia de Tablas (SINGLE_TABLE)

La entidad `MetodoPago` utiliza **herencia de tabla única** con un discriminador:

```sql
CREATE TABLE metodos_pago (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tipo_pago VARCHAR(20), -- Discriminador (TARJETA, PAYPAL, TRANSFERENCIA)
    tipo VARCHAR(20),
    usuario_id BIGINT,
    fecha_registro DATETIME,
    activo BOOLEAN,
    predeterminado BOOLEAN,
    
    -- Campos específicos de PagoTarjeta
    numero_tarjeta VARCHAR(16),
    titular VARCHAR(100),
    fecha_vencimiento VARCHAR(7),
    cvv VARCHAR(4),
    
    -- Campos específicos de PagoPayPal
    email_paypal VARCHAR(100),
    id_cuenta_paypal VARCHAR(100),
    verificado BOOLEAN,
    
    -- Campos específicos de PagoTransferencia
    nombre_banco VARCHAR(100),
    numero_cuenta VARCHAR(50),
    titular_cuenta VARCHAR(100),
    codigo_swift VARCHAR(20),
    
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);
```

**Ventaja**: Una sola tabla para todos los tipos de pago, con discriminador para diferenciarlos.

---

## Auditoría con Envers (@Audited)

Las siguientes entidades están auditadas para rastrear cambios históricos:

1. **Usuario** - Rastrea cambios en datos del usuario
2. **Perfil** - Rastrea actualizaciones de información personal
3. **Plan** - Rastrea cambios en precios y características
4. **Suscripción** ⭐ - **MUY IMPORTANTE**: Rastrea cambios de plan y estado
5. **Factura** - Rastrea modificaciones en facturas
6. **MetodoPago** - Rastrea añadido/eliminación de métodos de pago

### Tablas de Auditoría Generadas Automáticamente:
- `usuarios_AUD`
- `perfiles_AUD`
- `planes_AUD`
- `suscripciones_AUD` ⭐
- `facturas_AUD`
- `metodos_pago_AUD`
- `REVINFO` (tabla de revisiones)

**Ejemplo de uso**: Para ver quién cambió de plan y cuándo:
```java
AuditReader reader = AuditReaderFactory.get(entityManager);
List<Suscripcion> revisiones = reader.createQuery()
    .forRevisionsOfEntity(Suscripcion.class, false, true)
    .add(AuditEntity.id().eq(suscripcionId))
    .getResultList();
```

---

## Normalización

✅ **Primera Forma Normal (1FN)**: Todos los atributos son atómicos
✅ **Segunda Forma Normal (2FN)**: No hay dependencias parciales
✅ **Tercera Forma Normal (3FN)**: No hay dependencias transitivas

### Justificación:
- Cada entidad tiene una clave primaria (`id`)
- No hay campos multivaluados
- Las relaciones están bien definidas con claves foráneas
- Los Enums evitan redundancia de datos
- La herencia de `MetodoPago` agrupa comportamiento común

---

## Características Avanzadas Implementadas

✅ Relaciones `@OneToOne`, `@OneToMany`, `@ManyToOne`
✅ Herencia de entidades con `@Inheritance(SINGLE_TABLE)`
✅ Auditoría completa con `@Audited` (Hibernate Envers)
✅ Enums para estados y tipos
✅ Validaciones con Bean Validation (`@NotBlank`, `@Email`, etc.)
✅ Métodos helper para coherencia bidireccional
✅ Cálculos automáticos (prorrateo, renovaciones)
✅ Cascadas y orphanRemoval configurados correctamente

---

## Próximos Pasos (Semana 2)

1. Implementar la lógica de renovación automática de suscripciones
2. Calcular impuestos según el país del usuario
3. Desarrollar vistas dinámicas de facturación
4. Crear panel de auditoría para administradores
5. Implementar el cálculo de prorrateo al cambiar de plan

---

_Documentación generada para el proyecto SaaS Final - Semana 1_
