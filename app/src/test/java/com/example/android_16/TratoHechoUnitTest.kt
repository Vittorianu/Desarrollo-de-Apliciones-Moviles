package com.example.android_16

import com.example.android_16.data.LocalDatabaseRepository
import com.example.android_16.model.EstadoCotizacion
import com.example.android_16.model.ItemCotizacion
import com.example.android_16.model.Moneda
import com.example.android_16.viewmodel.CotizacionViewModel
import com.example.android_16.viewmodel.IndicadoresViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.math.RoundingMode

class TratoHechoUnitTest {

    @Test
    fun testCalculoNetoImpuestoYTotalItemCLP() {
        // Item: qty 2, price 100,000 CLP, discount 10%, IVA 19%
        val item = ItemCotizacion(
            id = "1",
            descripcion = "Servicio Test",
            unidad = "UN",
            cantidad = 2.0,
            precioUnitario = 100000.0,
            porcentajeDescuento = 10.0,
            porcentajeImpuesto = 19.0,
            esExento = false
        )

        // Net = 2 * 100000 * 0.9 = 180000 CLP
        val neto = item.calcularNeto(Moneda.CLP.decimales)
        assertEquals(BigDecimal("180000"), neto)

        // Tax = 180000 * 0.19 = 34200 CLP
        val impuesto = item.calcularImpuesto(Moneda.CLP.decimales)
        assertEquals(BigDecimal("34200"), impuesto)

        // Total Item = 180000 + 34200 = 214200 CLP
        val totalItem = item.calcularTotalItem(Moneda.CLP.decimales)
        assertEquals(BigDecimal("214200"), totalItem)
    }

    @Test
    fun testCalculoDecimalUSD() {
        // USD uses 2 decimals with HALF_UP rounding
        val item = ItemCotizacion(
            id = "2",
            descripcion = "Software SaaS",
            unidad = "LIC",
            cantidad = 3.0,
            precioUnitario = 125.55,
            porcentajeDescuento = 5.0,
            porcentajeImpuesto = 0.0,
            esExento = true
        )

        // Net = 3 * 125.55 * 0.95 = 357.8175 -> rounded to 2 decimals HALF_UP = 357.82 USD
        val neto = item.calcularNeto(Moneda.USD.decimales)
        assertEquals(BigDecimal("357.82"), neto)
    }

    @Test
    fun testFórmulaTasaAceptacionIndicadores() {
        val repo = LocalDatabaseRepository
        val viewModel = IndicadoresViewModel()

        val list = repo.cotizaciones.value
        val resumen = viewModel.calcularIndicadores(list)

        assertNotNull(resumen)
        assertTrue(resumen.tasaAceptacionPorcentaje >= 0.0)
    }

    @Test
    fun testDuplicarCotizacion() {
        val repo = LocalDatabaseRepository
        val original = repo.cotizaciones.value.first()

        // Authenticate user first
        repo.autenticar("juan@prismatec.cl", "1234")

        val copia = repo.duplicarCotizacion(original.id)
        assertNotNull(copia)
        assertEquals(EstadoCotizacion.BORRADOR, copia?.estadoComercial)
        assertTrue(copia?.id != original.id)
    }
}
