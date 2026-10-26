package com.example.android_16.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.android_16.ui.components.ValidatedTextField
import com.example.android_16.viewmodel.ClienteViewModel
import com.google.android.gms.location.LocationServices
import java.io.File

@Composable
fun ClientFormScreen(
    viewModel: ClienteViewModel,
    modoOffline: Boolean = false,
    onGuardadoExitoso: () -> Unit,
    onCancelar: () -> Unit
) {
    val state by viewModel.formState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    var mostrarDialogoPermisoCamara by remember { mutableStateOf(false) }
    var mostrarDialogoPermisoUbicacion by remember { mutableStateOf(false) }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // --- Native Resource 1: Geolocation Launchers & Helper ---
    @SuppressLint("MissingPermission")
    fun capturarUbicacionGps() {
        fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
            if (loc != null) {
                viewModel.onUbicacionCapturada(loc.latitude, loc.longitude)
            } else {
                // Fallback mock coordinates for emulator/indoor testing
                viewModel.onUbicacionCapturada(-36.8270, -73.0503)
            }
        }.addOnFailureListener {
            viewModel.onUbicacionCapturada(-36.8270, -73.0503)
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val fineG = perms[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseG = perms[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineG || coarseG) {
            capturarUbicacionGps()
        } else {
            mostrarDialogoPermisoUbicacion = true
        }
    }

    fun solicitarUbicacion() {
        val fineCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)

        if (fineCheck == PackageManager.PERMISSION_GRANTED || coarseCheck == PackageManager.PERMISSION_GRANTED) {
            capturarUbicacionGps()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    // --- Native Resource 2: Camera & Gallery Launchers & Helper ---
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { viewModel.onImagenCapturadaOSeleccionada(it.toString()) }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            viewModel.onImagenCapturadaOSeleccionada(tempCameraUri.toString())
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = crearTempPictureUri(context)
            tempCameraUri = uri
            cameraLauncher.launch(uri)
        } else {
            mostrarDialogoPermisoCamara = true
        }
    }

    fun solicitarCamara() {
        val check = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (check == PackageManager.PERMISSION_GRANTED) {
            val uri = crearTempPictureUri(context)
            tempCameraUri = uri
            cameraLauncher.launch(uri)
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
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
            Text(
                text = if (state.id.isBlank()) "Registrar Nuevo Cliente" else "Editar Cliente",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            ValidatedTextField(
                value = state.razonSocial,
                onValueChange = viewModel::onRazonSocialChanged,
                label = "Nombre / Razón Social *",
                error = state.errorRazonSocial
            )

            ValidatedTextField(
                value = state.rut,
                onValueChange = viewModel::onRutChanged,
                label = "Identificador Tributario (RUT) *",
                error = state.errorRut
            )

            ValidatedTextField(
                value = state.direccion,
                onValueChange = viewModel::onDireccionChanged,
                label = "Dirección Comercial (Opcional)"
            )

            // --- NATIVE RESOURCE 1: Geolocation Section ---
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = "GPS", tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Ubicación Geográfica en Terreno", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (state.latitud != null && state.longitud != null) {
                        Text(
                            text = "Coordenadas GPS: Lat ${state.latitud}, Lng ${state.longitud}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Text(
                            text = "No se han capturado coordenadas GPS para la visita.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { solicitarUbicacion() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Capturar Coordenadas GPS")
                    }
                }
            }

            // --- NATIVE RESOURCE 2: Camera & Gallery Image Attachment ---
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Foto de Perfil / Fachada / Visita en Terreno",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (state.imagenUri != null) {
                            AsyncImage(
                                model = state.imagenUri,
                                contentDescription = "Foto Cliente",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = "Sin imagen adjunta",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        OutlinedButton(onClick = {
                            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }) {
                            Icon(Icons.Default.AccountBox, contentDescription = "Galería")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Galería")
                        }

                        OutlinedButton(onClick = { solicitarCamara() }) {
                            Icon(Icons.Default.Edit, contentDescription = "Cámara")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Cámara")
                        }
                    }
                }
            }

            // --- Contact List Section ---
            Text(
                text = "Contactos del Cliente",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            state.contactos.forEachIndexed { index, contacto ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "Contacto #${index + 1}", style = MaterialTheme.typography.labelMedium)
                        Spacer(modifier = Modifier.height(4.dp))

                        ValidatedTextField(
                            value = contacto.nombre,
                            onValueChange = { viewModel.onContactoNombreChanged(index, it) },
                            label = "Nombre Contacto"
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        ValidatedTextField(
                            value = contacto.email,
                            onValueChange = { viewModel.onContactoEmailChanged(index, it) },
                            label = "Correo Electrónico",
                            error = if (index == 0) state.errorContactoEmail else null
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        ValidatedTextField(
                            value = contacto.telefono,
                            onValueChange = { viewModel.onContactoTelefonoChanged(index, it) },
                            label = "Teléfono"
                        )
                    }
                }
            }

            TextButton(
                onClick = { viewModel.agregarContacto() },
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Agregar Otro Contacto")
            }

            // Error General Banner
            AnimatedVisibility(visible = state.errorGeneral != null) {
                state.errorGeneral?.let { msg ->
                    Text(
                        text = msg,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCancelar,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }

                Button(
                    onClick = {
                        if (viewModel.guardarCliente(modoOffline = modoOffline)) {
                            onGuardadoExitoso()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Guardar Cliente")
                }
            }
        }
    }

    if (mostrarDialogoPermisoCamara) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoPermisoCamara = false },
            title = { Text("Permiso de Cámara Requerido") },
            text = { Text("Para capturar fotos con la cámara del dispositivo es necesario otorgar el permiso. ¿Desea solicitarlo nuevamente?") },
            confirmButton = {
                Button(onClick = {
                    mostrarDialogoPermisoCamara = false
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }) {
                    Text("Solicitar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoPermisoCamara = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (mostrarDialogoPermisoUbicacion) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoPermisoUbicacion = false },
            title = { Text("Permiso de Ubicación Requerido") },
            text = { Text("Para registrar la ubicación GPS de la visita es necesario otorgar el permiso de ubicación. ¿Desea solicitarlo nuevamente?") },
            confirmButton = {
                Button(onClick = {
                    mostrarDialogoPermisoUbicacion = false
                    locationPermissionLauncher.launch(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                    )
                }) {
                    Text("Solicitar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoPermisoUbicacion = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

private fun crearTempPictureUri(context: Context): Uri {
    val tempFile = File.createTempFile("cliente_foto_", ".jpg", context.cacheDir).apply {
        createNewFile()
        deleteOnExit()
    }
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        tempFile
    )
}
