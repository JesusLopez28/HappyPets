package com.example.happypets

object CarritoManager {
    private val carrito = Carrito()

    fun agregarProducto(producto: Producto) {
        carrito.agregarProducto(producto)
    }

    fun quitarProducto(producto: Producto) {
        carrito.quitarProducto(producto)
    }

    fun obtenerCarrito(): Carrito {
        return carrito
    }
}
