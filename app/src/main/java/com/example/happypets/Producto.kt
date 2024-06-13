package com.example.happypets

class Producto {
    var id: Int = 0
    var nombre: String = ""
    var descripcion: String = ""
    var precio: Double = 0.0
    var stock: Int = 0
    var categoria: String = ""

    constructor(
        id: Int,
        nombre: String,
        descripcion: String,
        precio: Double,
        stock: Int,
        categoria: String
    ) {
        this.id = id
        this.nombre = nombre
        this.descripcion = descripcion
        this.precio = precio
        this.stock = stock
        this.categoria = categoria
    }

}

