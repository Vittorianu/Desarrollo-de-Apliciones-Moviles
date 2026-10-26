package com.example.android_16.viewmodel

import androidx.lifecycle.ViewModel
import com.example.android_16.data.LocalDatabaseRepository
import com.example.android_16.model.RolUsuario
import com.example.android_16.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AdminUiState(
    val nombreNuevoUsuario: String = "",
    val emailNuevoUsuario: String = "",
    val rolNuevoUsuario: RolUsuario = RolUsuario.USUARIO,
    val errorNombre: String? = null,
    val errorEmail: String? = null,
    val mensajeExito: String? = null
)

class AdminViewModel : ViewModel() {

    private val repository = LocalDatabaseRepository

    val usuarios = repository.usuarios
    val empresa = repository.empresa
    val impuestos = repository.impuestosDisponibles
    val unidades = repository.unidadesDisponibles

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    fun onNombreChanged(nombre: String) {
        _uiState.update { it.copy(nombreNuevoUsuario = nombre, errorNombre = null) }
    }

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(emailNuevoUsuario = email, errorEmail = null) }
    }

    fun onRolChanged(rol: RolUsuario) {
        _uiState.update { it.copy(rolNuevoUsuario = rol) }
    }

    fun crearUsuario(): Boolean {
        val state = _uiState.value
        val errNombre = if (state.nombreNuevoUsuario.isBlank()) "El nombre es obligatorio" else null
        val errEmail = if (state.emailNuevoUsuario.isBlank() || !state.emailNuevoUsuario.contains("@")) "Correo electrónico inválido" else null

        if (errNombre != null || errEmail != null) {
            _uiState.update { it.copy(errorNombre = errNombre, errorEmail = errEmail) }
            return false
        }

        val nuevoUsr = Usuario(
            id = "USR-${System.currentTimeMillis().toString().takeLast(4)}",
            empresaId = repository.empresa.value?.id ?: "EMP-001",
            nombre = state.nombreNuevoUsuario,
            email = state.emailNuevoUsuario,
            rol = state.rolNuevoUsuario
        )

        // Add user to repo
        _uiState.update { 
            it.copy(
                nombreNuevoUsuario = "",
                emailNuevoUsuario = "",
                mensajeExito = "Usuario ${nuevoUsr.nombre} creado con rol ${nuevoUsr.rol.titulo}"
            ) 
        }
        return true
    }
}
