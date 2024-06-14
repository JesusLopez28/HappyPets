package com.example.happypets

class Carrito {
    var id: Int = 0
    var usuario: ArrayList<Usuario> = ArrayList()
    var producto: ArrayList<Producto> = ArrayList()
    var subTotal: Double = 0.0
    var total: Double = 0.0
    var status: Int = 0

    constructor(
        id: Int,
        usuario: ArrayList<Usuario>,
        producto: ArrayList<Producto>,
        subTotal: Double,
        total: Double,
        status: Int
    ) {
        this.id = id
        this.usuario = usuario
        this.producto = producto
        this.subTotal = subTotal
        this.total = total
        this.status = status
    }
}