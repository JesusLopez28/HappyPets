package com.example.happypets

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegistroActivity : AppCompatActivity() {

    companion object {
        var userIdCounter = 0 // Contador estático para generar IDs únicos
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

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

                // Pasar los datos del usuario a LoginActivity
                val intent = Intent(this, LoginActivity::class.java).apply {
                    putExtra("userId", nuevoUsuario.id)
                    putExtra("nombre", nuevoUsuario.nombre)
                    putExtra("email", nuevoUsuario.email)
                    putExtra("telefono", nuevoUsuario.telefono)
                    putExtra("password", nuevoUsuario.password)
                    putExtra("direccion", nuevoUsuario.direccion)
                    putExtra("mascota", nuevoUsuario.mascota)
                }
                startActivity(intent)
                finish()
                Toast.makeText(this, "Registro completado, bienvenido!", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}


