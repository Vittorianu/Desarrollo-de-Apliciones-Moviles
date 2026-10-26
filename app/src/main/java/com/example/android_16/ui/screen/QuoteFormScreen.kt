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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android_16.model.Moneda
import com.example.android_16.ui.components.ValidatedTextField
import com.example.android_16.viewmodel.CotizacionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuoteFormScreen(
    viewModel: CotizacionViewModel,
    modoOffline: Boolean = false,
    onGuardadoExitoso: () -> Unit,
    onCancelar: () -> Unit
) {
    val state by viewModel.formState.collectAsState()
    val clientes by viewModel.clientes.collectAsState()
    val scrollState = rememberScrollState()

    var clienteMenuExpanded by remember { mutableStateOf(false) }

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
                text = "Borrador de Cotización (${state.folio})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // Client Selector Dropdown
            ExposedDropdownMenuBox(
                expanded = clienteMenuExpanded,
                onExpandedChange = { clienteMenuExpanded = !clienteMenuExpanded }
            ) {
                TextField(
                    value = state.clienteSeleccionado?.razonSocial ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Seleccionar Cliente *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = clienteMenuExpanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    isError = state.errorCliente != null,
                    supportingText = {
                        state.errorCliente?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = clienteMenuExpanded,
                    onDismissRequest = { clienteMenuExpanded = false }
                ) {
                    clientes.filter { it.activo }.forEach { cli ->
                        DropdownMenuItem(
                            text = { Text("${cli.razonSocial} (${cli.rut})") },
                            onClick = {
                                viewModel.seleccionarCliente(cli)
                                clienteMenuExpanded = false
                            }
                        )
                    }
                }
            }

            ValidatedTextField(
                value = state.contactoEmail,
                onValueChange = viewModel::onContactoEmailChanged,
                label = "Correo Destinatario *",
                error = state.errorEmail,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            // Currency Selector Chips
            Text(text = "Moneda / Unidad de Expresión:", fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Moneda.values().forEach { mon ->
                    FilterChip(
                        selected = state.moneda == mon,
                        onClick = { viewModel.seleccionarMoneda(mon) },
                        label = { Text("${mon.codigo} (${mon.simbolo})") }
                    )
                }
            }

            if (state.moneda != Moneda.CLP) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Valor Indicador ${state.moneda.codigo}: \$${state.valorConversionCLP} CLP",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "Fuente: Banco Central / SII - Mindecon",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Items Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ítems de la Cotización",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { viewModel.agregarItem() }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Agregar Ítem")
                }
            }

            state.errorItems?.let { err ->
                Text(text = err, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            state.items.forEachIndexed { index, item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Ítem #${index + 1}", fontWeight = FontWeight.Bold)
                            IconButton(onClick = { viewModel.eliminarItem(index) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar Ítem", tint = MaterialTheme.colorScheme.error)
                            }
                        }

                        ValidatedTextField(
                            value = item.descripcion,
                            onValueChange = { desc ->
                                viewModel.actualizarItem(index, desc, item.unidad, item.cantidad, item.precioUnitario, item.porcentajeDescuento, item.esExento)
                            },
                            label = "Descripción del Ítem *"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ValidatedTextField(
                                value = if (item.cantidad == 0.0) "" else item.cantidad.toString(),
                                onValueChange = { qty ->
                                    val valNum = qty.toDoubleOrNull() ?: 0.0
                                    viewModel.actualizarItem(index, item.descripcion, item.unidad, valNum, item.precioUnitario, item.porcentajeDescuento, item.esExento)
                                },
                                label = "Cantidad *",
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )

                            ValidatedTextField(
                                value = if (item.precioUnitario == 0.0) "" else item.precioUnitario.toString(),
                                onValueChange = { prc ->
                                    val valNum = prc.toDoubleOrNull() ?: 0.0
                                    viewModel.actualizarItem(index, item.descripcion, item.unidad, valNum, valNum, item.porcentajeDescuento, item.esExento)
                                },
                                label = "Precio Unit. (${state.moneda.simbolo}) *",
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ValidatedTextField(
                                value = item.porcentajeDescuento.toString(),
                                onValueChange = { dsc ->
                                    val valNum = dsc.toDoubleOrNull() ?: 0.0
                                    viewModel.actualizarItem(index, item.descripcion, item.unidad, item.cantidad, item.precioUnitario, valNum, item.esExento)
                                },
                                label = "Descuento %",
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Checkbox(
                                    checked = item.esExento,
                                    onCheckedChange = { ex ->
                                        viewModel.actualizarItem(index, item.descripcion, item.unidad, item.cantidad, item.precioUnitario, item.porcentajeDescuento, ex)
                                    }
                                )
                                Text(text = "Exento de IVA", fontSize = 12.sp)
                            }
                        }

                        // Calculated Line Summary
                        val netoLine = item.calcularNeto(state.moneda.decimales)
                        val taxLine = item.calcularImpuesto(state.moneda.decimales)
                        val totalLine = item.calcularTotalItem(state.moneda.decimales)

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Neto: ${state.moneda.simbolo}$netoLine | IVA: ${state.moneda.simbolo}$taxLine | Total Ítem: ${state.moneda.simbolo}$totalLine",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Real-Time Total Preview Calculation
            val netTotal = state.items.fold(java.math.BigDecimal.ZERO) { acc, itm -> acc.add(itm.calcularNeto(state.moneda.decimales)) }
            val taxTotal = state.items.fold(java.math.BigDecimal.ZERO) { acc, itm -> acc.add(itm.calcularImpuesto(state.moneda.decimales)) }
            val grandTotal = netTotal.add(taxTotal)

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "CÁLCULO TOTAL COTIZACIÓN", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Subtotal Neto: ${state.moneda.simbolo} $netTotal")
                    Text(text = "Total Impuestos: ${state.moneda.simbolo} $taxTotal")
                    Text(
                        text = "TOTAL PROPUESTA: ${state.moneda.simbolo} $grandTotal ${state.moneda.codigo}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (state.moneda != Moneda.CLP) {
                        val eqClp = grandTotal.multiply(java.math.BigDecimal.valueOf(state.valorConversionCLP)).setScale(0, java.math.RoundingMode.HALF_UP)
                        Text(
                            text = "Equivalencia en Pesos Chilenos: $ $eqClp CLP",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            ValidatedTextField(
                value = state.observaciones,
                onValueChange = viewModel::onObservacionesChanged,
                label = "Observaciones o Condiciones Comerciales",
                singleLine = false
            )

            // Error / Success Banner
            AnimatedVisibility(visible = state.mensajeExito != null) {
                state.mensajeExito?.let { msg ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFE8F5E9), shape = RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(text = msg, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                    }
                }
            }

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
                        if (viewModel.guardarBorrador(modoOffline = modoOffline)) {
                            onGuardadoExitoso()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Guardar Borrador")
                }
            }
        }
    }
}
