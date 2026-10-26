package com.example.android_16.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.android_16.model.Cotizacion
import com.example.android_16.model.EstadoCotizacion
import com.example.android_16.model.Moneda
import com.example.android_16.model.Resultado
import com.example.android_16.ui.components.StatusBadge
import com.example.android_16.ui.components.SyncBadge
import com.example.android_16.viewmodel.CotizacionViewModel

@Composable
fun QuoteDetailScreen(
    cotizacionId: String,
    viewModel: CotizacionViewModel,
    onVolver: () -> Unit
) {
    val cotizaciones by viewModel.cotizaciones.collectAsState()
    val cot = cotizaciones.find { it.id == cotizacionId }

    var mostrandoModalPdf by remember { mutableStateOf(false) }
    var mensajeRespuesta by remember { mutableStateOf<String?>(null) }
    var esErrorRespuesta by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    if (cot == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Cotización no encontrada")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onVolver) { Text("Volver") }
        }
        return
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = cot.folio,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Fecha: ${cot.fechaCreacion}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    StatusBadge(estado = cot.estadoComercial)
                    Spacer(modifier = Modifier.height(4.dp))
                    SyncBadge(syncState = cot.estadoSync)
                }
            }

            // Client Info Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "DATOS DEL CLIENTE", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = cot.clienteNombre, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                    Text(text = "Destinatario: ${cot.contactoEmail}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Vencimiento: ${cot.fechaVencimiento}", style = MaterialTheme.typography.bodySmall)
                }
            }

            // Document Breakdown
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "DETALLE DE ÍTEMS", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    cot.items.forEachIndexed { idx, item ->
                        val neto = item.calcularNeto(cot.moneda.decimales)
                        val tax = item.calcularImpuesto(cot.moneda.decimales)
                        val tot = item.calcularTotalItem(cot.moneda.decimales)

                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            Text(text = "${idx + 1}. ${item.descripcion}", fontWeight = FontWeight.SemiBold)
                            Text(
                                text = "Cant: ${item.cantidad} ${item.unidad} x ${cot.moneda.simbolo}${item.precioUnitario} (Desc: ${item.porcentajeDescuento}%)",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Neto: ${cot.moneda.simbolo}$neto | Impuesto: ${cot.moneda.simbolo}$tax | Subtotal: ${cot.moneda.simbolo}$tot",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Totals
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal Neto:")
                        Text("${cot.moneda.simbolo} ${cot.subtotalNeto}")
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Impuestos:")
                        Text("${cot.moneda.simbolo} ${cot.totalImpuestos}")
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("TOTAL COTIZACIÓN:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(
                            text = "${cot.moneda.simbolo} ${cot.totalCotizacion} ${cot.moneda.codigo}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (cot.moneda != Moneda.CLP) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Equivalencia en Pesos: \$${cot.equivalenciaCLP} CLP (Indicador \$${cot.valorConversionCLP})",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Message Banner
            AnimatedVisibility(visible = mensajeRespuesta != null) {
                mensajeRespuesta?.let { msg ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = if (esErrorRespuesta) MaterialTheme.colorScheme.errorContainer else Color(0xFFE8F5E9),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            text = msg,
                            color = if (esErrorRespuesta) MaterialTheme.colorScheme.onErrorContainer else Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Action Buttons
            Button(
                onClick = { mostrandoModalPdf = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Send, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Vista Previa PDF & Enviar por Correo")
            }

            OutlinedButton(
                onClick = {
                    val nueva = viewModel.duplicarCotizacion(cot.id)
                    if (nueva != null) {
                        mensajeRespuesta = "Cotización duplicada como nuevo Borrador ${nueva.folio}."
                        esErrorRespuesta = false
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Duplicar como Nuevo Borrador")
            }

            // Web Simulation Controls for Evaluation
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Simular Respuesta Web de la Plataforma:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.simularRespuestaWebPlataforma(cot.id, aceptada = true)
                                mensajeRespuesta = "Estado actualizado a ACEPTADA desde la plataforma web."
                                esErrorRespuesta = false
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Aceptar Web", fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                viewModel.simularRespuestaWebPlataforma(cot.id, aceptada = false)
                                mensajeRespuesta = "Estado actualizado a RECHAZADA desde la plataforma web."
                                esErrorRespuesta = true
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Rechazar Web", fontSize = 11.sp)
                        }
                    }
                }
            }

            TextButton(
                onClick = onVolver,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("← Volver a la Lista")
            }
        }
    }

    // PDF Preview Modal Dialog
    if (mostrandoModalPdf) {
        Dialog(onDismissRequest = { mostrandoModalPdf = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Vista Previa Documento PDF",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "DOCUMENTO FORMAL DE COTIZACIÓN", fontWeight = FontWeight.Bold, color = Color.Gray)
                            Text(text = cot.folio, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Emisor: Soluciones Industriales Prismatec S.A.")
                            Text(text = "Cliente: ${cot.clienteNombre}")
                            Text(text = "Destinatario: ${cot.contactoEmail}")
                            Text(text = "Moneda: ${cot.moneda.codigo}")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "TOTAL: ${cot.moneda.simbolo} ${cot.totalCotizacion}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { mostrandoModalPdf = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cerrar")
                        }

                        Button(
                            onClick = {
                                val res = viewModel.enviarCotizacion()
                                when (res) {
                                    is Resultado.Exito -> {
                                        mensajeRespuesta = res.datos
                                        esErrorRespuesta = false
                                        mostrandoModalPdf = false
                                    }
                                    is Resultado.Error -> {
                                        mensajeRespuesta = res.mensaje
                                        esErrorRespuesta = true
                                    }
                                    Resultado.Cargando -> {}
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Email, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Confirmar Envío")
                        }
                    }
                }
            }
        }
    }
}
