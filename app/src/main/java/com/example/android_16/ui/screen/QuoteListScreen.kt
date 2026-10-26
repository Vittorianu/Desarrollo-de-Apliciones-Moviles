package com.example.android_16.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.android_16.model.Cotizacion
import com.example.android_16.model.EstadoCotizacion
import com.example.android_16.ui.components.StatusBadge
import com.example.android_16.ui.components.SyncBadge
import com.example.android_16.viewmodel.CotizacionViewModel

@Composable
fun QuoteListScreen(
    viewModel: CotizacionViewModel,
    onNuevaCotizacion: () -> Unit,
    onVerDetalleCotizacion: (Cotizacion) -> Unit,
    onEditarBorrador: (Cotizacion) -> Unit
) {
    val cotizaciones by viewModel.cotizaciones.collectAsState()
    val filtroEstado by viewModel.filtroEstado.collectAsState()
    val busqueda by viewModel.busqueda.collectAsState()

    val cotizacionesFiltradas = cotizaciones.filter { cot ->
        val coincideEstado = filtroEstado == null || cot.estadoComercial == filtroEstado
        val query = busqueda.lowercase()
        val coincideBusqueda = query.isBlank() ||
                cot.folio.lowercase().contains(query) ||
                cot.clienteNombre.lowercase().contains(query)
        coincideEstado && coincideBusqueda
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.prepararNuevoBorrador()
                    onNuevaCotizacion()
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Cotización")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = busqueda,
                onValueChange = viewModel::onBusquedaChanged,
                label = { Text("Buscar por Folio o Cliente") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Filter Chips Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = filtroEstado == null,
                        onClick = { viewModel.onFiltroEstadoChanged(null) },
                        label = { Text("Todos (${cotizaciones.size})") }
                    )
                }
                items(EstadoCotizacion.values()) { est ->
                    val cant = cotizaciones.count { it.estadoComercial == est }
                    FilterChip(
                        selected = filtroEstado == est,
                        onClick = { viewModel.onFiltroEstadoChanged(est) },
                        label = { Text("${est.etiqueta} ($cant)") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (cotizacionesFiltradas.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No hay cotizaciones para mostrar",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cotizacionesFiltradas) { cot ->
                        Card(
                            onClick = {
                                if (cot.estadoComercial == EstadoCotizacion.BORRADOR) {
                                    viewModel.cargarCotizacionParaEdicion(cot)
                                    onEditarBorrador(cot)
                                } else {
                                    onVerDetalleCotizacion(cot)
                                }
                            },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = cot.folio,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    StatusBadge(estado = cot.estadoComercial)
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = cot.clienteNombre,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Total: ${cot.moneda.simbolo} ${cot.totalCotizacion.toPlainString()} ${cot.moneda.codigo}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    SyncBadge(syncState = cot.estadoSync)
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    if (cot.estadoComercial == EstadoCotizacion.BORRADOR) {
                                        TextButton(onClick = {
                                            viewModel.cargarCotizacionParaEdicion(cot)
                                            onEditarBorrador(cot)
                                        }) {
                                            Text("Editar Borrador")
                                        }
                                    }
                                    TextButton(onClick = { onVerDetalleCotizacion(cot) }) {
                                        Text("Ver Detalle / PDF")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
