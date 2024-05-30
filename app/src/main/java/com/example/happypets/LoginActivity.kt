package com.example.happypets

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val userManager = UserManager(this)

        val usernameEditText = findViewById<EditText>(R.id.username)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.button)
        val forgotPasswordButton = findViewById<Button>(R.id.button2)
        val signUpButton = findViewById<Button>(R.id.button4)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                // Obtener la lista de usuarios registrados
                val users = userManager.getUsers()
                val user = users.find { it.email == username && it.password == password }

                if (user != null) {
                    // Autenticación exitosa, navegar a la siguiente actividad
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Autenticación fallida, mostrar un mensaje de error
                    Toast.makeText(this, "Authentication failed. Please check your username and password.", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Campos vacíos, mostrar un mensaje de error
                Toast.makeText(this, "Please enter both username and password.", Toast.LENGTH_SHORT).show()
            }
        }

        forgotPasswordButton.setOnClickListener {
            // Lógica para manejar la recuperación de la contraseña
            Toast.makeText(this, "Forgot Password clicked", Toast.LENGTH_SHORT).show()
        }

        signUpButton.setOnClickListener {
            // Lógica para manejar el registro de un nuevo usuario
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }
    }
}
