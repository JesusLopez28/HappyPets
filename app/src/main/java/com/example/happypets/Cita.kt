package com.example.happypets

class Cita {
    var id: Int = 0
    var usuario: ArrayList<Usuario> = ArrayList()
    var mascota: ArrayList<Mascotas> = ArrayList()
    var fecha: String = ""
    var hora: String = ""

    constructor(
        id: Int,
        usuario: ArrayList<Usuario>,
        mascota: ArrayList<Mascotas>,
        fecha: String,
        hora: String
    ) {
        this.id = id
        this.usuario = usuario
        this.mascota = mascota
        this.fecha = fecha
        this.hora = hora
    }
}