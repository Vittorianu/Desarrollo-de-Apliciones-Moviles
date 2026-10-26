[readme.md](https://github.com/user-attachments/files/33981596/readme.md)
# Desarrollo-de-Apliciones-Moviles# 🤝 Trato Hecho - Android App

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=android&logoColor=white)

Trato Hecho es una aplicación móvil nativa para Android diseñada para optimizar la gestión comercial. Permite a los usuarios administrar clientes, generar cotizaciones, gestionar facturas y órdenes de compra, todo con soporte para **modo offline** y sincronización de datos.

## ✨ Características Principales

*   **👥 Gestión de Clientes:** Creación, edición y listado de clientes (`ClientFormScreen`, `ClientListScreen`).
*   **📄 Cotizaciones y Facturación:** Flujo completo para crear cotizaciones y emitir facturas (`BillingScreen`).
*   **🏢 Gestión de Empresas:** Administración de datos empresariales y contactos.
*   **📴 Modo Offline First:** Funcionamiento sin conexión con persistencia de datos local y sincronización posterior (`TratoHechoDataStore`, `EstadoSincronizacion`).
*   **🔐 Autenticación:** Inicio de sesión seguro y gestión de sesiones de usuario con distintos roles (`RolUsuario`).
*   **📊 Panel de Administración:** Vista dedicada para tareas administrativas (`AdminScreen`).
*   **💰 Indicadores Económicos:** Consulta de indicadores y múltiples monedas configurables.

## 🛠️ Tecnologías y Arquitectura

El proyecto está construido utilizando las herramientas y prácticas modernas de desarrollo en Android:

*   **Lenguaje:** [Kotlin](https://kotlinlang.org/)
*   **UI Toolkit:** [Jetpack Compose](https://developer.android.com/jetpack/compose) para una interfaz de usuario declarativa.
*   **Almacenamiento Local:** DataStore (preferencias y estado de sesión) y base de datos local (`LocalDatabaseRepository`).
*   **Navegación:** Compose Navigation (`AppNavigation`).
*   **Arquitectura:** Basada en componentes limpios (Separación en capas: `data`, `model`, `ui`, `navigation`).
*   **Gestión de Estado:** Manejo de estados de UI y resultados (`Resultado`, `ResultadoLogin`, `Cargando`, `Exito`, `Error`).

## 📂 Estructura del Proyecto (Resumen)

*   `com.example.android_16.data`: Repositorios y DataStore (manejo de datos locales, offline y sincronización).
*   `com.example.android_16.model`: Clases de datos del dominio (`Cliente`, `Cotizacion`, `Factura`, `Empresa`, etc.).
*   `com.example.android_16.ui.screen`: Pantallas principales construidas con Compose (`AdminScreen`, `BillingScreen`, `ClientListScreen`, etc.).
*   `com.example.android_16.ui.components`: Componentes de UI reutilizables (ej. `StatCard`, `StatusBadge`, `ValidatedTextField`).
*   `com.example.android_16.navigation`: Configuración de rutas y navegación de la app.

## 🚀 Instalación y Configuración

Sigue estos pasos para compilar y ejecutar el proyecto en tu entorno local:

1.  **Clonar el repositorio:**
    ```bash
    git clone https://github.com/tu-usuario/android_16.git
    ```
2.  **Abrir en Android Studio:**
    *   Abre Android Studio (se recomienda la versión más reciente compatible con Compose).
    *   Selecciona `File > Open...` y busca la carpeta del proyecto clonado.
3.  **Sincronizar Gradle:**
    *   Espera a que Gradle descargue las dependencias y configure el proyecto.
4.  **Ejecutar la aplicación:**
    *   Conecta un dispositivo físico o inicia un emulador de Android.
    *   Haz clic en el botón de **Run** (Shift + F10) en Android Studio.

## 🤝 Contribución

Si deseas contribuir a este proyecto:
1. Haz un *Fork* del repositorio.
2. Crea una rama para tu función (`git checkout -b feature/NuevaFuncion`).
3. Realiza tus cambios y haz *commit* (`git commit -m 'Añade nueva función'`).
4. Sube la rama (`git push origin feature/NuevaFuncion`).
5. Abre un *Pull Request*.

## 📄 Licencia

Este proyecto está bajo la Licencia [MIT/Propietaria - *Actualizar según corresponda*].
