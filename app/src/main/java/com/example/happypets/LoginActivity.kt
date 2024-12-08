package com.example.happypets

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
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

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var forgotPasswordButton: Button
    private lateinit var registrateButton: Button

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
            intent.putExtra("email", email)
            startActivity(intent)
        } else {
            showToast("Contraseña o Email incorrecto")
        }
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
                Toast.makeText(this, "Error en el inicio de sesión", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(this, "Bienvenido, ${user?.displayName}", Toast.LENGTH_SHORT).show()
                    // Navegar a otra actividad
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Log.w("SignInActivity", "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Error en la autenticación", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
