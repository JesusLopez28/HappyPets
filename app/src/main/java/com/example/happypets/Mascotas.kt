package com.example.happypets

class Mascotas {
    var nombre: String = ""
    var raza: String = ""
    var edad: Int = 0
    var idUsuario: Int = 0

    constructor(nombre: String, raza: String, edad: Int, idUsuario: Int) {
        this.nombre = nombre
        this.raza = raza
        this.edad = edad
        this.idUsuario = idUsuario
    }

    constructor()

    override fun toString(): String {
        return "Nombre: $nombre, Raza: $raza, Edad: $edad"
    }
}