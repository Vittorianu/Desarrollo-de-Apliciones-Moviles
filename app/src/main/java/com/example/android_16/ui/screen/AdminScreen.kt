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
import androidx.compose.material3.FilterChip
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
import com.example.android_16.model.RolUsuario
import com.example.android_16.ui.components.ValidatedTextField
import com.example.android_16.viewmodel.AdminViewModel

@Composable
fun AdminScreen(
    viewModel: AdminViewModel,
    onVolver: () -> Unit
) {
    val usuarios by viewModel.usuarios.collectAsState()
    val empresa by viewModel.empresa.collectAsState()
    val state by viewModel.uiState.collectAsState()

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
                text = "Administración de la Empresa",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // Issuer Config Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "DATOS DEL EMISOR", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = empresa?.razonSocial ?: "", fontWeight = FontWeight.Bold)
                    Text(text = "RUT: ${empresa?.rut ?: ""}")
                    Text(text = "Dirección: ${empresa?.direccion ?: ""}")
                    Text(text = "Email Emisor: ${empresa?.emailEmisor ?: ""}")
                }
            }

            // Create User Section
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Crear Nuevo Usuario para la Empresa", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    ValidatedTextField(
                        value = state.nombreNuevoUsuario,
                        onValueChange = viewModel::onNombreChanged,
                        label = "Nombre del Usuario *",
                        error = state.errorNombre
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ValidatedTextField(
                        value = state.emailNuevoUsuario,
                        onValueChange = viewModel::onEmailChanged,
                        label = "Correo Electrónico *",
                        error = state.errorEmail
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Asignar Perfil / Rol:", fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        RolUsuario.values().forEach { rol ->
                            FilterChip(
                                selected = state.rolNuevoUsuario == rol,
                                onClick = { viewModel.onRolChanged(rol) },
                                label = { Text(rol.titulo) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.crearUsuario() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Crear Usuario")
                    }

                    AnimatedVisibility(visible = state.mensajeExito != null) {
                        state.mensajeExito?.let { msg ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = msg, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Current Users List
            Text(text = "Usuarios Registrados (${usuarios.size})", fontWeight = FontWeight.Bold)

            usuarios.forEach { usr ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = usr.nombre, fontWeight = FontWeight.Bold)
                        Text(text = "Email: ${usr.email} | Rol: ${usr.rol.titulo}", style = MaterialTheme.typography.bodyMedium)
                    }
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
