package com.example.happypets

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var forgotPasswordButton: Button
    private lateinit var registrateButton: Button

    private lateinit var userManager: UserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViews()
        userManager = UserManager(this)

        loginButton.setOnClickListener { handleLogin() }
        forgotPasswordButton.setOnClickListener { navigateToRecuperacion() }
        registrateButton.setOnClickListener { navigateToRegistro() }
    }

    private fun initViews() {
        emailEditText = findViewById(R.id.username)
        passwordEditText = findViewById(R.id.Stock_producto)
        loginButton = findViewById(R.id.button)
        forgotPasswordButton = findViewById(R.id.button2)
        registrateButton = findViewById(R.id.button4)
    }

    private fun handleLogin() {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        val user = userManager.getUserByEmail(email)

        if (user != null) {
            login(email, password, user.type)
        } else {
            showToast("Usuario no encontrado")
        }
    }

    private fun navigateToRecuperacion() {
        startActivity(Intent(this, RecuperacionActivity::class.java))
    }

    private fun navigateToRegistro() {
        startActivity(Intent(this, RegistroActivity::class.java))
    }

    private fun login(email: String, password: String, userType: Int) {
        val user = userManager.getUserByEmail(email)
        if (user != null && user.password == password && user.type == userType) {
            showToast("Bienvenido!!")
            val intent = if (userType == 1) {
                Intent(this, MainActivity::class.java)
            } else {
                Intent(this, MainActivity2::class.java)
            }
            startActivity(intent)
        } else {
            showToast("Contraseña o Email incorrecto")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
