package com.example.android_16.viewmodel

import androidx.lifecycle.ViewModel
import com.example.android_16.data.LocalDatabaseRepository
import com.example.android_16.model.Cotizacion
import com.example.android_16.model.EstadoCotizacion
import com.example.android_16.model.Moneda
import kotlinx.coroutines.flow.StateFlow
import java.math.BigDecimal

data class ResumenIndicadores(
    val totalBorradores: Int = 0,
    val totalEnviadas: Int = 0,
    val totalAceptadas: Int = 0,
    val totalRechazadas: Int = 0,
    val totalFacturadas: Int = 0,
    val tasaAceptacionPorcentaje: Double = 0.0,
    val montoAceptadoFacturadoCLP: BigDecimal = BigDecimal.ZERO,
    val montoAceptadoFacturadoUSD: BigDecimal = BigDecimal.ZERO,
    val montoAceptadoFacturadoUF: BigDecimal = BigDecimal.ZERO
)

class IndicadoresViewModel : ViewModel() {

    private val repository = LocalDatabaseRepository

    val cotizaciones: StateFlow<List<Cotizacion>> = repository.cotizaciones

    fun calcularIndicadores(lista: List<Cotizacion>): ResumenIndicadores {
        val borradores = lista.count { it.estadoComercial == EstadoCotizacion.BORRADOR }
        val enviadas = lista.count { it.estadoComercial == EstadoCotizacion.ENVIADA }
        val aceptadas = lista.count { it.estadoComercial == EstadoCotizacion.ACEPTADA }
        val rechazadas = lista.count { it.estadoComercial == EstadoCotizacion.RECHAZADA }
        val facturadas = lista.count { it.estadoComercial == EstadoCotizacion.FACTURADA }

        // Tasa de Aceptación = (Aceptadas + Facturadas) / (Aceptadas + Facturadas + Rechazadas) * 100
        val numerador = (aceptadas + facturadas).toDouble()
        val denominador = (aceptadas + facturadas + rechazadas).toDouble()
        val tasa = if (denominador > 0) (numerador / denominador) * 100.0 else 0.0

        val aceptadasYFacturadas = lista.filter { 
            it.estadoComercial == EstadoCotizacion.ACEPTADA || it.estadoComercial == EstadoCotizacion.FACTURADA 
        }

        var sumCLP = BigDecimal.ZERO
        var sumUSD = BigDecimal.ZERO
        var sumUF = BigDecimal.ZERO

        for (cot in aceptadasYFacturadas) {
            when (cot.moneda) {
                Moneda.CLP -> sumCLP = sumCLP.add(cot.totalCotizacion)
                Moneda.USD -> sumUSD = sumUSD.add(cot.totalCotizacion)
                Moneda.UF -> sumUF = sumUF.add(cot.totalCotizacion)
            }
        }

        return ResumenIndicadores(
            totalBorradores = borradores,
            totalEnviadas = enviadas,
            totalAceptadas = aceptadas,
            totalRechazadas = rechazadas,
            totalFacturadas = facturadas,
            tasaAceptacionPorcentaje = tasa,
            montoAceptadoFacturadoCLP = sumCLP,
            montoAceptadoFacturadoUSD = sumUSD,
            montoAceptadoFacturadoUF = sumUF
        )
    }
}
