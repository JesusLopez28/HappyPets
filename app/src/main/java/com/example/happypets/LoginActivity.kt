package com.example.happypets

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private var registeredUser: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Recuperar los datos del usuario registrado si existen
        val userId = intent.getIntExtra("userId", -1)
        val nombre = intent.getStringExtra("nombre") ?: ""
        val email = intent.getStringExtra("email") ?: ""
        val telefono = intent.getStringExtra("telefono") ?: ""
        val password = intent.getStringExtra("password") ?: ""
        val direccion = intent.getStringExtra("direccion") ?: ""
        val mascota = intent.getStringExtra("mascota") ?: ""

        if (userId != -1) {
            registeredUser = Usuario(
                id = userId,
                nombre = nombre,
                email = email,
                telefono = telefono,
                password = password,
                direccion = direccion,
                mascota = mascota
            )
        }

        val usernameEditText = findViewById<EditText>(R.id.username)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.button)
        val forgotPasswordButton = findViewById<Button>(R.id.button2)
        val signUpButton = findViewById<Button>(R.id.button4)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                // Comprobar las credenciales con el usuario registrado
                if (registeredUser != null && username == registeredUser!!.email && password == registeredUser!!.password) {
                    // Autenticación exitosa, navegar a la siguiente actividad
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Autenticación fallida, mostrar un mensaje de error
                    Toast.makeText(this, "La autenticacion falló. Por favor revisar su email y contraseña.", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Campos vacíos, mostrar un mensaje de error
                Toast.makeText(this, "Por favor ingrese su email y contraseña.", Toast.LENGTH_SHORT).show()
            }
        }

        forgotPasswordButton.setOnClickListener {
            // Lógica para manejar la recuperación de la contraseña
            Toast.makeText(this, "Disponible en proximas versiones", Toast.LENGTH_SHORT).show()
        }

        signUpButton.setOnClickListener {
            // Lógica para manejar el registro de un nuevo usuario
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }
    }
}
