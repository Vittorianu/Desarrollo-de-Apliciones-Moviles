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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android_16.model.EstadoSincronizacion
import com.example.android_16.model.RolUsuario
import com.example.android_16.ui.components.StatCard
import com.example.android_16.viewmodel.AuthViewModel
import com.example.android_16.viewmodel.ClienteViewModel
import com.example.android_16.viewmodel.CotizacionViewModel

@Composable
fun DashboardScreen(
    authViewModel: AuthViewModel,
    clienteViewModel: ClienteViewModel,
    cotizacionViewModel: CotizacionViewModel,
    onNavigateToCotizaciones: () -> Unit,
    onNavigateToClientes: () -> Unit,
    onNavigateToNuevaCotizacion: () -> Unit,
    onNavigateToFacturacion: () -> Unit,
    onNavigateToIndicadores: () -> Unit
) {
    val authState by authViewModel.uiState.collectAsState()
    val cotizaciones by cotizacionViewModel.cotizaciones.collectAsState()
    val clientes by clienteViewModel.clientes.collectAsState()

    val usuario = authState.usuarioLogueado
    val empresa = authState.empresa

    val pendingSyncCount = cotizaciones.count { it.estadoSync == EstadoSincronizacion.PENDIENTE } +
            clientes.count { it.estadoSync == EstadoSincronizacion.PENDIENTE }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // User & Tenant Profile Banner
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = empresa?.razonSocial ?: "Empresa Cliente SaaS",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "RUT: ${empresa?.rut ?: ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = usuario?.rol?.titulo ?: "Usuario",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = usuario?.nombre ?: "Usuario Sesión",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Offline / Pending Sync Banner (AnimatedVisibility)
        AnimatedVisibility(visible = authState.modoOfflineActivo || pendingSyncCount > 0) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (authState.modoOfflineActivo) "Modo Sin Conexión Activo" else "Operaciones Pendientes",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100)
                        )
                        Text(
                            text = "$pendingSyncCount registros pendientes de sincronizar con el servidor",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFBF360C)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Sync",
                        tint = Color(0xFFE65100)
                    )
                }
            }
        }

        // Action Shortcuts
        Text(
            text = "Acciones Rápidas",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onNavigateToNuevaCotizacion,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Nueva Cotización", fontSize = 12.sp)
            }

            OutlinedButton(
                onClick = onNavigateToClientes,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Icon(Icons.Default.Person, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Clientes", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // KPI Summary Cards
        Text(
            text = "Resumen Comercial",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                titulo = "Cotizaciones",
                valor = "${cotizaciones.size}",
                subtitulo = "${cotizaciones.count { it.estadoComercial.name == "BORRADOR" }} borradores",
                modifier = Modifier.weight(1f)
            )

            StatCard(
                titulo = "Clientes",
                valor = "${clientes.size}",
                subtitulo = "${clientes.count { it.activo }} activos",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Feature Navigation Cards
        Card(
            onClick = onNavigateToCotizaciones,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Cotizaciones & Presupuestos", fontWeight = FontWeight.Bold)
                    Text(text = "Crear, editar borradores, calcular y enviar PDF", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        if (usuario?.rol == RolUsuario.FACTURACION || usuario?.rol == RolUsuario.ADMINISTRADOR) {
            Card(
                onClick = onNavigateToFacturacion,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF6A1B9A))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Registro de Ordenes & Facturas", fontWeight = FontWeight.Bold)
                        Text(text = "Registrar OC y facturar cotizaciones aceptadas", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        Card(
            onClick = onNavigateToIndicadores,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF2E7D32))
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Indicadores del Negocio", fontWeight = FontWeight.Bold)
                    Text(text = "Tasa de aceptación y totales por moneda", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
