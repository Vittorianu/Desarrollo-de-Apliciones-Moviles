package com.example.android_16.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.android_16.model.RolUsuario
import com.example.android_16.ui.screen.AdminScreen
import com.example.android_16.ui.screen.BillingScreen
import com.example.android_16.ui.screen.ClientFormScreen
import com.example.android_16.ui.screen.ClientListScreen
import com.example.android_16.ui.screen.DashboardScreen
import com.example.android_16.ui.screen.IndicatorScreen
import com.example.android_16.ui.screen.LoginScreen
import com.example.android_16.ui.screen.QuoteDetailScreen
import com.example.android_16.ui.screen.QuoteFormScreen
import com.example.android_16.ui.screen.QuoteListScreen
import com.example.android_16.viewmodel.AdminViewModel
import com.example.android_16.viewmodel.AuthViewModel
import com.example.android_16.viewmodel.ClienteViewModel
import com.example.android_16.viewmodel.CotizacionViewModel
import com.example.android_16.viewmodel.FacturacionViewModel
import com.example.android_16.viewmodel.IndicadoresViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // ViewModels
    val authViewModel: AuthViewModel = viewModel()
    val clienteViewModel: ClienteViewModel = viewModel()
    val cotizacionViewModel: CotizacionViewModel = viewModel()
    val facturacionViewModel: FacturacionViewModel = viewModel()
    val indicadoresViewModel: IndicadoresViewModel = viewModel()
    val adminViewModel: AdminViewModel = viewModel()

    val authState by authViewModel.uiState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val estaLogueado = authState.usuarioLogueado != null

    Scaffold(
        topBar = {
            if (estaLogueado && currentRoute != "login") {
                TopAppBar(
                    title = {
                        Text(text = authState.empresa?.razonSocial ?: "Trato Hecho Móvil")
                    },
                    colors = TopAppBarDefaults.topAppBarColors(),
                    actions = {
                        IconButton(onClick = {
                            authViewModel.cerrarSesion()
                            navController.navigate("login") {
                                popUpTo(0)
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar Sesión")
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (estaLogueado && currentRoute != "login") {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == "dashboard",
                        onClick = { navController.navigate("dashboard") },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                        label = { Text("Inicio") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "clientes",
                        onClick = { navController.navigate("clientes") },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Clientes") },
                        label = { Text("Clientes") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "cotizaciones",
                        onClick = { navController.navigate("cotizaciones") },
                        icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Cotizaciones") },
                        label = { Text("Cotizaciones") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "indicadores",
                        onClick = { navController.navigate("indicadores") },
                        icon = { Icon(Icons.Default.Info, contentDescription = "Indicadores") },
                        label = { Text("Indicadores") }
                    )
                    if (authState.usuarioLogueado?.rol == RolUsuario.ADMINISTRADOR) {
                        NavigationBarItem(
                            selected = currentRoute == "admin",
                            onClick = { navController.navigate("admin") },
                            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Admin") },
                            label = { Text("Admin") }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = if (estaLogueado) "dashboard" else "login"
            ) {
                composable("login") {
                    LoginScreen(
                        viewModel = authViewModel,
                        onLoginExitoso = {
                            navController.navigate("dashboard") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )
                }

                composable("dashboard") {
                    DashboardScreen(
                        authViewModel = authViewModel,
                        clienteViewModel = clienteViewModel,
                        cotizacionViewModel = cotizacionViewModel,
                        onNavigateToCotizaciones = { navController.navigate("cotizaciones") },
                        onNavigateToClientes = { navController.navigate("clientes") },
                        onNavigateToNuevaCotizacion = {
                            cotizacionViewModel.prepararNuevoBorrador()
                            navController.navigate("cotizacion_form")
                        },
                        onNavigateToFacturacion = { navController.navigate("facturacion") },
                        onNavigateToIndicadores = { navController.navigate("indicadores") }
                    )
                }

                composable("clientes") {
                    ClientListScreen(
                        viewModel = clienteViewModel,
                        onAgregarCliente = { navController.navigate("cliente_form") },
                        onEditarCliente = { navController.navigate("cliente_form") },
                        onCrearCotizacionParaCliente = { cli ->
                            cotizacionViewModel.prepararNuevoBorrador(cli)
                            navController.navigate("cotizacion_form")
                        }
                    )
                }

                composable("cliente_form") {
                    ClientFormScreen(
                        viewModel = clienteViewModel,
                        modoOffline = authState.modoOfflineActivo,
                        onGuardadoExitoso = { navController.popBackStack() },
                        onCancelar = { navController.popBackStack() }
                    )
                }

                composable("cotizaciones") {
                    QuoteListScreen(
                        viewModel = cotizacionViewModel,
                        onNuevaCotizacion = { navController.navigate("cotizacion_form") },
                        onVerDetalleCotizacion = { cot ->
                            navController.navigate("cotizacion_detalle/${cot.id}")
                        },
                        onEditarBorrador = { navController.navigate("cotizacion_form") }
                    )
                }

                composable("cotizacion_form") {
                    QuoteFormScreen(
                        viewModel = cotizacionViewModel,
                        modoOffline = authState.modoOfflineActivo,
                        onGuardadoExitoso = { navController.popBackStack() },
                        onCancelar = { navController.popBackStack() }
                    )
                }

                composable(
                    route = "cotizacion_detalle/{cotizacionId}",
                    arguments = listOf(navArgument("cotizacionId") { type = NavType.StringType })
                ) { backStack ->
                    val cotId = backStack.arguments?.getString("cotizacionId") ?: ""
                    QuoteDetailScreen(
                        cotizacionId = cotId,
                        viewModel = cotizacionViewModel,
                        onVolver = { navController.popBackStack() }
                    )
                }

                composable("facturacion") {
                    BillingScreen(
                        viewModel = facturacionViewModel,
                        onVolver = { navController.popBackStack() }
                    )
                }

                composable("indicadores") {
                    IndicatorScreen(
                        viewModel = indicadoresViewModel,
                        onVolver = { navController.popBackStack() }
                    )
                }

                composable("admin") {
                    AdminScreen(
                        viewModel = adminViewModel,
                        onVolver = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
