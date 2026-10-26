package com.example.android_16.viewmodel

import androidx.lifecycle.ViewModel
import com.example.android_16.data.LocalDatabaseRepository
import com.example.android_16.model.Cliente
import com.example.android_16.model.Contacto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ClienteFormState(
    val id: String = "",
    val razonSocial: String = "",
    val rut: String = "",
    val direccion: String = "",
    val latitud: Double? = null,
    val longitud: Double? = null,
    val imagenUri: String? = null,
    val contactos: List<Contacto> = emptyList(),
    val errorRazonSocial: String? = null,
    val errorRut: String? = null,
    val errorContactoEmail: String? = null,
    val errorGeneral: String? = null,
    val mensajeExito: String? = null
)

class ClienteViewModel : ViewModel() {

    private val repository = LocalDatabaseRepository

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    private val _formState = MutableStateFlow(ClienteFormState())
    val formState: StateFlow<ClienteFormState> = _formState.asStateFlow()

    val clientes: StateFlow<List<Cliente>> = repository.clientes

    fun onBusquedaChanged(query: String) {
        _busqueda.value = query
    }

    fun prepararNuevoCliente() {
        _formState.value = ClienteFormState(
            contactos = listOf(Contacto(id = "CON-${System.currentTimeMillis()}", nombre = "", email = "", telefono = ""))
        )
    }

    fun cargarClienteParaEdicion(cliente: Cliente) {
        _formState.value = ClienteFormState(
            id = cliente.id,
            razonSocial = cliente.razonSocial,
            rut = cliente.rut,
            direccion = cliente.direccion,
            latitud = cliente.latitud,
            longitud = cliente.longitud,
            imagenUri = cliente.imagenUri,
            contactos = if (cliente.contactos.isEmpty()) {
                listOf(Contacto(id = "CON-${System.currentTimeMillis()}", nombre = "", email = "", telefono = ""))
            } else cliente.contactos
        )
    }

    fun onRazonSocialChanged(valor: String) {
        _formState.update { it.copy(razonSocial = valor, errorRazonSocial = null, errorGeneral = null) }
    }

    fun onRutChanged(valor: String) {
        _formState.update { it.copy(rut = valor, errorRut = null, errorGeneral = null) }
    }

    fun onDireccionChanged(valor: String) {
        _formState.update { it.copy(direccion = valor) }
    }

    fun onUbicacionCapturada(lat: Double, lng: Double) {
        _formState.update { it.copy(latitud = lat, longitud = lng, mensajeExito = "Ubicación GPS capturada: $lat, $lng") }
    }

    fun onImagenCapturadaOSeleccionada(uriStr: String) {
        _formState.update { it.copy(imagenUri = uriStr, mensajeExito = "Imagen adjunta correctamente") }
    }

    fun onContactoNombreChanged(index: Int, nombre: String) {
        _formState.update { state ->
            val list = state.contactos.toMutableList()
            if (index in list.indices) {
                list[index] = list[index].copy(nombre = nombre)
            }
            state.copy(contactos = list)
        }
    }

    fun onContactoEmailChanged(index: Int, email: String) {
        _formState.update { state ->
            val list = state.contactos.toMutableList()
            if (index in list.indices) {
                list[index] = list[index].copy(email = email)
            }
            state.copy(contactos = list, errorContactoEmail = null)
        }
    }

    fun onContactoTelefonoChanged(index: Int, telefono: String) {
        _formState.update { state ->
            val list = state.contactos.toMutableList()
            if (index in list.indices) {
                list[index] = list[index].copy(telefono = telefono)
            }
            state.copy(contactos = list)
        }
    }

    fun agregarContacto() {
        _formState.update { state ->
            val list = state.contactos + Contacto(id = "CON-${System.currentTimeMillis()}", nombre = "", email = "", telefono = "")
            state.copy(contactos = list)
        }
    }

    fun guardarCliente(modoOffline: Boolean = false): Boolean {
        val state = _formState.value
        val errNombre = if (state.razonSocial.isBlank()) "La Razón Social o Nombre es obligatorio" else null
        val errRut = if (state.rut.isBlank()) "El Identificador Tributario (RUT) es obligatorio" else null
        
        var errEmail: String? = null
        for (c in state.contactos) {
            if (c.email.isNotBlank() && !c.email.contains("@")) {
                errEmail = "Correo de contacto con formato inválido (${c.email})"
                break
            }
        }

        if (errNombre != null || errRut != null || errEmail != null) {
            _formState.update { 
                it.copy(errorRazonSocial = errNombre, errorRut = errRut, errorContactoEmail = errEmail) 
            }
            return false
        }

        val clienteAguardar = Cliente(
            id = if (state.id.isBlank()) "CLI-${System.currentTimeMillis().toString().takeLast(6)}" else state.id,
            empresaId = "", // repository assigns
            usuarioPropietarioId = "", // repository assigns
            razonSocial = state.razonSocial,
            rut = state.rut,
            direccion = state.direccion,
            latitud = state.latitud,
            longitud = state.longitud,
            imagenUri = state.imagenUri,
            contactos = state.contactos.filter { it.nombre.isNotBlank() || it.email.isNotBlank() }
        )

        val errorDup = repository.agregarOActualizarCliente(clienteAguardar, offline = modoOffline)
        if (errorDup != null) {
            _formState.update { it.copy(errorGeneral = errorDup) }
            return false
        }

        _formState.update { it.copy(mensajeExito = "Cliente guardado exitosamente.") }
        return true
    }

    fun desactivarCliente(clienteId: String) {
        repository.desactivarCliente(clienteId)
    }
}
