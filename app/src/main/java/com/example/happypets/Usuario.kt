package com.example.happypets

import android.widget.Toast

class Usuario {
    var id: Int = 0
    var nombre: String = ""
    var email: String  = ""
    var telefono: String = ""
    var password: String = ""
    var direccion: String = ""
    var mascota: ArrayList<Mascotas> = ArrayList()
    var type: Int = 0

    constructor(id: Int, nombre: String, email: String, telefono: String, password: String, direccion: String, type: Int) {
        this.id = id
        this.nombre = nombre
        this.email = email
        this.telefono = telefono
        this.password = password
        this.direccion = direccion
        this.type = type
    }

    constructor(id: Int, nombre: String, email: String, telefono: String, password: String, direccion: String, mascota: ArrayList<Mascotas>, type: Int) {
        this.id = id
        this.nombre = nombre
        this.email = email
        this.telefono = telefono
        this.password = password
        this.direccion = direccion
        this.mascota = mascota
        this.type = type
    }

    fun agregarMascota(mascota: Mascotas) : Boolean {
        this.mascota.add(mascota)
        return true
    }
}