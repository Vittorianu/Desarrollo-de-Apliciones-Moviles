# DOCUMENTO DE JUSTIFICACIÓN TÉCNICA Y REQUISITOS
## Proyecto Trato Hecho — Aplicación Móvil Nativa en Kotlin (DSY1105)

---

### 1. INFORMACIÓN DEL PROYECTO
* **Aplicación**: Trato Hecho — Cotizaciones Móviles SaaS
* **Plataforma**: Android Nativa (Kotlin + Jetpack Compose)
* **Asignatura**: Desarrollo de Aplicaciones Móviles (DSY1105)
* **Caso de Estudio**: Consultora Prismatec Ltda. — Plataforma SaaS Multi-Tenant Trato Hecho
* **Arquitectura**: Clean Architecture + MVVM (Model-View-ViewModel) + StateFlow
* **Versión de SDK**: Min SDK API 24+ / Target SDK API 36

---

### 2. RESUMEN DE LA SOLUCIÓN Y ALCANCE

La aplicación móvil nativa **Trato Hecho** permite a los usuarios comerciales de empresas clientes (*tenants*) gestionar clientes, armar cotizaciones en borrador en terreno (incluso sin conexión a internet), realizar cálculos exactos con impuestos, descuentos y conversión de divisas, enviar propuestas formales en PDF mediante la API existente, y dar seguimiento a las cotizaciones hasta la facturación.

#### Matriz de Roles y Permisos Implementada:
1. **Administrador**: Parametriza catálogos (impuestos, unidades), configura el emisor de la empresa, gestiona usuarios de su tenant y consulta todas las cotizaciones de la empresa.
2. **Supervisor**: Visualiza y monitorea los clientes y cotizaciones del equipo asignado, consultando los indicadores de su equipo.
3. **Usuario (Comercial / Vendedor)**: Registra y gestiona sus propios clientes y contactos, crea y edita borradores propios, solicita el envío por correo de sus cotizaciones y consulta sus indicadores propios.
4. **Facturación**: Consulta cotizaciones en estado **ACEPTADA**, registra referencias de **Órdenes de Compra (OC)** y **Facturas**, cambiando el estado comercial a **FACTURADA**.

---

### 3. CUMPLIMIENTO DETALLADO DE INDICADORES DE LOGRO DE LA RÚBRICA (IE)

#### IE 2.1.1 — Diseño de Interfaz Coherente, Jerarquía Visual y Navegación Functional
* **Implementación**: Se desarrolló una interfaz reactiva utilizando **Jetpack Compose** y **Material Design 3**.
* **Navegación**: Administrada de forma centralizada con `NavHost` (`androidx.navigation.compose`) en `AppNavigation.kt`, integrando una barra superior (`TopAppBar`) con información del tenant y perfil activo, y una barra inferior de navegación (`NavigationBar`) adaptativa según el rol del usuario.
* **Componentes**: Pantallas modulares (`LoginScreen`, `DashboardScreen`, `ClientListScreen`, `ClientFormScreen`, `QuoteListScreen`, `QuoteFormScreen`, `QuoteDetailScreen`, `BillingScreen`, `IndicatorScreen`, `AdminScreen`).

#### IE 2.1.2 — Formularios Completos con Validaciones Visuales por Campo e Íconos
* **Implementación**: Reutilización del componente `ValidatedTextField.kt` en todos los formularios (Login, Registro/Edición de Clientes, Borrador de Cotización, Registro de OC/Factura y Creación de Usuarios).
* **Retroalimentación Visual**: Cada campo valida en tiempo real y muestra estado `isError = true`, íconos de advertencia/error (`Icons.Default.Warning`), y mensajes aclaratorios en el texto de soporte (`supportingText`) con el color de error del tema (`MaterialTheme.colorScheme.error`).

#### IE 2.2.1 — Gestión de Lógica de Validación Centralizada y Desacoplada (MVVM)
* **Implementación**: La lógica de negocio y las reglas de validación están totalmente desacopladas de las pantallas de Compose y contenidas en los ViewModels (`AuthViewModel`, `ClienteViewModel`, `CotizacionViewModel`, `FacturacionViewModel`, `IndicadoresViewModel`, `AdminViewModel`).
* **Manejo de Estado**: Se utiliza `MutableStateFlow` y `StateFlow.asStateFlow()` expuestos mediante `collectAsState()` en la UI. Las pantallas únicamente invocan métodos del ViewModel y renderizan el estado inmutable (`UiState`).

#### IE 2.2.2 — Animaciones Visuales Funcionales
* **Transiciones y Paneles**: Uso de `AnimatedVisibility` para la aparición/desaparición fluida de avisos de error, banners de advertencia de modo sin conexión y mensajes de confirmación de guardado.
* **Animación de Colores**: Uso de `animateColorAsState` con `tween(durationMillis = 400)` en el componente `StatusBadge.kt` y `SyncBadge.kt` para animar suavemente la transición de colores de los estados comerciales (*Borrador*, *Enviada*, *Aceptada*, *Rechazada*, *Facturada*) y de los estados de sincronización (*Sincronizado*, *Pendiente*, *Error*, *Conflicto*).

#### IE 2.3.1 — Estructura Modular del Proyecto y Persistencia Local
* **Estructura de Paquetes**:
  * `com.example.android_16.model`: Entidades, data classes, sealed classes (`Resultado`, `ResultadoLogin`) y enums (`RolUsuario`, `EstadoCotizacion`, `Moneda`, `EstadoSincronizacion`).
  * `com.example.android_16.data`: Repositorio local singleton `LocalDatabaseRepository` y persistencia con `DataStore Preferences` (`TratoHechoDataStore`).
  * `com.example.android_16.viewmodel`: ViewModels por módulo funcional.
  * `com.example.android_16.ui.components`: Componentes visuales reutilizables (`StatusBadge`, `ValidatedTextField`, `StatCard`).
  * `com.example.android_16.ui.screen`: Vistas Compose.
  * `com.example.android_16.navigation`: Grafo de navegación `AppNavigation.kt`.
* **Persistencia Local**: `TratoHechoDataStore` persiste preferencias, id de usuario en sesión, estado de modo offline y marcas de tiempo de sincronización. `LocalDatabaseRepository` gestiona la persistencia reactiva en memoria y almacén local para garantizar el trabajo sin conexión.

#### IE 2.3.2 — Herramientas Colaborativas y Control de Versiones
* **Estrategia de Control de Versiones**:
  * Control de cambios con Git/GitHub mediante commits estructurados por componente (model, data, viewmodel, ui, navigation, tests).
  * Planificación y distribución de tareas organizada en tableros Trello con asignación de tarjetas por miembro de equipo.

#### IE 2.4.1 & IE 2.4.2 — Acceso Funcional a Recursos Nativos del Dispositivo
1. **Recurso Nativo 1: Geolocalización (GPS / FusedLocationServices)**:
   * **Ubicación**: En `ClientFormScreen.kt`.
   * **Funcionamiento**: Solicita permisos nativos `ACCESS_FINE_LOCATION` y `ACCESS_COARSE_LOCATION` mediante `ActivityResultContracts.RequestMultiplePermissions()`.
   * **Integración Visual**: Utiliza `LocationServices.getFusedLocationProviderClient(context)` para obtener latitud y longitud de la visita en terreno, mostrando la información en una tarjeta visual con ícono GPS en la ficha del cliente.
2. **Recurso Nativo 2: Cámara y Galería de Fotos**:
   * **Ubicación**: En `ClientFormScreen.kt`.
   * **Funcionamiento**: Solicita permiso `CAMERA` mediante `ActivityResultContracts.RequestPermission()` y utiliza `FileProvider` con `res/xml/file_paths.xml` para capturar fotos con la cámara nativa (`ActivityResultContracts.TakePicture()`) o seleccionar imágenes de la galería (`ActivityResultContracts.PickVisualMedia()`).
   * **Integración Visual**: Renderiza la imagen capturada en la interfaz utilizando la librería **Coil** (`AsyncImage`) con bordes redondeados (`RoundedCornerShape`).

---

### 4. REGLAS DE NEGOCIO Y CÁLCULOS MONETARIOS DE PRECISIÓN

Conforme a los requisitos numéricos del caso (RN-06 a RN-09):
1. **Cálculo Neto del Ítem**: `Neto = Cantidad × PrecioUnitario × (1 - Descuento / 100)`, redondeado a los decimales correspondientes según la moneda (`HALF_UP`).
2. **Cálculo de Impuestos**: `Impuesto = Neto × (TasaImpuesto / 100)` (0% en ítems exentos), redondeado con `HALF_UP`.
3. **Manejo de Monedas**:
   * **CLP**: 0 decimales.
   * **USD**: 2 decimales.
   * **UF**: 4 decimales.
4. **Conversión a CLP**: La equivalencia en CLP de cotizaciones en USD o UF se calcula multiplicando el total por la tasa de conversión registrada del indicador económico, redondeando a enteros (`HALF_UP`).
5. **Indicadores de Negocio**:
   * **Tasa de Aceptación**: `(Aceptadas + Facturadas) / (Aceptadas + Facturadas + Rechazadas) × 100`.
   * **Montos por Moneda**: Se suman estrictamente por tipo de moneda (CLP, USD, UF) de forma independiente, sin sumarlos directamente entre monedas distintas.

---

### 5. PRUEBAS UNITARIAS AUTOMATIZADAS
Se implementó la suite de pruebas unitarias `TratoHechoUnitTest.kt`, verificando:
* Exactitud en cálculos netos, impuestos y totales de ítems.
* Precisión decimal y redondeo `HALF_UP` en dólares (USD).
* Ejecución de la fórmula de Tasa de Aceptación Comercial.
* Duplicación exitosa de cotizaciones a nuevos borradores independientes.

**Resultado de Pruebas**: 5 ejecutadas, 5 aprobadas (100% de éxito).
