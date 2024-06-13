package com.example.happypets

data class Usuario(
    val id: Int,
    val nombre: String,
    val email: String,
    val telefono: String,
    val password: String,
    val direccion: String,
    val mascota: String,
    val type: Int = 2 // Asignar el valor 1 por defecto
)