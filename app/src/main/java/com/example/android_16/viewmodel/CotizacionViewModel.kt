package com.example.android_16.viewmodel

import androidx.lifecycle.ViewModel
import com.example.android_16.data.LocalDatabaseRepository
import com.example.android_16.model.Cliente
import com.example.android_16.model.Cotizacion
import com.example.android_16.model.EstadoCotizacion
import com.example.android_16.model.ItemCotizacion
import com.example.android_16.model.Moneda
import com.example.android_16.model.Resultado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

data class CotizacionFormState(
    val id: String = "",
    val folio: String = "",
    val clienteSeleccionado: Cliente? = null,
    val contactoEmail: String = "",
    val moneda: Moneda = Moneda.CLP,
    val items: List<ItemCotizacion> = emptyList(),
    val valorConversionCLP: Double = 1.0,
    val observaciones: String = "",
    val errorCliente: String? = null,
    val errorEmail: String? = null,
    val errorItems: String? = null,
    val errorGeneral: String? = null,
    val mensajeExito: String? = null,
    val mostrandoVistaPreviaPDF: Boolean = false
)

class CotizacionViewModel : ViewModel() {

    private val repository = LocalDatabaseRepository

    private val _filtroEstado = MutableStateFlow<EstadoCotizacion?>(null)
    val filtroEstado: StateFlow<EstadoCotizacion?> = _filtroEstado.asStateFlow()

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    private val _formState = MutableStateFlow(CotizacionFormState())
    val formState: StateFlow<CotizacionFormState> = _formState.asStateFlow()

    val cotizaciones: StateFlow<List<Cotizacion>> = repository.cotizaciones
    val clientes: StateFlow<List<Cliente>> = repository.clientes
    val indicadores = repository.indicadores

    fun onFiltroEstadoChanged(estado: EstadoCotizacion?) {
        _filtroEstado.value = estado
    }

    fun onBusquedaChanged(query: String) {
        _busqueda.value = query
    }

    fun prepararNuevoBorrador(clienteInicial: Cliente? = null) {
        val user = repository.usuarioActual.value
        val folioTemp = "BORR-${System.currentTimeMillis().toString().takeLast(6)}"
        val email = clienteInicial?.contactos?.firstOrNull()?.email ?: ""

        _formState.value = CotizacionFormState(
            id = "COT-${System.currentTimeMillis()}",
            folio = folioTemp,
            clienteSeleccionado = clienteInicial,
            contactoEmail = email,
            moneda = Moneda.CLP,
            items = listOf(
                ItemCotizacion(
                    id = "ITM-${System.currentTimeMillis()}",
                    descripcion = "Producto / Servicio Demo",
                    unidad = "UN",
                    cantidad = 1.0,
                    precioUnitario = 100000.0,
                    porcentajeDescuento = 0.0,
                    porcentajeImpuesto = 19.0
                )
            )
        )
    }

    fun cargarCotizacionParaEdicion(cot: Cotizacion) {
        val clienteObj = repository.clientes.value.find { it.id == cot.clienteId }
        _formState.value = CotizacionFormState(
            id = cot.id,
            folio = cot.folio,
            clienteSeleccionado = clienteObj,
            contactoEmail = cot.contactoEmail,
            moneda = cot.moneda,
            items = cot.items,
            valorConversionCLP = cot.valorConversionCLP,
            observaciones = cot.observaciones
        )
    }

    fun seleccionarCliente(cliente: Cliente) {
        val defaultEmail = cliente.contactos.firstOrNull { it.email.isNotBlank() }?.email ?: ""
        _formState.update { 
            it.copy(clienteSeleccionado = cliente, contactoEmail = defaultEmail, errorCliente = null) 
        }
    }

    fun onContactoEmailChanged(email: String) {
        _formState.update { it.copy(contactoEmail = email, errorEmail = null) }
    }

    fun seleccionarMoneda(moneda: Moneda) {
        var conversion = 1.0
        if (moneda != Moneda.CLP) {
            val ind = repository.indicadores.value.find { it.moneda == moneda }
            if (ind != null) conversion = ind.valorCLP
        }
        _formState.update { it.copy(moneda = moneda, valorConversionCLP = conversion) }
    }

    fun onObservacionesChanged(obs: String) {
        _formState.update { it.copy(observaciones = obs) }
    }

    // --- Item Management ---
    fun agregarItem() {
        _formState.update { state ->
            val newItem = ItemCotizacion(
                id = "ITM-${System.currentTimeMillis()}",
                descripcion = "",
                unidad = "UN",
                cantidad = 1.0,
                precioUnitario = 0.0,
                porcentajeDescuento = 0.0,
                porcentajeImpuesto = 19.0
            )
            state.copy(items = state.items + newItem, errorItems = null)
        }
    }

    fun actualizarItem(index: Int, descripcion: String, unidad: String, cantidad: Double, precio: Double, descuento: Double, exento: Boolean) {
        _formState.update { state ->
            val list = state.items.toMutableList()
            if (index in list.indices) {
                list[index] = list[index].copy(
                    descripcion = descripcion,
                    unidad = unidad,
                    cantidad = if (cantidad < 0) 0.0 else cantidad,
                    precioUnitario = if (precio < 0) 0.0 else precio,
                    porcentajeDescuento = descuento.coerceIn(0.0, 100.0),
                    esExento = exento
                )
            }
            state.copy(items = list, errorItems = null)
        }
    }

    fun eliminarItem(index: Int) {
        _formState.update { state ->
            val list = state.items.toMutableList()
            if (index in list.indices) {
                list.removeAt(index)
            }
            state.copy(items = list)
        }
    }

    fun abrirVistaPreviaPDF(mostrar: Boolean) {
        _formState.update { it.copy(mostrandoVistaPreviaPDF = mostrar) }
    }

    fun guardarBorrador(modoOffline: Boolean = false): Boolean {
        val state = _formState.value
        val user = repository.usuarioActual.value ?: return false
        val cli = state.clienteSeleccionado

        val cot = Cotizacion(
            id = state.id,
            folio = if (state.folio.startsWith("BORR-")) "COT-2026-${(100..999).random()}" else state.folio,
            empresaId = user.empresaId,
            usuarioPropietarioId = user.id,
            clienteId = cli?.id ?: "",
            clienteNombre = cli?.razonSocial ?: "Cliente Sin Asignar",
            contactoEmail = state.contactoEmail,
            moneda = state.moneda,
            items = state.items,
            valorConversionCLP = state.valorConversionCLP,
            observaciones = state.observaciones,
            estadoComercial = EstadoCotizacion.BORRADOR
        )

        repository.guardarBorradorCotizacion(cot, offline = modoOffline)
        _formState.update { it.copy(mensajeExito = "Borrador guardado correctamente.") }
        return true
    }

    fun enviarCotizacion(): Resultado<String> {
        val state = _formState.value
        if (state.clienteSeleccionado == null) {
            _formState.update { it.copy(errorCliente = "Debe seleccionar un cliente activo") }
            return Resultado.Error("Seleccione un cliente activo")
        }
        if (state.contactoEmail.isBlank() || !state.contactoEmail.contains("@")) {
            _formState.update { it.copy(errorEmail = "Ingrese un correo válido para el envío") }
            return Resultado.Error("Correo destinatario inválido")
        }
        if (state.items.isEmpty()) {
            _formState.update { it.copy(errorItems = "Debe agregar al menos un ítem válido") }
            return Resultado.Error("Sin ítems en la cotización")
        }

        // Guardar primero
        guardarBorrador()

        // Solicitar envío al backend / repositorio
        val res = repository.enviarCotizacion(state.id, state.contactoEmail)
        if (res is Resultado.Exito) {
            _formState.update { it.copy(mensajeExito = res.datos, mostrandoVistaPreviaPDF = false) }
        }
        return res
    }

    fun duplicarCotizacion(cotizacionId: String): Cotizacion? {
        return repository.duplicarCotizacion(cotizacionId)
    }

    fun descartarBorrador(cotizacionId: String): Boolean {
        return repository.descartarBorrador(cotizacionId)
    }

    fun simularRespuestaWebPlataforma(cotizacionId: String, aceptada: Boolean) {
        val nuevoEst = if (aceptada) EstadoCotizacion.ACEPTADA else EstadoCotizacion.RECHAZADA
        repository.actualizarEstadoWebPlataforma(cotizacionId, nuevoEst)
    }
}
