package com.example.happypets

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import okhttp3.Call
import okhttp3.Callback
import okio.IOException

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var forgotPasswordButton: Button
    private lateinit var registrateButton: Button
    private lateinit var rememberMeCheckBox: CheckBox

    private lateinit var userManager: UserManager

    //Login con Google
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private val RC_SIGN_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViews()
        userManager = UserManager(this)

        loadPreferences()

        loginButton.setOnClickListener { handleLogin() }
        forgotPasswordButton.setOnClickListener { navigateToRecuperacion() }
        registrateButton.setOnClickListener { navigateToRegistro() }

        //Logion con Google
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Inicializar FirebaseAuth
        auth = FirebaseAuth.getInstance()

        findViewById<com.google.android.gms.common.SignInButton>(R.id.btn_sign_in).setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun initViews() {
        emailEditText = findViewById(R.id.username)
        passwordEditText = findViewById(R.id.Stock_producto)
        loginButton = findViewById(R.id.button)
        forgotPasswordButton = findViewById(R.id.button2)
        registrateButton = findViewById(R.id.button4)
        rememberMeCheckBox = findViewById(R.id.checkBox)
    }

    private fun handleLogin() {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            showToast("Por favor, llena todos los campos")
            return
        }

        if (rememberMeCheckBox.isChecked) {
            savePreferences(email, password)
        } else {
            clearPreferences()
        }

        login(email, password)
    }

    private fun navigateToRecuperacion() {
        startActivity(Intent(this, RecuperacionActivity::class.java))
    }

    private fun navigateToRegistro() {
        startActivity(Intent(this, RegistroActivity::class.java))
    }

    private fun login(email: String, password: String) {
        val url =
            "${Config.BASE_URL}/usuario/login.php" // Endpoint de login por correo y contraseña

        val requestBody = FormBody.Builder()
            .add("email", email)
            .add("password", password)
            .build()

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    showToast("Error de red: ${e.message}")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                runOnUiThread {
                    if (response.isSuccessful && responseData != null) {
                        val user = JSONObject(responseData)
                        if (user.has("error")) {
                            showToast(user.getString("error"))
                        } else {
                            showToast("Bienvenido ${user.getString("nombre")}")
                            navigateToMain(user)
                        }
                    } else {
                        showToast("Error al iniciar sesión" + response.message)
                    }
                }
            }
        })
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Log.w("SignInActivity", "Google sign-in failed", e)
                Toast.makeText(this, "Error en el inicio de sesión" + e.message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        loginWithGoogle(
                            user.email!!,
                            user.displayName!!
                        )
                    }
                } else {
                    Log.w("SignInActivity", "signInWithCredential:failure", task.exception)
                    showToast("Error en la autenticación")
                }
            }
    }


    private fun loginWithGoogle(email: String, nombre: String) {
        val url = "${Config.BASE_URL}/usuario/login_google.php" // Endpoint de login con Google

        val requestBody = FormBody.Builder()
            .add("email", email)
            .add("nombre", nombre)
            .build()

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    showToast("Error de red: ${e.message}")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                runOnUiThread {
                    if (response.isSuccessful && responseData != null) {
                        val result = JSONObject(responseData)
                        if (result.has("error")) {
                            showToast(result.getString("error"))
                        } else {
                            showToast(
                                "Bienvenido ${
                                    result.getJSONObject("data").getString("nombre")
                                }"
                            )
                            navigateToMain(result.getJSONObject("data"))
                        }
                    } else {
                        showToast("Error al iniciar sesión con Google" + response.message)
                    }
                }
            }
        })
    }

    private fun navigateToMain(user: JSONObject) {
        val type = user.getString("type")
        val intent = if (type == "1") {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, MainActivity2::class.java)
        }

        intent.putExtra("email", user.getString("email"))

        startActivity(intent)
        finish()
    }

    private fun loadPreferences() {
        val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        val email = sharedPreferences.getString("email", "")
        val password = sharedPreferences.getString("password", "")
        val rememberMe = sharedPreferences.getBoolean("rememberMe", false)

        emailEditText.setText(email)
        passwordEditText.setText(password)
        rememberMeCheckBox.isChecked = rememberMe
    }

    private fun savePreferences(email: String, password: String) {
        val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("email", email)
        editor.putString("password", password)
        editor.putBoolean("rememberMe", true)
        editor.apply()
    }

    private fun clearPreferences() {
        val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear() // Elimina todas las preferencias guardadas
        editor.apply()
    }


}
