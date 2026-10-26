package com.example.android_16.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_16.data.LocalDatabaseRepository
import com.example.android_16.data.TratoHechoDataStore
import com.example.android_16.model.Empresa
import com.example.android_16.model.ResultadoLogin
import com.example.android_16.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val email: String = "juan@prismatec.cl",
    val clave: String = "1234",
    val emailError: String? = null,
    val claveError: String? = null,
    val errorGeneral: String? = null,
    val usuarioLogueado: Usuario? = null,
    val empresa: Empresa? = null,
    val esCargando: Boolean = false,
    val modoOfflineActivo: Boolean = false
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LocalDatabaseRepository
    private val dataStore = TratoHechoDataStore(application)

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.usuarioActual.collect { user ->
                val emp = repository.empresa.value
                _uiState.update { it.copy(usuarioLogueado = user, empresa = emp) }
            }
        }
        viewModelScope.launch {
            val savedUserId = dataStore.obtenerUsuarioLogueadoId().first()
            if (!savedUserId.isNullOrBlank()) {
                val user = repository.usuarios.value.find { it.id == savedUserId }
                if (user != null) {
                    repository.seleccionarUsuarioPrototipo(user)
                }
            }
        }
        viewModelScope.launch {
            dataStore.obtenerModoOffline().collect { isOffline ->
                _uiState.update { it.copy(modoOfflineActivo = isOffline) }
            }
        }
    }

    fun onEmailChanged(valor: String) {
        _uiState.update { it.copy(email = valor, emailError = null, errorGeneral = null) }
    }

    fun onClaveChanged(valor: String) {
        _uiState.update { it.copy(clave = valor, claveError = null, errorGeneral = null) }
    }

    fun alternarModoOffline() {
        val nuevoEstado = !_uiState.value.modoOfflineActivo
        viewModelScope.launch {
            dataStore.guardarModoOffline(nuevoEstado)
        }
    }

    fun cambiarPerfilPrototipo(usuario: Usuario) {
        repository.seleccionarUsuarioPrototipo(usuario)
        viewModelScope.launch {
            dataStore.guardarSesionUsuario(usuario.id)
        }
    }

    fun iniciarSesion(): Boolean {
        val state = _uiState.value
        val eErr = if (state.email.isBlank()) "El correo es obligatorio" else if (!state.email.contains("@")) "Correo inválido" else null
        val cErr = if (state.clave.length < 4) "La contraseña debe tener al menos 4 caracteres" else null

        if (eErr != null || cErr != null) {
            _uiState.update { it.copy(emailError = eErr, claveError = cErr) }
            return false
        }

        _uiState.update { it.copy(esCargando = true) }
        val resultado = repository.autenticar(state.email, state.clave)
        _uiState.update { it.copy(esCargando = false) }

        return when (resultado) {
            is ResultadoLogin.Exito -> {
                viewModelScope.launch {
                    dataStore.guardarSesionUsuario(resultado.usuario.id)
                }
                true
            }
            is ResultadoLogin.Error -> {
                _uiState.update { it.copy(errorGeneral = resultado.mensaje) }
                false
            }
            ResultadoLogin.Autenticando -> false
        }
    }

    fun cerrarSesion() {
        repository.cerrarSesion()
        viewModelScope.launch {
            dataStore.cerrarSesion()
        }
    }
}
