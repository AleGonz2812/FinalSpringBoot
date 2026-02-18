# Documentación de Pruebas Unitarias

## Resumen de Ejecución
- **Total de Tests:** 72
- **Tests Pasados:** 72 ✅
- **Tests Fallidos:** 0 ❌
- **Cobertura:** Services principales (UsuarioService, SuscripcionService, FacturaService, ImpuestoService, RenovacionService)
- **Fecha de Ejecución:** 18 de febrero de 2026

---

## Tabla de Casos de Prueba

| # | Caso de Prueba | Clase/Método | Input | Expected | Actual | Estado | Notas |
|---|---|---|---|---|---|---|---|
| **USUARIO SERVICE - Registro y Autenticación** |
| 1 | Registro de usuario válido | `UsuarioServiceTest.testRegistrarUsuario_Exitoso()` | username="newuser", email="new@test.com", password="Password123!", nombre="New", apellido="User", pais="España" | Usuario guardado con password BCrypt, perfil creado, rol USER | Usuario creado correctamente con perfil asociado | ✅ | Password encriptado con BCrypt |
| 2 | Username duplicado rechazado | `UsuarioServiceTest.testRegistrarUsuario_UsernameDuplicado()` | username existente en BD | RuntimeException: "El username ya está en uso" | Exception lanzada correctamente | ✅ | Validación previa a inserción |
| 3 | Email duplicado rechazado | `UsuarioServiceTest.testRegistrarUsuario_EmailDuplicado()` | email existente en BD | RuntimeException: "El email ya está registrado" | Exception lanzada correctamente | ✅ | Validación de unicidad |
| 4 | Buscar usuario por username | `UsuarioServiceTest.testObtenerUsuarioPorUsername_Encontrado()` | username="testuser" | Optional<Usuario> con datos | Usuario encontrado | ✅ | - |
| 5 | Buscar usuario inexistente | `UsuarioServiceTest.testObtenerUsuarioPorUsername_NoEncontrado()` | username="noexiste" | Optional.empty() | Optional vacío | ✅ | - |
| 6 | Buscar usuario por email | `UsuarioServiceTest.testObtenerUsuarioPorEmail_Encontrado()` | email="test@test.com" | Optional<Usuario> con datos | Usuario encontrado | ✅ | - |
| **USUARIO SERVICE - Gestión de Perfiles** |
| 7 | Actualizar perfil existente | `UsuarioServiceTest.testActualizarPerfil_PerfilExistente()` | usuarioId=1, nombre="Updated", apellido="Name", pais="Francia" | Perfil actualizado con nuevos datos | Datos actualizados correctamente | ✅ | Update en cascada |
| 8 | Crear perfil para usuario sin perfil | `UsuarioServiceTest.testActualizarPerfil_UsuarioSinPerfil()` | usuarioId=2, datos nuevos | Perfil creado y asociado | Perfil creado desde cero | ✅ | Manejo de null safety |
| 9 | Actualizar perfil de usuario inexistente | `UsuarioServiceTest.testActualizarPerfil_UsuarioNoEncontrado()` | usuarioId=999 | RuntimeException: "Usuario no encontrado" | Exception lanzada | ✅ | - |
| 10 | Verificar existencia de username | `UsuarioServiceTest.testExisteUsername_True()` | username existente | true | true | ✅ | - |
| 11 | Verificar username no existente | `UsuarioServiceTest.testExisteUsername_False()` | username nuevo | false | false | ✅ | - |
| 12 | Verificar existencia de email | `UsuarioServiceTest.testExisteEmail_True()` | email existente | true | true | ✅ | - |
| 13 | Verificar email no existente | `UsuarioServiceTest.testExisteEmail_False()` | email nuevo | false | false | ✅ | - |
| **SUSCRIPCION SERVICE - Creación** |
| 14 | Crear suscripción exitosa | `SuscripcionServiceTest.testCrearSuscripcion_Exitoso()` | usuario, plan BASIC | Suscripción ACTIVA, duración 30 días, factura generada | Suscripción creada con fechas correctas | ✅ | Factura automática generada |
| 15 | Crear suscripción con usuario ya activo | `SuscripcionServiceTest.testCrearSuscripcion_YaTieneSuscripcionActiva()` | usuario con suscripción activa | RuntimeException: "ya tiene una suscripción activa" | Exception lanzada | ✅ | Solo 1 suscripción activa por usuario |
| 16 | Crear suscripción con método de pago | `SuscripcionServiceTest.testCrearSuscripcionConMetodoPago_Exitoso()` | usuario, plan, tarjetaPago | Suscripción con método de pago asociado | Suscripción con tarjeta guardada | ✅ | Soporte para TarjetaPago, PayPal, Transferencia |
| **SUSCRIPCION SERVICE - Cambio de Plan** |
| 17 | Upgrade con prorrateo | `SuscripcionServiceTest.testCambiarPlan_Upgrade_ConProrrateo()` | BASIC → PREMIUM (dias restantes: 15) | Factura de prorrateo generada, plan actualizado | Prorrateo calculado: (19.99-9.99)*(15/30)=5.00 | ✅ | Cálculo proporcional de diferencia |
| 18 | Downgrade sin prorrateo | `SuscripcionServiceTest.testCambiarPlan_Downgrade_SinProrrateo()` | PREMIUM → BASIC | Plan cambiado, NO genera factura | Plan actualizado sin cobro adicional | ✅ | Cambio efectivo en próxima renovación |
| 19 | Cambiar al mismo plan | `SuscripcionServiceTest.testCambiarPlan_MismoPlan()` | BASIC → BASIC | RuntimeException: "Ya estás suscrito a este plan" | Exception lanzada | ✅ | Validación de cambio innecesario |
| 20 | Cambiar plan sin suscripción activa | `SuscripcionServiceTest.testCambiarPlan_SinSuscripcionActiva()` | Usuario sin suscripción | RuntimeException: "No hay suscripción activa" | Exception lanzada | ✅ | - |
| **SUSCRIPCION SERVICE - Renovación** |
| 21 | Renovar suscripción exitosa | `SuscripcionServiceTest.testRenovarSuscripcion_Exitoso()` | suscripcionId=1 (activa) | Fecha fin extendida +30 días, factura generada | Renovación exitosa con nueva fecha fin | ✅ | Cobro del precio del plan actual |
| 22 | Renovar suscripción no activa | `SuscripcionServiceTest.testRenovarSuscripcion_NoActiva()` | suscripcionId=1 (CANCELADA) | RuntimeException: "no está activa" | Exception lanzada | ✅ | Solo permite renovar ACTIVA |
| 23 | Renovar suscripción inexistente | `SuscripcionServiceTest.testRenovarSuscripcion_NoEncontrada()` | suscripcionId=999 | RuntimeException: "no encontrada" | Exception lanzada | ✅ | - |
| 24 | Obtener suscripciones para renovar | `SuscripcionServiceTest.testObtenerSuscripcionesParaRenovar()` | Fecha actual | Lista de suscripciones vencidas ACTIVAS | Lista obtenida correctamente | ✅ | Usado por proceso automático |
| **SUSCRIPCION SERVICE - Gestión de Estado** |
| 25 | Cancelar suscripción | `SuscripcionServiceTest.testCancelarSuscripcion_Exitoso()` | suscripcionId=1 | Estado cambiado a CANCELADA | Estado actualizado | ✅ | No se devuelve dinero |
| 26 | Marcar como morosa | `SuscripcionServiceTest.testMarcarComoMorosa_Exitoso()` | suscripcionId=1 | Estado cambiado a MOROSA | Estado actualizado | ✅ | Por fallo en renovación automática |
| 27 | Obtener suscripción activa de usuario | `SuscripcionServiceTest.testObtenerSuscripcionActiva_Encontrada()` | usuarioId=1 | Optional<Suscripcion> con estado ACTIVA | Suscripción encontrada | ✅ | - |
| 28 | Obtener suscripción activa inexistente | `SuscripcionServiceTest.testObtenerSuscripcionActiva_NoEncontrada()` | usuarioId sin suscripción | Optional.empty() | Optional vacío | ✅ | - |
| 29 | Filtrar suscripciones por estado | `SuscripcionServiceTest.testObtenerPorEstado()` | estado=ACTIVA | Lista de suscripciones ACTIVAS | 2 suscripciones activas obtenidas | ✅ | Usado en admin panel |
| **FACTURA SERVICE - Generación** |
| 30 | Generar factura con impuestos (España) | `FacturaServiceTest.testGenerarFactura_Exitoso()` | suscripción, montoBruto=9.99, pais="España" | Factura: bruto=9.99, impuesto=2.10 (21%), total=12.09 | Factura generada correctamente | ✅ | Cálculo automático por país |
| 31 | Generar factura con impuestos (México) | `FacturaServiceTest.testGenerarFactura_PaisDiferente()` | suscripción, montoBruto=10.00, pais="México" | Factura: bruto=10.00, impuesto=1.60 (16%), total=11.60 | Factura generada correctamente | ✅ | Diferentes tasas por país |
| **FACTURA SERVICE - Consultas** |
| 32 | Obtener facturas por usuario | `FacturaServiceTest.testObtenerFacturasPorUsuario()` | usuarioId=1 | Lista ordenada por fecha desc | 2 facturas obtenidas en orden | ✅ | Orden cronológico inverso |
| 33 | Obtener facturas por suscripción | `FacturaServiceTest.testObtenerFacturasPorSuscripcion()` | suscripcionId=1 | Lista de facturas de esa suscripción | 1 factura obtenida | ✅ | Historial de cobros |
| 34 | Calcular total facturado de usuario | `FacturaServiceTest.testCalcularTotalFacturadoUsuario()` | usuarioId=1 con 3 facturas | Suma total: 12.09+12.09+24.18=48.36 | 48.36 calculado | ✅ | Usado en dashboard admin |
| 35 | Total facturado usuario sin facturas | `FacturaServiceTest.testCalcularTotalFacturadoUsuario_SinFacturas()` | usuarioId sin facturas | BigDecimal.ZERO | 0.00 | ✅ | Manejo de caso vacío |
| 36 | Filtrar facturas por rango de fechas | `FacturaServiceTest.testFiltrarPorFechas()` | fechaInicio, fechaFin | Facturas en ese rango | 1 factura en rango | ✅ | Reportes mensuales/anuales |
| 37 | Filtrar facturas por monto mayor a | `FacturaServiceTest.testFiltrarPorMontoMayorA()` | monto=10.00 | Facturas con total > 10.00 | Lista filtrada | ✅ | Análisis de altos montos |
| 38 | Filtrar facturas por monto menor a | `FacturaServiceTest.testFiltrarPorMontoMenorA()` | monto=20.00 | Facturas con total < 20.00 | Lista filtrada | ✅ | - |
| 39 | Buscar con filtros combinados | `FacturaServiceTest.testBuscarConFiltros()` | fechas + montoMin + montoMax | Facturas que cumplen todos los filtros | Lista filtrada correctamente | ✅ | Query complejo con múltiples condiciones |
| 40 | Obtener factura por ID | `FacturaServiceTest.testObtenerFacturaPorId_Encontrada()` | facturaId=1 | Optional<Factura> | Factura encontrada | ✅ | - |
| 41 | Obtener factura inexistente | `FacturaServiceTest.testObtenerFacturaPorId_NoEncontrada()` | facturaId=999 | Optional.empty() | Optional vacío | ✅ | - |
| 42 | Eliminar factura | `FacturaServiceTest.testEliminarFactura()` | facturaId=1 | Factura eliminada de BD | Eliminación exitosa | ✅ | Operación admin |
| 43 | Obtener todas las facturas | `FacturaServiceTest.testObtenerTodasLasFacturas()` | - | Lista completa de facturas | 2 facturas obtenidas | ✅ | Vista admin global |
| **IMPUESTO SERVICE - Cálculo por País** |
| 44 | Obtener porcentaje España | `ImpuestoServiceTest.testObtenerPorcentajeImpuesto_España()` | pais="España" | 0.21 (21%) | 0.21 | ✅ | IVA España |
| 45 | Obtener porcentaje México | `ImpuestoServiceTest.testObtenerPorcentajeImpuesto_Mexico()` | pais="México" | 0.16 (16%) | 0.16 | ✅ | IVA México |
| 46 | Obtener porcentaje Francia | `ImpuestoServiceTest.testObtenerPorcentajeImpuesto_Francia()` | pais="Francia" | 0.20 (20%) | 0.20 | ✅ | IVA Francia |
| 47 | Obtener porcentaje Argentina | `ImpuestoServiceTest.testObtenerPorcentajeImpuesto_Argentina()` | pais="Argentina" | 0.21 (21%) | 0.21 | ✅ | IVA Argentina |
| 48 | Obtener porcentaje Estados Unidos | `ImpuestoServiceTest.testObtenerPorcentajeImpuesto_EstadosUnidos()` | pais="Estados Unidos" | 0.07 (7%) | 0.07 | ✅ | Sales Tax promedio USA |
| 49 | País no configurado usa default | `ImpuestoServiceTest.testObtenerPorcentajeImpuesto_PaisNoConfigurado()` | pais="PaisDesconocido" | 0.15 (15%) DEFAULT | 0.15 | ✅ | Fallback para países sin configurar |
| **IMPUESTO SERVICE - Cálculos** |
| 50 | Calcular impuesto España | `ImpuestoServiceTest.testCalcularImpuesto_España()` | montoBruto=100.00, pais="España" | 21.00 | 21.00 | ✅ | 100 * 0.21 = 21 |
| 51 | Calcular impuesto México | `ImpuestoServiceTest.testCalcularImpuesto_Mexico()` | montoBruto=100.00, pais="México" | 16.00 | 16.00 | ✅ | 100 * 0.16 = 16 |
| 52 | Calcular impuesto con decimales | `ImpuestoServiceTest.testCalcularImpuesto_ConDecimales()` | montoBruto=9.99, pais="España" | 2.10 (redondeado) | 2.10 | ✅ | Redondeo HALF_UP a 2 decimales |
| 53 | Calcular monto total España | `ImpuestoServiceTest.testCalcularMontoTotal_España()` | montoBruto=100.00 | 121.00 (bruto + impuesto) | 121.00 | ✅ | 100 + 21 = 121 |
| 54 | Calcular monto total México | `ImpuestoServiceTest.testCalcularMontoTotal_Mexico()` | montoBruto=100.00 | 116.00 | 116.00 | ✅ | 100 + 16 = 116 |
| 55 | Calcular total con decimales | `ImpuestoServiceTest.testCalcularMontoTotal_ConDecimales()` | montoBruto=9.99, pais="España" | 12.09 | 12.09 | ✅ | 9.99 + 2.10 = 12.09 |
| 56 | Calcular total país no configurado | `ImpuestoServiceTest.testCalcularMontoTotal_PaisNoConfigurado()` | montoBruto=100.00, pais desconocido | 115.00 (con 15% default) | 115.00 | ✅ | Usa impuesto DEFAULT |
| 57 | Obtener mapa completo de impuestos | `ImpuestoServiceTest.testObtenerTodosLosImpuestos()` | - | Map con todos los países configurados | Map con España, México, Francia, etc. | ✅ | Para configuración/admin |
| **IMPUESTO SERVICE - Precisión** |
| 58 | Redondeo hacia arriba | `ImpuestoServiceTest.testPrecision_RedondeoArriba()` | montoBruto=10.555, España | 2.22 (10.555*0.21=2.21655→2.22) | 2.22 | ✅ | HALF_UP redondea 0.005→0.01 |
| 59 | Redondeo hacia abajo | `ImpuestoServiceTest.testPrecision_RedondeoAbajo()` | montoBruto=10.544, España | 2.21 (10.544*0.21=2.21424→2.21) | 2.21 | ✅ | HALF_UP redondea 0.004→0.00 |
| 60 | Impuesto sobre monto cero | `ImpuestoServiceTest.testCero_ImpuestoCero()` | montoBruto=0.00 | 0.00 | 0.00 | ✅ | Edge case: sin monto = sin impuesto |
| **RENOVACION SERVICE - Proceso Automático** |
| 61 | Renovación automática todas exitosas | `RenovacionServiceTest.testRenovarSuscripcionesAutomaticamente_TodasExitosas()` | 2 suscripciones vencidas | 2 renovadas exitosamente, 0 morosas | 2 renovaciones exitosas | ✅ | Happy path del cron job |
| 62 | Renovación automática con 1 fallo | `RenovacionServiceTest.testRenovarSuscripcionesAutomaticamente_UnaFalla()` | 2 suscripciones, 1 falla pago | 1 renovada, 1 marcada MOROSA | Fallo manejado correctamente | ✅ | Manejo de errores de pago |
| 63 | Renovación automática todas fallan | `RenovacionServiceTest.testRenovarSuscripcionesAutomaticamente_TodasFallan()` | 2 suscripciones, ambas fallan | 0 renovadas, 2 marcadas MOROSA | Todas marcadas como morosas | ✅ | Peor escenario manejado |
| 64 | Renovación sin suscripciones vencidas | `RenovacionServiceTest.testRenovarSuscripcionesAutomaticamente_SinSuscripciones()` | Lista vacía | Ninguna operación | Proceso completado sin errores | ✅ | Nada que renovar |
| 65 | Error al marcar como morosa | `RenovacionServiceTest.testRenovarSuscripcionesAutomaticamente_ErrorAlMarcarMorosa()` | Fallo en renovación + fallo al marcar morosa | Error loggeado, proceso continúa | No lanza exception, loggea error | ✅ | Resilencia del proceso |
| 66 | Renovación manual forzada | `RenovacionServiceTest.testForzarRenovaciones()` | Llamada manual al método | Ejecuta el proceso de renovación | Proceso ejecutado | ✅ | Para testing o emergencias |
| 67 | Múltiples renovaciones mixtas | `RenovacionServiceTest.testRenovarSuscripcionesAutomaticamente_MultiplesRenovacionesYFallos()` | 3 suscripciones: 2 éxito + 1 fallo | 2 renovadas, 1 morosa, proceso continúa | Resultado esperado | ✅ | Escenario realista de producción |

---

## Cobertura de Casos Edge

### ✅ Casos Cubiertos:
- **Validaciones de Unicidad**: Username, Email duplicados
- **Estado de Entidades**: Suscripción ya activa, suscripción no activa al renovar
- **Null Safety**: Usuario sin perfil, búsquedas sin resultados
- **Cálculos Financieros**: Prorrateo proporcional, impuestos con decimales, redondeo HALF_UP
- **Errores de Negocio**: Cambio al mismo plan, renovación sin suscripción activa
- **Manejo de Fallos**: Renovación automática con errores, marcado como morosa
- **Colecciones Vacías**: Usuario sin facturas devuelve total 0, renovación sin suscripciones vencidas
- **Operaciones en Cascada**: Registro usuario crea perfil automático, crear suscripción genera factura
- **Países No Configurados**: Usa impuesto DEFAULT del 15%
- **Precisión Decimal**: Redondeo correcto a 2 decimales en cálculos financieros

### 🔍 Áreas No Cubiertas (Mejoras Futuras):
- Tests de integración con base de datos real (H2 embedded)
- Tests de API REST con MockMvc (@WebMvcTest)
- Tests de seguridad con @WithMockUser
- Tests de validación de formularios (@Valid)
- Tests de transacciones con @Transactional rollback
- Tests de Hibernate Envers (auditoría de cambios)
- Tests de scheduled tasks con TestExecutionListener
- Tests de rendimiento/carga

---

## Tecnologías Utilizadas

- **JUnit 5** (Jupiter): Framework de testing
- **Mockito**: Mocking de dependencias (@Mock, @InjectMocks)
- **AssertJ**: Assertions fluidas (`assertThat()`)
- **Maven Surefire Plugin**: Ejecución de tests
- **Spring Boot Test**: Soporte para testing de aplicaciones Spring

---

## Comandos de Ejecución

### Ejecutar todos los tests
```bash
mvn test
```

### Ejecutar tests de un servicio específico
```bash
mvn test -Dtest=UsuarioServiceTest
mvn test -Dtest=SuscripcionServiceTest
mvn test -Dtest=FacturaServiceTest
```

### Ejecutar un test específico
```bash
mvn test -Dtest=UsuarioServiceTest#testRegistrarUsuario_Exitoso
```

### Generar reporte de cobertura (opcional)
```bash
mvn jacoco:report
```

---

## Notas de Implementación

### Estrategia de Testing:
- **Unit Tests con Mockito**: Todas las dependencias externas (repositorios, servicios) son mockeadas
- **No se usa base de datos real**: Tests aislados y rápidos
- **Given-When-Then**: Estructura clara de arrange-act-assert
- **Uso de @BeforeEach**: Setup de datos de prueba reutilizables
- **Verificación de interacciones**: `verify()` para asegurar que se llaman métodos esperados

### Convenciones:
- Tests nombrados con patrón: `test[Método]_[Escenario]()`
- Datos de prueba en variables privadas del test class
- Uso de BigDecimal para montos financieros (precisión)
- Mockeo de respuestas con `when().thenReturn()` y `when().thenThrow()`

### Lecciones Aprendidas:
- **TipoPlan es enum**: Tests inicialmente fallaron por usar String en lugar de enum
- **Precisión decimal**: BigDecimal necesita `.setScale(2, RoundingMode.HALF_UP)` para redondeo correcto
- **Transacciones**: @Transactional no necesario en unit tests (solo mocks)
- **Fecha/Hora**: LocalDateTime.now() puede causar flakiness; considerar Clock mock para tests temporales

---

## Mantenimiento

### Cuando agregar nuevos tests:
1. ✅ Nueva funcionalidad de negocio
2. ✅ Bug encontrado en producción (regression test)
3. ✅ Cambio en lógica de cálculo (prorrateo, impuestos)
4. ✅ Nueva validación de datos

### Cuando modificar tests existentes:
1. ❌ Cambio en requirements de negocio
2. ❌ Refactoring de firma de métodos
3. ❌ Cambio en estructura de datos (ej: TipoPlan String → Enum)

---

**Última actualización:** 18 de febrero de 2026  
**Autor:** Equipo de Desarrollo FinalSpringBoot  
**Estado:** ✅ Todos los tests pasando
