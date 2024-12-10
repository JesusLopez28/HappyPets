package com.example.happypets

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class RegistroActivity : AppCompatActivity() {

    private lateinit var nombreEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var telefonoEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var direccionEditText: EditText
    private lateinit var mascotaEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var edadMascotaEditText: EditText
    private lateinit var razaMascotaSpinner: Spinner
    private lateinit var atrasRegistrarteButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        initViews()

        registerButton.setOnClickListener { handleRegister() }

        atrasRegistrarteButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun initViews() {
        nombreEditText = findViewById(R.id.nombre)
        emailEditText = findViewById(R.id.email)
        telefonoEditText = findViewById(R.id.Precio_producto)
        passwordEditText = findViewById(R.id.Stock_producto)
        direccionEditText = findViewById(R.id.direccion)
        mascotaEditText = findViewById(R.id.mascota)
        registerButton = findViewById(R.id.button_registro)
        edadMascotaEditText = findViewById(R.id.edad)
        razaMascotaSpinner = findViewById(R.id.spinner_raza)
        atrasRegistrarteButton = findViewById(R.id.AtrasRegistrarteButton)

        // LLenar spinner_raza
        val razaAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.raza,
            android.R.layout.simple_spinner_item
        )

        razaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        razaMascotaSpinner.adapter = razaAdapter
    }

    private fun handleRegister() {
        val nombre = nombreEditText.text.toString()
        val email = emailEditText.text.toString()
        val telefono = telefonoEditText.text.toString()
        val password = passwordEditText.text.toString()
        val direccion = direccionEditText.text.toString()
        val mascota = mascotaEditText.text.toString()
        val edad = edadMascotaEditText.text.toString()
        val raza = razaMascotaSpinner.selectedItem.toString()

        if (validateInputs(nombre, email, telefono, password, direccion, mascota, edad, raza)) {
            if (!validateEmail(email)) {
                showToast("Email no válido")
            } else if (!validatePhone(telefono)) {
                showToast("Teléfono no válido")
            } else {
                // Realizar la solicitud HTTP para registrar el usuario
                registerUser(nombre, email, telefono, password, direccion, mascota, edad, raza)
            }
        } else {
            showToast("Por favor revisar todos los campos")
        }
    }

    private fun validateInputs(vararg inputs: String): Boolean {
        return inputs.all { it.isNotEmpty() }
    }

    private fun validateEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }

    private fun validatePhone(phone: String): Boolean {
        val phonePattern = "[0-9]{10}"
        return phone.matches(phonePattern.toRegex())
    }

    private fun registerUser(
        nombre: String,
        email: String,
        telefono: String,
        password: String,
        direccion: String,
        mascota: String,
        edad: String,
        raza: String
    ) {
        // Usando la constante BASE_URL para construir la URL completa
        val url = "${Config.BASE_URL}/usuario/register.php"

        val requestBody = FormBody.Builder()
            .add("nombre", nombre)
            .add("email", email)
            .add("telefono", telefono)
            .add("password", password)
            .add("direccion", direccion)
            .add("nombre_mascota", mascota)
            .add("raza_mascota", raza)
            .add("edad_mascota", edad)
            .build()

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    showToast("Error al registrar el usuario")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        showToast("Usuario registrado correctamente")
                        navigateToLogin(nombre, email)
                    } else {
                        showToast("Error al registrar el usuario")
                    }
                }
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToLogin(nombre: String, email: String) {
        val intent = Intent(this, LoginActivity::class.java).apply {
            putExtra("nombre", nombre)
            putExtra("email", email)
        }
        startActivity(intent)
    }
}
