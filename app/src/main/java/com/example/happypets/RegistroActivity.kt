package com.example.happypets

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity



class RegistroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        val nombreEditText = findViewById<EditText>(R.id.nombre)
        val emailEditText = findViewById<EditText>(R.id.email)
        val telefonoEditText = findViewById<EditText>(R.id.Precio_producto)
        val passwordEditText = findViewById<EditText>(R.id.Stock_producto)
        val direccionEditText = findViewById<EditText>(R.id.direccion)
        val mascotaEditText = findViewById<EditText>(R.id.mascota)
        val registerButton = findViewById<Button>(R.id.button_registro)

        val userManager = UserManager(this)

        registerButton.setOnClickListener {
            val nombre = nombreEditText.text.toString()
            val email = emailEditText.text.toString()
            val telefono = telefonoEditText.text.toString()
            val password = passwordEditText.text.toString()
            val direccion = direccionEditText.text.toString()
            val mascota = mascotaEditText.text.toString()

            if (nombre.isNotEmpty() && email.isNotEmpty() && telefono.isNotEmpty() && password.isNotEmpty() &&
                direccion.isNotEmpty() && mascota.isNotEmpty()) {
                val id = userManager.getAllUsers().size + 1
                val nuevoUsuario = Usuario(id, nombre, email, telefono, password, direccion, mascota)
                userManager.saveUser(nuevoUsuario)
                Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                intent.putExtra("nombre", nombre)
                intent.putExtra("email", email)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Por favor revisar todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

