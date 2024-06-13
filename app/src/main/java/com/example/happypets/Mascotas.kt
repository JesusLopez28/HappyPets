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

}