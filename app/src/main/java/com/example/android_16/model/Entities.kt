package com.example.android_16.model

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

data class Empresa(
    val id: String,
    val razonSocial: String,
    val rut: String,
    val direccion: String,
    val telefono: String,
    val emailEmisor: String
)

data class Usuario(
    val id: String,
    val empresaId: String,
    val nombre: String,
    val email: String,
    val rol: RolUsuario,
    val equipoId: String? = null,
    val activo: Boolean = true
)

data class Contacto(
    val id: String,
    val nombre: String,
    val email: String,
    val telefono: String
)

data class Cliente(
    val id: String,
    val empresaId: String,
    val usuarioPropietarioId: String,
    val razonSocial: String,
    val rut: String,
    val direccion: String = "",
    val latitud: Double? = null,
    val longitud: Double? = null,
    val imagenUri: String? = null,
    val contactos: List<Contacto> = emptyList(),
    val activo: Boolean = true,
    val estadoSync: EstadoSincronizacion = EstadoSincronizacion.SINCRONIZADO
)

data class ItemCotizacion(
    val id: String,
    val descripcion: String,
    val unidad: String,
    val cantidad: Double,
    val precioUnitario: Double,
    val porcentajeDescuento: Double = 0.0, // 0 - 100
    val porcentajeImpuesto: Double = 19.0,  // e.g. 19% IVA or 0%
    val esExento: Boolean = false
) {
    fun calcularNeto(decimales: Int): BigDecimal {
        val qty = BigDecimal.valueOf(cantidad)
        val price = BigDecimal.valueOf(precioUnitario)
        val discFactor = BigDecimal.ONE.subtract(BigDecimal.valueOf(porcentajeDescuento / 100.0))
        return qty.multiply(price).multiply(discFactor).setScale(decimales, RoundingMode.HALF_UP)
    }

    fun calcularImpuesto(decimales: Int): BigDecimal {
        if (esExento || porcentajeImpuesto <= 0) return BigDecimal.ZERO.setScale(decimales, RoundingMode.HALF_UP)
        val neto = calcularNeto(decimales)
        val taxFactor = BigDecimal.valueOf(porcentajeImpuesto / 100.0)
        return neto.multiply(taxFactor).setScale(decimales, RoundingMode.HALF_UP)
    }

    fun calcularTotalItem(decimales: Int): BigDecimal {
        return calcularNeto(decimales).add(calcularImpuesto(decimales)).setScale(decimales, RoundingMode.HALF_UP)
    }
}

data class Cotizacion(
    val id: String,
    val folio: String, // e.g. "COT-2026-001" or temp ID
    val empresaId: String,
    val usuarioPropietarioId: String,
    val clienteId: String,
    val clienteNombre: String,
    val contactoEmail: String,
    val fechaCreacion: LocalDate = LocalDate.now(),
    val fechaVencimiento: LocalDate = LocalDate.now().plusDays(30),
    val moneda: Moneda = Moneda.CLP,
    val items: List<ItemCotizacion> = emptyList(),
    val valorConversionCLP: Double = 1.0, // Valor indicador si USD u UF
    val fechaIndicador: LocalDate = LocalDate.now(),
    val fuenteIndicador: String = "Banco Central / Mindecon",
    val estadoComercial: EstadoCotizacion = EstadoCotizacion.BORRADOR,
    val estadoSync: EstadoSincronizacion = EstadoSincronizacion.SINCRONIZADO,
    val observaciones: String = ""
) {
    val subtotalNeto: BigDecimal
        get() = items.fold(BigDecimal.ZERO) { acc, item -> acc.add(item.calcularNeto(moneda.decimales)) }

    val totalImpuestos: BigDecimal
        get() = items.fold(BigDecimal.ZERO) { acc, item -> acc.add(item.calcularImpuesto(moneda.decimales)) }

    val totalCotizacion: BigDecimal
        get() = subtotalNeto.add(totalImpuestos)

    val equivalenciaCLP: BigDecimal
        get() {
            if (moneda == Moneda.CLP) return totalCotizacion.setScale(0, RoundingMode.HALF_UP)
            return totalCotizacion.multiply(BigDecimal.valueOf(valorConversionCLP)).setScale(0, RoundingMode.HALF_UP)
        }
}

data class OrdenCompra(
    val id: String,
    val cotizacionId: String,
    val numeroOrden: String,
    val fecha: LocalDate,
    val observaciones: String = ""
)

data class Factura(
    val id: String,
    val cotizacionId: String,
    val folioFactura: String,
    val fecha: LocalDate,
    val montoTotal: Double
)

data class ImpuestoConfig(
    val id: String,
    val nombre: String,
    val porcentaje: Double,
    val activo: Boolean = true
)

data class TipoUnidadConfig(
    val id: String,
    val codigo: String,
    val nombre: String,
    val activo: Boolean = true
)

data class IndicadorEconomico(
    val moneda: Moneda,
    val valorCLP: Double,
    val fecha: LocalDate,
    val fuente: String
)

sealed class Resultado<out T> {
    data class Exito<out T>(val datos: T) : Resultado<T>()
    data class Error(val mensaje: String) : Resultado<Nothing>()
    object Cargando : Resultado<Nothing>()
}

sealed class ResultadoLogin {
    data class Exito(val usuario: Usuario, val empresa: Empresa) : ResultadoLogin()
    data class Error(val mensaje: String) : ResultadoLogin()
    object Autenticando : ResultadoLogin()
}
