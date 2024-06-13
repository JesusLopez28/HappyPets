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

        val emailEditText = findViewById<EditText>(R.id.username)
        val passwordEditText = findViewById<EditText>(R.id.Stock_producto)
        val loginButton = findViewById<Button>(R.id.button)
        val forgotPasswordButton = findViewById<Button>(R.id.button2)
        val registrateButton = findViewById<Button>(R.id.button4)

        val userManager = UserManager(this)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            val user = userManager.getUserByEmail(email)
            if (user != null && user.password == password) {
                Toast.makeText(this, "Bienvenido!!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Contraseña o Email incorrecto", Toast.LENGTH_SHORT).show()
            }

            if (user != null && user.password == password && user.type == 2) {
                Toast.makeText(this, "Bienvenido!!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity2::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Contraseña o Email incorrecto", Toast.LENGTH_SHORT).show()
            }
        }

        forgotPasswordButton.setOnClickListener {
            val intent = Intent(this, RecuperacionActivity::class.java)
            startActivity(intent)
        }

        registrateButton.setOnClickListener{
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }
    }
}

