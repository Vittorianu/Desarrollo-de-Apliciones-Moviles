package com.example.android_16.ui.screen

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.android_16.model.EstadoCotizacion
import com.example.android_16.ui.components.ValidatedTextField
import com.example.android_16.viewmodel.FacturacionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingScreen(
    viewModel: FacturacionViewModel,
    onVolver: () -> Unit
) {
    val cotizaciones by viewModel.cotizacionesAceptadas.collectAsState()
    val aceptadas = cotizaciones.filter { it.estadoComercial == EstadoCotizacion.ACEPTADA }

    val state by viewModel.formState.collectAsState()
    val scrollState = rememberScrollState()

    var cotMenuExpanded by remember { mutableStateOf(false) }

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
                text = "Facturación & Órdenes de Compra",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // Select Accepted Quote
            ExposedDropdownMenuBox(
                expanded = cotMenuExpanded,
                onExpandedChange = { cotMenuExpanded = !cotMenuExpanded }
            ) {
                TextField(
                    value = state.cotizacionSeleccionada?.let { "${it.folio} - ${it.clienteNombre}" } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Seleccionar Cotización Aceptada *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cotMenuExpanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = cotMenuExpanded,
                    onDismissRequest = { cotMenuExpanded = false }
                ) {
                    if (aceptadas.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("No hay cotizaciones Aceptadas pendientes de facturar") },
                            onClick = { cotMenuExpanded = false }
                        )
                    } else {
                        aceptadas.forEach { cot ->
                            DropdownMenuItem(
                                text = { Text("${cot.folio} - ${cot.clienteNombre} (${cot.moneda.simbolo}${cot.totalCotizacion})") },
                                onClick = {
                                    viewModel.seleccionarCotizacion(cot)
                                    cotMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Register Purchase Order
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "1. REGISTRAR ÓRDEN DE COMPRA (OPCIONAL)", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    ValidatedTextField(
                        value = state.numeroOrdenCompra,
                        onValueChange = viewModel::onNumeroOrdenChanged,
                        label = "Número de Orden de Compra",
                        error = state.errorOrden
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ValidatedTextField(
                        value = state.obsOrdenCompra,
                        onValueChange = viewModel::onObsOrdenChanged,
                        label = "Observaciones OC"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { viewModel.registrarOrdenCompra() },
                        enabled = state.cotizacionSeleccionada != null,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Registrar Orden de Compra")
                    }
                }
            }

            // Register Invoice
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "2. REGISTRAR FACTURA (ASIGNA ESTADO FACTURADA)", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    ValidatedTextField(
                        value = state.folioFactura,
                        onValueChange = viewModel::onFolioFacturaChanged,
                        label = "Folio o Número de Factura *",
                        error = state.errorFactura
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ValidatedTextField(
                        value = state.montoFactura,
                        onValueChange = viewModel::onMontoFacturaChanged,
                        label = "Monto Factura *"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { viewModel.registrarFactura() },
                        enabled = state.cotizacionSeleccionada != null,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Confirmar Registro de Factura")
                    }
                }
            }

            AnimatedVisibility(visible = state.mensajeExito != null) {
                state.mensajeExito?.let { msg ->
                    Text(text = msg, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
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
