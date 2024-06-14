package com.example.happypets

class Cita {
    var id: Int = 0
    var usuario: Usuario = Usuario()
    var mascota: Mascotas = Mascotas()
    var fecha: String = ""
    var hora: String = ""

    constructor(
        id: Int,
        usuario: Usuario,
        mascotas: Mascotas,
        fecha: String,
        hora: String
    ) {
        this.id = id
        this.usuario = usuario
        this.mascota = mascotas
        this.fecha = fecha
        this.hora = hora
    }
}