package com.example.happypets

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegistroActivity : AppCompatActivity() {

    companion object {
        var userIdCounter = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        val userManager = UserManager(this)

        val nombreEditText = findViewById<EditText>(R.id.nombre)
        val emailEditText = findViewById<EditText>(R.id.email)
        val telefonoEditText = findViewById<EditText>(R.id.telefono)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val direccionEditText = findViewById<EditText>(R.id.direccion)
        val mascotaEditText = findViewById<EditText>(R.id.mascota)
        val registroButton = findViewById<Button>(R.id.button_registro)

        registroButton.setOnClickListener {
            val nombre = nombreEditText.text.toString()
            val email = emailEditText.text.toString()
            val telefono = telefonoEditText.text.toString()
            val password = passwordEditText.text.toString()
            val direccion = direccionEditText.text.toString()
            val mascota = mascotaEditText.text.toString()

            if (nombre.isNotEmpty() && email.isNotEmpty() && telefono.isNotEmpty() && password.isNotEmpty() && direccion.isNotEmpty() && mascota.isNotEmpty()) {
                // Incrementar el contador de ID de usuario
                val userId = ++userIdCounter

                // Crear una instancia de Usuario
                val nuevoUsuario = Usuario(
                    id = userId,
                    nombre = nombre,
                    email = email,
                    telefono = telefono,
                    password = password,
                    direccion = direccion,
                    mascota = mascota
                )

                // Guardar el usuario en el archivo JSON
                userManager.saveUser(nuevoUsuario)

                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()

            } else {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

