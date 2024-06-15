package com.example.happypets

class Compra {
    var id: Int = 0
    var carrito: ArrayList<Producto> = ArrayList()
    var direccion: String = ""
    var metodo_pago: String = ""
    var tipo_envio: String = ""

    constructor()

    constructor(
        id: Int,
        carrito: ArrayList<Producto>,
        direccion: String,
        metodo_pago: String,
        tipo_envio: String
    ) {
        this.id = id
        this.carrito = carrito
        this.direccion = direccion
        this.metodo_pago = metodo_pago
        this.tipo_envio = tipo_envio
    }
}