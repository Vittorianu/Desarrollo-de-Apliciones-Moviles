package com.example.android_16.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "trato_hecho_prefs")

class TratoHechoDataStore(private val context: Context) {

    private val KEY_LOGGED_USER_ID = stringPreferencesKey("logged_user_id")
    private val KEY_OFFLINE_MODE = booleanPreferencesKey("offline_mode")
    private val KEY_LAST_SYNC_TIME = stringPreferencesKey("last_sync_time")

    suspend fun guardarSesionUsuario(userId: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_LOGGED_USER_ID] = userId
        }
    }

    suspend fun cerrarSesion() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_LOGGED_USER_ID)
        }
    }

    fun obtenerUsuarioLogueadoId(): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[KEY_LOGGED_USER_ID]
        }
    }

    suspend fun guardarModoOffline(activo: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_OFFLINE_MODE] = activo
        }
    }

    fun obtenerModoOffline(): Flow<Boolean> {
        return context.dataStore.data.map { prefs ->
            prefs[KEY_OFFLINE_MODE] ?: false
        }
    }

    suspend fun guardarUltimaSincronizacion(timestamp: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_LAST_SYNC_TIME] = timestamp
        }
    }

    fun obtenerUltimaSincronizacion(): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[KEY_LAST_SYNC_TIME]
        }
    }
}
