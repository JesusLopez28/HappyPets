package com.example.happypets

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecuperacionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperacion)

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val sendButton = findViewById<Button>(R.id.sendButton)
        val atrasRecuperarDatosButton = findViewById<ImageButton>(R.id.AtrasRecuperarDatosButton)

        sendButton.setOnClickListener {
            val email = emailEditText.text.toString()
            if (email.isNotEmpty()) {
                val userManager = UserManager(this)
                val user = userManager.getUserByEmail(email)
                if (user != null) {
                    sendRecoveryEmail(user)
                } else {
                    Toast.makeText(this, "Email no encontrado", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor ingresé un Email", Toast.LENGTH_SHORT).show()
            }
        }

        atrasRecuperarDatosButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun sendRecoveryEmail(user: Usuario) {
        val subject = "Información de tu cuenta"
        val message = """
            Hola ${user.nombre},
            
            Aquí tienes la información de tu cuenta:
            
            Nombre: ${user.nombre}
            Correo: ${user.email}
            Celular: ${user.telefono}
            Contraseña: ${user.password}
            Domicilio: ${user.direccion}
            Mascota: ${user.mascota}
            
            Gracias por usar Happy Pets!
        """.trimIndent()

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val mailSender = MailSender(user.email, subject, message)
                mailSender.send()
                Toast.makeText(this@RecuperacionActivity, "Email enviado", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@RecuperacionActivity, "El envío falló", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }
}

