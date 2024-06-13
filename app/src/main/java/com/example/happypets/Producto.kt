package com.example.happypets

data class Producto(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    var stock: Int,
    val categoria: String
)
