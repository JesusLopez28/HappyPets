package com.example.happypets

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

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

    private lateinit var userManager: UserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        initViews()
        userManager = UserManager(this)

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
        val id = userManager.getAllUsers().size + 1
        val mascota = Mascotas(mascota, raza, edad.toInt(), id)
        val type = if (userManager.getAllUsers().isEmpty()) 2 else 1
        val nuevoUsuario = Usuario(id, nombre, email, telefono, password, direccion, type)
        val agregado = nuevoUsuario.agregarMascota(mascota)
        if (!agregado) {
            showToast("Error al agregar mascota")
            return
        }
        userManager.saveUser(nuevoUsuario)
        showToast("Usuario registrado correctamente")
        navigateToLogin(nombre, email)
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
