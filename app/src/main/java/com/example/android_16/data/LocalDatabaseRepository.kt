package com.example.android_16.data

import com.example.android_16.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

object LocalDatabaseRepository {

    // --- Data Caches ---
    private val _empresa = MutableStateFlow<Empresa?>(
        Empresa(
            id = "EMP-001",
            razonSocial = "Soluciones Industriales Prismatec S.A.",
            rut = "76.543.210-K",
            direccion = "Av. Paicaví 1280, Concepción",
            telefono = "+56 41 234 5678",
            emailEmisor = "ventas@prismatec.cl"
        )
    )
    val empresa: StateFlow<Empresa?> = _empresa.asStateFlow()

    // Pre-populated Users for role testing
    private val usuariosIniciales = listOf(
        Usuario("USR-01", "EMP-001", "Carlos Admin", "admin@prismatec.cl", RolUsuario.ADMINISTRADOR),
        Usuario("USR-02", "EMP-001", "Sofía Supervisor", "supervisor@prismatec.cl", RolUsuario.SUPERVISOR, equipoId = "EQ-NORTH"),
        Usuario("USR-03", "EMP-001", "Juan Vendedor", "juan@prismatec.cl", RolUsuario.USUARIO, equipoId = "EQ-NORTH"),
        Usuario("USR-04", "EMP-001", "María Facturación", "facturacion@prismatec.cl", RolUsuario.FACTURACION)
    )

    private val _usuarios = MutableStateFlow<List<Usuario>>(usuariosIniciales)
    val usuarios: StateFlow<List<Usuario>> = _usuarios.asStateFlow()

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    // Pre-populated Clients
    private val clientesIniciales = listOf(
        Cliente(
            id = "CLI-101",
            empresaId = "EMP-001",
            usuarioPropietarioId = "USR-03",
            razonSocial = "Constructora El Roble SpA",
            rut = "77.123.456-7",
            direccion = "Aníbal Pinto 450, Concepción",
            latitud = -36.8270,
            longitud = -73.0503,
            contactos = listOf(
                Contacto("CON-1", "Roberto Gómez", "rgomez@elroble.cl", "+56 9 8765 4321"),
                Contacto("CON-2", "Ana Morales", "amorales@elroble.cl", "+56 9 1234 5678")
            )
        ),
        Cliente(
            id = "CLI-102",
            empresaId = "EMP-001",
            usuarioPropietarioId = "USR-03",
            razonSocial = "Minera San Pedro Ltda.",
            rut = "88.987.654-3",
            direccion = "Ruta 160 Km 12, Coronel",
            latitud = -36.9333,
            longitud = -73.1500,
            contactos = listOf(
                Contacto("CON-3", "Pedro Soto", "psoto@sanpedro.cl", "+56 9 9988 7766")
            )
        )
    )

    private val _clientes = MutableStateFlow<List<Cliente>>(clientesIniciales)
    val clientes: StateFlow<List<Cliente>> = _clientes.asStateFlow()

    // Pre-populated Quotes
    private val cotizacionesIniciales = listOf(
        Cotizacion(
            id = "COT-1001",
            folio = "COT-2026-001",
            empresaId = "EMP-001",
            usuarioPropietarioId = "USR-03",
            clienteId = "CLI-101",
            clienteNombre = "Constructora El Roble SpA",
            contactoEmail = "rgomez@elroble.cl",
            fechaCreacion = LocalDate.now().minusDays(5),
            moneda = Moneda.CLP,
            items = listOf(
                ItemCotizacion("ITM-1", "Suministro e instalación de sensores IoT", "UN", 2.0, 450000.0, porcentajeDescuento = 5.0, porcentajeImpuesto = 19.0),
                ItemCotizacion("ITM-2", "Mantenimiento preventivo anual", "SER", 1.0, 300000.0, porcentajeDescuento = 0.0, porcentajeImpuesto = 19.0)
            ),
            estadoComercial = EstadoCotizacion.ENVIADA,
            estadoSync = EstadoSincronizacion.SINCRONIZADO
        ),
        Cotizacion(
            id = "COT-1002",
            folio = "COT-2026-002",
            empresaId = "EMP-001",
            usuarioPropietarioId = "USR-03",
            clienteId = "CLI-102",
            clienteNombre = "Minera San Pedro Ltda.",
            contactoEmail = "psoto@sanpedro.cl",
            fechaCreacion = LocalDate.now().minusDays(2),
            moneda = Moneda.USD,
            valorConversionCLP = 950.0,
            fechaIndicador = LocalDate.now().minusDays(2),
            items = listOf(
                ItemCotizacion("ITM-3", "Licencia de Software SaaS Trato Hecho Enterprise", "LIC", 1.0, 1200.0, porcentajeDescuento = 10.0, esExento = true)
            ),
            estadoComercial = EstadoCotizacion.ACEPTADA,
            estadoSync = EstadoSincronizacion.SINCRONIZADO
        )
    )

    private val _cotizaciones = MutableStateFlow<List<Cotizacion>>(cotizacionesIniciales)
    val cotizaciones: StateFlow<List<Cotizacion>> = _cotizaciones.asStateFlow()

    private val _ordenesCompra = MutableStateFlow<List<OrdenCompra>>(emptyList())
    val ordenesCompra: StateFlow<List<OrdenCompra>> = _ordenesCompra.asStateFlow()

    private val _facturas = MutableStateFlow<List<Factura>>(emptyList())
    val facturas: StateFlow<List<Factura>> = _facturas.asStateFlow()

    // Economic Indicators Cache
    private val _indicadores = MutableStateFlow(
        listOf(
            IndicadorEconomico(Moneda.USD, 955.50, LocalDate.now(), "Banco Central de Chile"),
            IndicadorEconomico(Moneda.UF, 38420.15, LocalDate.now(), "Servicio de Impuestos Internos")
        )
    )
    val indicadores: StateFlow<List<IndicadorEconomico>> = _indicadores.asStateFlow()

    // Config Catálogos
    val impuestosDisponibles = listOf(
        ImpuestoConfig("IMP-19", "IVA 19%", 19.0),
        ImpuestoConfig("IMP-0", "Exento de IVA", 0.0)
    )

    val unidadesDisponibles = listOf(
        TipoUnidadConfig("UNI-1", "UN", "Unidad"),
        TipoUnidadConfig("UNI-2", "SER", "Servicio"),
        TipoUnidadConfig("UNI-3", "HRS", "Horas Hombre"),
        TipoUnidadConfig("UNI-4", "GLB", "Global")
    )

    // --- Authentication & Session Methods ---
    fun autenticar(email: String, clave: String): ResultadoLogin {
        val user = _usuarios.value.find { it.email.equals(email, ignoreCase = true) }
        return if (user != null && clave == "1234") { // Mock password
            _usuarioActual.value = user
            ResultadoLogin.Exito(user, _empresa.value!!)
        } else {
            ResultadoLogin.Error("Credenciales inválidas. Compruebe correo y contraseña (clave demo: 1234).")
        }
    }

    fun seleccionarUsuarioPrototipo(user: Usuario) {
        _usuarioActual.value = user
    }

    fun cerrarSesion() {
        _usuarioActual.value = null
    }

    // --- Client Operations ---
    fun agregarOActualizarCliente(cliente: Cliente, offline: Boolean = false): String? {
        val user = _usuarioActual.value ?: return "Usuario no autenticado"
        // RUT Duplicate Check
        val existente = _clientes.value.find { 
            it.empresaId == user.empresaId && it.rut == cliente.rut && it.id != cliente.id 
        }
        if (existente != null) {
            return "Ya existe un cliente registrado con el RUT ${cliente.rut} en la empresa."
        }

        val syncStatus = if (offline) EstadoSincronizacion.PENDIENTE else EstadoSincronizacion.SINCRONIZADO
        val clienteAguardar = cliente.copy(
            empresaId = user.empresaId,
            usuarioPropietarioId = if (cliente.id.isBlank()) user.id else cliente.usuarioPropietarioId,
            estadoSync = syncStatus
        )

        _clientes.update { list ->
            val index = list.indexOfFirst { it.id == clienteAguardar.id }
            if (index >= 0) {
                list.toMutableList().apply { set(index, clienteAguardar) }
            } else {
                list + clienteAguardar
            }
        }
        return null
    }

    fun desactivarCliente(clienteId: String) {
        _clientes.update { list ->
            list.map { if (it.id == clienteId) it.copy(activo = false) else it }
        }
    }

    // --- Quote Operations ---
    fun guardarBorradorCotizacion(cotizacion: Cotizacion, offline: Boolean = false) {
        val user = _usuarioActual.value ?: return
        val syncStatus = if (offline) EstadoSincronizacion.PENDIENTE else EstadoSincronizacion.SINCRONIZADO
        val cotToSave = cotizacion.copy(
            empresaId = user.empresaId,
            usuarioPropietarioId = if (cotizacion.id.isBlank()) user.id else cotizacion.usuarioPropietarioId,
            estadoComercial = EstadoCotizacion.BORRADOR,
            estadoSync = syncStatus
        )

        _cotizaciones.update { list ->
            val index = list.indexOfFirst { it.id == cotToSave.id }
            if (index >= 0) {
                list.toMutableList().apply { set(index, cotToSave) }
            } else {
                list + cotToSave
            }
        }
    }

    fun enviarCotizacion(cotizacionId: String, emailDestino: String): Resultado<String> {
        val cot = _cotizaciones.value.find { it.id == cotizacionId }
            ?: return Resultado.Error("Cotización no encontrada")

        if (cot.estadoComercial != EstadoCotizacion.BORRADOR) {
            return Resultado.Error("Solo se pueden enviar cotizaciones en estado Borrador")
        }
        if (cot.items.isEmpty()) {
            return Resultado.Error("Debe incluir al menos un ítem para enviar la cotización")
        }
        if (emailDestino.isBlank() || !emailDestino.contains("@")) {
            return Resultado.Error("Ingrese un correo destinatario válido")
        }

        // Transición a ENVIADA (RN-11)
        _cotizaciones.update { list ->
            list.map {
                if (it.id == cotizacionId) {
                    it.copy(
                        contactoEmail = emailDestino,
                        estadoComercial = EstadoCotizacion.ENVIADA,
                        estadoSync = EstadoSincronizacion.SINCRONIZADO
                    )
                } else it
            }
        }
        return Resultado.Exito("Cotización ${cot.folio} enviada exitosamente a $emailDestino con PDF adjunto.")
    }

    fun duplicarCotizacion(cotizacionId: String): Cotizacion? {
        val cot = _cotizaciones.value.find { it.id == cotizacionId } ?: return null
        val user = _usuarioActual.value ?: return null
        val nuevoId = "COT-${System.currentTimeMillis().toString().takeLast(6)}"
        val nuevoFolio = "COT-2026-${(100..999).random()}"

        val copia = cot.copy(
            id = nuevoId,
            folio = nuevoFolio,
            usuarioPropietarioId = user.id,
            fechaCreacion = LocalDate.now(),
            fechaVencimiento = LocalDate.now().plusDays(30),
            estadoComercial = EstadoCotizacion.BORRADOR,
            estadoSync = EstadoSincronizacion.SINCRONIZADO
        )

        _cotizaciones.update { it + copia }
        return copia
    }

    fun descartarBorrador(cotizacionId: String): Boolean {
        val cot = _cotizaciones.value.find { it.id == cotizacionId } ?: return false
        if (cot.estadoComercial != EstadoCotizacion.BORRADOR) return false

        _cotizaciones.update { list -> list.filterNot { it.id == cotizacionId } }
        return true
    }

    fun actualizarEstadoWebPlataforma(cotizacionId: String, nuevoEstado: EstadoCotizacion) {
        // Simula actualización recibida desde la API web existente
        _cotizaciones.update { list ->
            list.map {
                if (it.id == cotizacionId && (nuevoEstado == EstadoCotizacion.ACEPTADA || nuevoEstado == EstadoCotizacion.RECHAZADA)) {
                    it.copy(estadoComercial = nuevoEstado)
                } else it
            }
        }
    }

    // --- Billing Operations ---
    fun registrarOrdenCompra(orden: OrdenCompra): Resultado<String> {
        val cot = _cotizaciones.value.find { it.id == orden.cotizacionId }
            ?: return Resultado.Error("Cotización asociada no encontrada")

        if (cot.estadoComercial != EstadoCotizacion.ACEPTADA) {
            return Resultado.Error("Solo se pueden registrar órdenes de compra sobre cotizaciones ACEPTADAS")
        }

        _ordenesCompra.update { it + orden }
        return Resultado.Exito("Orden de compra N° ${orden.numeroOrden} registrada correctamente.")
    }

    fun registrarFactura(factura: Factura): Resultado<String> {
        val cot = _cotizaciones.value.find { it.id == factura.cotizacionId }
            ?: return Resultado.Error("Cotización asociada no encontrada")

        if (cot.estadoComercial != EstadoCotizacion.ACEPTADA) {
            return Resultado.Error("Solo se puede facturar una cotización en estado ACEPTADA")
        }

        // RN-13 & DA-15: Confirmar factura pasa estado a FACTURADA
        _facturas.update { it + factura }
        _cotizaciones.update { list ->
            list.map {
                if (it.id == factura.cotizacionId) {
                    it.copy(estadoComercial = EstadoCotizacion.FACTURADA)
                } else it
            }
        }
        return Resultado.Exito("Factura N° ${factura.folioFactura} registrada. Cotización paso a estado FACTURADA.")
    }

    // --- Sync Operations ---
    fun sincronizarPendientes(): Int {
        var count = 0
        _clientes.update { list ->
            list.map {
                if (it.estadoSync == EstadoSincronizacion.PENDIENTE) {
                    count++
                    it.copy(estadoSync = EstadoSincronizacion.SINCRONIZADO)
                } else it
            }
        }
        _cotizaciones.update { list ->
            list.map {
                if (it.estadoSync == EstadoSincronizacion.PENDIENTE) {
                    count++
                    it.copy(estadoSync = EstadoSincronizacion.SINCRONIZADO)
                } else it
            }
        }
        return count
    }
}
