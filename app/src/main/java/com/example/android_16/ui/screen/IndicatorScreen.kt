package com.example.android_16.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android_16.ui.components.StatCard
import com.example.android_16.viewmodel.IndicadoresViewModel
import java.util.Locale

@Composable
fun IndicatorScreen(
    viewModel: IndicadoresViewModel,
    onVolver: () -> Unit
) {
    val cotizaciones by viewModel.cotizaciones.collectAsState()
    val resumen = viewModel.calcularIndicadores(cotizaciones)
    val scrollState = rememberScrollState()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Indicadores del Negocio",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // Acceptance Rate Metric
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "TASA DE ACEPTACIÓN COMERCIAL", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = String.format(Locale.US, "%.1f%%", resumen.tasaAceptacionPorcentaje),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { (resumen.tasaAceptacionPorcentaje / 100.0).toFloat().coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Fórmula: (Aceptadas + Facturadas) / (Aceptadas + Facturadas + Rechazadas) * 100",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Amounts Per Currency (Strictly Isolated - No Cross-Currency Summation)
            Text(text = "Montos Aceptados & Facturados por Moneda", fontWeight = FontWeight.Bold)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard(
                    titulo = "Total CLP",
                    valor = "$ ${resumen.montoAceptadoFacturadoCLP}",
                    subtitulo = "Pesos Chilenos",
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    titulo = "Total USD",
                    valor = "US$ ${resumen.montoAceptadoFacturadoUSD}",
                    subtitulo = "Dólares EE.UU.",
                    modifier = Modifier.weight(1f)
                )
            }

            StatCard(
                titulo = "Total UF",
                valor = "UF ${resumen.montoAceptadoFacturadoUF}",
                subtitulo = "Unidades de Fomento"
            )

            // Counts by Commercial State
            Text(text = "Volumen por Estado Comercial", fontWeight = FontWeight.Bold)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard(
                    titulo = "Borradores",
                    valor = "${resumen.totalBorradores}",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    titulo = "Enviadas",
                    valor = "${resumen.totalEnviadas}",
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard(
                    titulo = "Aceptadas",
                    valor = "${resumen.totalAceptadas}",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    titulo = "Rechazadas",
                    valor = "${resumen.totalRechazadas}",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    titulo = "Facturadas",
                    valor = "${resumen.totalFacturadas}",
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedButton(
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver")
            }
        }
    }
}
