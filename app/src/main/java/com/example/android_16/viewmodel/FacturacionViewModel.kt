package com.example.android_16.viewmodel

import androidx.lifecycle.ViewModel
import com.example.android_16.data.LocalDatabaseRepository
import com.example.android_16.model.Cotizacion
import com.example.android_16.model.EstadoCotizacion
import com.example.android_16.model.Factura
import com.example.android_16.model.OrdenCompra
import com.example.android_16.model.Resultado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

data class FacturacionFormState(
    val cotizacionSeleccionada: Cotizacion? = null,
    val numeroOrdenCompra: String = "",
    val obsOrdenCompra: String = "",
    val folioFactura: String = "",
    val montoFactura: String = "",
    val errorOrden: String? = null,
    val errorFactura: String? = null,
    val mensajeExito: String? = null
)

class FacturacionViewModel : ViewModel() {

    private val repository = LocalDatabaseRepository

    val cotizacionesAceptadas: StateFlow<List<Cotizacion>> = repository.cotizaciones
    val ordenesCompra: StateFlow<List<OrdenCompra>> = repository.ordenesCompra
    val facturas: StateFlow<List<Factura>> = repository.facturas

    private val _formState = MutableStateFlow(FacturacionFormState())
    val formState: StateFlow<FacturacionFormState> = _formState.asStateFlow()

    fun seleccionarCotizacion(cot: Cotizacion) {
        _formState.update { 
            it.copy(
                cotizacionSeleccionada = cot,
                montoFactura = cot.totalCotizacion.toPlainString(),
                errorOrden = null,
                errorFactura = null,
                mensajeExito = null
            ) 
        }
    }

    fun onNumeroOrdenChanged(valor: String) {
        _formState.update { it.copy(numeroOrdenCompra = valor, errorOrden = null) }
    }

    fun onObsOrdenChanged(valor: String) {
        _formState.update { it.copy(obsOrdenCompra = valor) }
    }

    fun onFolioFacturaChanged(valor: String) {
        _formState.update { it.copy(folioFactura = valor, errorFactura = null) }
    }

    fun onMontoFacturaChanged(valor: String) {
        _formState.update { it.copy(montoFactura = valor, errorFactura = null) }
    }

    fun registrarOrdenCompra(): Boolean {
        val state = _formState.value
        val cot = state.cotizacionSeleccionada ?: return false

        if (state.numeroOrdenCompra.isBlank()) {
            _formState.update { it.copy(errorOrden = "El número de orden de compra es obligatorio") }
            return false
        }

        val orden = OrdenCompra(
            id = "OC-${System.currentTimeMillis()}",
            cotizacionId = cot.id,
            numeroOrden = state.numeroOrdenCompra,
            fecha = LocalDate.now(),
            observaciones = state.obsOrdenCompra
        )

        val res = repository.registrarOrdenCompra(orden)
        return when (res) {
            is Resultado.Exito -> {
                _formState.update { it.copy(mensajeExito = res.datos, numeroOrdenCompra = "", obsOrdenCompra = "") }
                true
            }
            is Resultado.Error -> {
                _formState.update { it.copy(errorOrden = res.mensaje) }
                false
            }
            Resultado.Cargando -> false
        }
    }

    fun registrarFactura(): Boolean {
        val state = _formState.value
        val cot = state.cotizacionSeleccionada ?: return false

        if (state.folioFactura.isBlank()) {
            _formState.update { it.copy(errorFactura = "El folio de la factura es obligatorio") }
            return false
        }

        val monto = state.montoFactura.toDoubleOrNull()
        if (monto == null || monto <= 0) {
            _formState.update { it.copy(errorFactura = "Monto de factura inválido") }
            return false
        }

        val factura = Factura(
            id = "FAC-${System.currentTimeMillis()}",
            cotizacionId = cot.id,
            folioFactura = state.folioFactura,
            fecha = LocalDate.now(),
            montoTotal = monto
        )

        val res = repository.registrarFactura(factura)
        return when (res) {
            is Resultado.Exito -> {
                _formState.update { it.copy(mensajeExito = res.datos, folioFactura = "", cotizacionSeleccionada = null) }
                true
            }
            is Resultado.Error -> {
                _formState.update { it.copy(errorFactura = res.mensaje) }
                false
            }
            Resultado.Cargando -> false
        }
    }
}
