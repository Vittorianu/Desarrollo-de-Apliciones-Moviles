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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android_16.model.RolUsuario
import com.example.android_16.model.Usuario
import com.example.android_16.ui.components.ValidatedTextField
import com.example.android_16.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginExitoso: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Logo / Title Banner
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(20.dp))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Trato Hecho Logo",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "TRATO HECHO",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Cotizaciones Móviles SaaS",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Form Card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Iniciar Sesión",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                ValidatedTextField(
                    value = state.email,
                    onValueChange = viewModel::onEmailChanged,
                    label = "Correo Electrónico",
                    error = state.emailError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    trailingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                ValidatedTextField(
                    value = state.clave,
                    onValueChange = viewModel::onClaveChanged,
                    label = "Contraseña",
                    error = state.claveError,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = { Icon(Icons.Default.Lock, contentDescription = null) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Error General Banner (AnimatedVisibility - IE 2.2.2)
                AnimatedVisibility(visible = state.errorGeneral != null) {
                    state.errorGeneral?.let { msg ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.errorContainer, shape = RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = msg,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                Button(
                    onClick = {
                        if (viewModel.iniciarSesion()) {
                            onLoginExitoso()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(text = "ACCEDER A MI EMPRESA", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Offline Toggle Control
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Simulación Modo Offline", fontWeight = FontWeight.SemiBold)
                    Text(
                        text = if (state.modoOfflineActivo) "Sin conexión (Trabajo local)" else "En línea con API Trato Hecho",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (state.modoOfflineActivo) Color(0xFFEF6C00) else Color(0xFF2E7D32)
                    )
                }
                Switch(
                    checked = state.modoOfflineActivo,
                    onCheckedChange = { viewModel.alternarModoOffline() }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Fast Profile Switcher for Evaluation Defense / Testing
        Text(
            text = "Acceso Rápido por Perfil (Modo Evaluación):",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    viewModel.cambiarPerfilPrototipo(
                        Usuario("USR-03", "EMP-001", "Juan Vendedor", "juan@prismatec.cl", RolUsuario.USUARIO)
                    )
                    onLoginExitoso()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Entrar como VENDEDOR (Usuario)")
            }

            OutlinedButton(
                onClick = {
                    viewModel.cambiarPerfilPrototipo(
                        Usuario("USR-01", "EMP-001", "Carlos Admin", "admin@prismatec.cl", RolUsuario.ADMINISTRADOR)
                    )
                    onLoginExitoso()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Entrar como ADMINISTRADOR")
            }

            OutlinedButton(
                onClick = {
                    viewModel.cambiarPerfilPrototipo(
                        Usuario("USR-04", "EMP-001", "María Facturación", "facturacion@prismatec.cl", RolUsuario.FACTURACION)
                    )
                    onLoginExitoso()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Entrar como FACTURACIÓN")
            }
        }
    }
}
