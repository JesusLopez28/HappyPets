package com.example.happypets

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

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
                fetchUserInfo(email)
            } else {
                Toast.makeText(this, "Por favor ingrese un Email", Toast.LENGTH_SHORT).show()
            }
        }

        atrasRecuperarDatosButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun fetchUserInfo(email: String) {
        val url = "${Config.BASE_URL}/usuario/get_user_info.php"

        val requestBody = FormBody.Builder()
            .add("email", email)
            .build()

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("RecuperacionActivity", "Error de red: ${e.message}")
                runOnUiThread {
                    Toast.makeText(
                        this@RecuperacionActivity,
                        "Error de red: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                runOnUiThread {
                    if (response.isSuccessful && responseData != null) {
                        try {
                            val jsonResponse = JSONObject(responseData)
                            if (jsonResponse.has("error")) {
                                Toast.makeText(
                                    this@RecuperacionActivity,
                                    "Usuario no encontrado",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                val userData = jsonResponse.getJSONObject("data")
                                sendRecoveryEmail(userData)
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                this@RecuperacionActivity,
                                "Error al procesar datos del usuario",
                                Toast.LENGTH_SHORT
                            ).show()
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(
                            this@RecuperacionActivity,
                            "Error al obtener datos del usuario: ${response.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }

    private fun sendRecoveryEmail(userData: JSONObject) {
        val subject = "Información de tu cuenta"
        val message = """
            Hola ${userData.getString("nombre")},
            
            Aquí tienes la información de tu cuenta:
            
            Nombre: ${userData.getString("nombre")}
            Correo: ${userData.getString("email")}
            Celular: ${userData.getString("telefono")}
            Contraseña: ${userData.getString("password")}
            Domicilio: ${userData.getString("direccion")}
            
            Gracias por usar Happy Pets!
        """.trimIndent()

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val mailSender = MailSender(userData.getString("email"), subject, message)
                mailSender.send()
                Toast.makeText(
                    this@RecuperacionActivity,
                    "Email enviado con éxito",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(this@RecuperacionActivity, "El envío falló", Toast.LENGTH_SHORT)
                    .show()
                e.printStackTrace()
            }
        }
    }
}
