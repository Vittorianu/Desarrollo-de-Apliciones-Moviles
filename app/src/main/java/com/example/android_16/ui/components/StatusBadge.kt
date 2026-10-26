package com.example.android_16.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android_16.model.EstadoCotizacion
import com.example.android_16.model.EstadoSincronizacion

@Composable
fun StatusBadge(estado: EstadoCotizacion, modifier: Modifier = Modifier) {
    val targetColor = when (estado) {
        EstadoCotizacion.BORRADOR -> Color(0xFF757575)
        EstadoCotizacion.ENVIADA -> Color(0xFF0288D1)
        EstadoCotizacion.ACEPTADA -> Color(0xFF2E7D32)
        EstadoCotizacion.RECHAZADA -> Color(0xFFC62828)
        EstadoCotizacion.FACTURADA -> Color(0xFF6A1B9A)
    }

    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = 400),
        label = "BadgeColorAnimation"
    )

    Box(
        modifier = modifier
            .background(color = animatedColor.copy(alpha = 0.15f), shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = estado.etiqueta.uppercase(),
            color = animatedColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SyncBadge(syncState: EstadoSincronizacion, modifier: Modifier = Modifier) {
    val targetColor = when (syncState) {
        EstadoSincronizacion.SINCRONIZADO -> Color(0xFF2E7D32)
        EstadoSincronizacion.PENDIENTE -> Color(0xFFEF6C00)
        EstadoSincronizacion.ERROR -> Color(0xFFC62828)
        EstadoSincronizacion.CONFLICTO -> Color(0xFFD81B60)
    }

    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = 400),
        label = "SyncColorAnimation"
    )

    Box(
        modifier = modifier
            .background(color = animatedColor.copy(alpha = 0.12f), shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = "• ${syncState.etiqueta}",
            color = animatedColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
