[readme.md](https://github.com/user-attachments/files/33981636/readme.md)
# Trato Hecho - Android App

Trato Hecho es una aplicación móvil Android orientada a la gestión comercial. Su objetivo principal es facilitar la administración de clientes, cotizaciones, facturas y órdenes de compra, destacando por su funcionamiento offline y sincronización de datos.

## Características Principales

* Gestión de clientes: Creación, edición y listado.
* Cotizaciones y facturación: Flujo completo de ventas y emisión de comprobantes.
* Gestión empresarial: Administración de datos de la empresa y contactos.
* Arquitectura Offline-First: Persistencia de datos en local y sincronización diferida.
* Autenticación y seguridad: Control de acceso basado en roles de usuario.
* Panel de administración: Vista dedicada para la gestión interna.
* Soporte multimoneda: Consulta de indicadores económicos y configuración de divisas.

## Stack Tecnológico y Arquitectura

* Lenguaje: Kotlin
* Interfaz de usuario: Jetpack Compose
* Almacenamiento local: DataStore y base de datos local
* Navegación: Compose Navigation
* Arquitectura: Separación por capas (`data`, `model`, `ui`, `navigation`)
* Manejo de estado: Estados de UI tipados y gestión de resultados

## Estructura del Proyecto

* `data`: Repositorios, DataStore y lógica de acceso a datos.
* `model`: Clases de dominio (Cliente, Cotizacion, Factura, etc.).
* `ui.screen`: Pantallas principales de la aplicación.
* `ui.components`: Componentes visuales reutilizables.
* `navigation`: Configuración del grafo de navegación.

## Instalación Local

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/tu-usuario/android_16.git
   ```
2. Abrir el proyecto en Android Studio.
3. Sincronizar los archivos de Gradle.
4. Ejecutar la aplicación en un dispositivo físico o emulador.

## Contribución

1. Realizar un Fork del repositorio.
2. Crear una rama para los cambios (`git checkout -b feature/nueva-funcion`).
3. Hacer commit de los cambios (`git commit -m 'Agrega nueva función'`).
4. Subir la rama (`git push origin feature/nueva-funcion`).
5. Abrir un Pull Request.

## Licencia

Este proyecto está bajo la Licencia [MIT / Propietaria - Actualizar según corresponda].
