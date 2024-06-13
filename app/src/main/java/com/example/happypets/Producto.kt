package com.example.happypets

class Producto {
    var id: Int = 0
    var nombre: String = ""
    var precio: Double = 0.0
    var cantidad: Int = 0
    var descripcion: String = ""
    var categoria: String = ""
    var imagen: Int = 0

    constructor(id: Int, nombre: String, precio: Double, cantidad: Int, descripcion: String, categoria: String, imagen: Int) {
        this.id = id
        this.nombre = nombre
        this.precio = precio
        this.cantidad = cantidad
        this.descripcion = descripcion
        this.categoria = categoria
        this.imagen = imagen
    }

}