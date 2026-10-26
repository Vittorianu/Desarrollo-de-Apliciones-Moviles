package com.example.android_16.model

enum class RolUsuario(val titulo: String, val descripcion: String) {
    ADMINISTRADOR("Administrador", "Parametriza catálogos, usuarios y ve todas las cotizaciones de la empresa"),
    SUPERVISOR("Supervisor", "Revisa y hace seguimiento del equipo asignado"),
    USUARIO("Usuario Comercial", "Gestiona sus propios clientes y crea/envía sus cotizaciones"),
    FACTURACION("Facturación", "Registra órdenes de compra y facturas sobre cotizaciones aceptadas")
}

enum class EstadoCotizacion(val etiqueta: String) {
    BORRADOR("Borrador"),
    ENVIADA("Enviada"),
    ACEPTADA("Aceptada"),
    RECHAZADA("Rechazada"),
    FACTURADA("Facturada")
}

enum class Moneda(val codigo: String, val simbolo: String, val decimales: Int) {
    CLP("CLP", "$", 0),
    USD("USD", "US$", 2),
    UF("UF", "UF", 4)
}

enum class EstadoSincronizacion(val etiqueta: String) {
    SINCRONIZADO("Sincronizado"),
    PENDIENTE("Pendiente de Sync"),
    ERROR("Error de Sync"),
    CONFLICTO("Conflicto de Edición")
}
