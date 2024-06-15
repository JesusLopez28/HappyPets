package com.example.happypets

class Carrito {
    var id: Int = 0
    var usuario: ArrayList<Usuario> = ArrayList()
    var productos: ArrayList<Producto> = ArrayList()
    var subTotal: Double = 0.0
    var iva: Double = 0.0  // Nuevo campo para el IVA
    var total: Double = 0.0
    var status: Int = 0

    constructor(
        id: Int,
        usuario: ArrayList<Usuario>,
        productos: ArrayList<Producto>,
        subTotal: Double,
        iva: Double,
        total: Double,
        status: Int
    ) {
        this.id = id
        this.usuario = usuario
        this.productos = productos
        this.subTotal = subTotal
        this.iva = iva
        this.total = total
        this.status = status
        calcularSubTotal()
        calcularIVA()
        calcularTotal()
    }

    constructor()

    fun agregarProducto(producto: Producto) {
        productos.add(producto)
        calcularSubTotal()
        calcularIVA()
        calcularTotal()
    }

    fun quitarProducto(producto: Producto) {
        productos.remove(producto)
        calcularSubTotal()
        calcularIVA()
        calcularTotal()
    }

    private fun calcularSubTotal() {
        subTotal = productos.sumByDouble { it.precio }
    }

    private fun calcularIVA() {
        // Supongamos que el IVA es del 16%
        val porcentajeIVA = 0.16
        iva = subTotal * porcentajeIVA
    }

    private fun calcularTotal() {
        total = subTotal + iva
    }
}




