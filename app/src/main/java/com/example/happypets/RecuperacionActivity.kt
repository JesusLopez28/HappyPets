package com.example.happypets

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson

class RecuperacionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperacion)

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val sendButton = findViewById<Button>(R.id.sendButton)

        sendButton.setOnClickListener {
            val email = emailEditText.text.toString()
            if (email.isNotEmpty()) {
                val userManager = UserManager(this)
                val user = userManager.getUsers().find { it.email == email }
                if (user != null) {
                    sendRecoveryEmail(user)
                } else {
                    Toast.makeText(this, "Email no encontrado", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor introduce tu email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendRecoveryEmail(user: Usuario) {
        val subject = "Informacion de tu cuenta"
        val message = """
            Hola ${user.nombre}!,
            
            Es un gusto volver a verte. Aquí tienes la información de tu cuenta:
            
            Nombre: ${user.nombre}
            Correo: ${user.email}
            Telefono: ${user.telefono}
            Contraseña: ${user.password}
            Domicilio: ${user.direccion}
            Mascota: ${user.mascota}
            
            Gracias por usar HappyPets!
        """.trimIndent()

        MailSender(user.email, subject, message).send { success ->
            runOnUiThread {
                if (success) {
                    Toast.makeText(this, "Email enviado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "El envio falló", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
