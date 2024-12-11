package com.example.happypets.ui.perfil

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.happypets.LoginActivity
import com.example.happypets.R
import com.example.happypets.Config // Asegúrate de tener Config.BASE_URL configurado
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class PerfilFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var informacionUsuario: TextView
    private lateinit var emailUsuarioTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_perfil, container, false)

        informacionUsuario = view.findViewById(R.id.InformacionUsuario)
        emailUsuarioTextView = view.findViewById(R.id.EmailUsuarioText)
        val btnCerrarSesion: Button = view.findViewById(R.id.btnCerrarSesion)
        val btnMascotas: Button = view.findViewById(R.id.btnVerMascotas)
        auth = FirebaseAuth.getInstance()

        val email = requireActivity().intent.getStringExtra("email")

        if (email != null) {
            fetchUserInfo(email)
        } else {
            informacionUsuario.text = "Error: No se encontró el email del usuario"
        }

        btnCerrarSesion.setOnClickListener {
            auth.signOut() // Cierra sesión en Firebase
            val googleSignInClient = GoogleSignIn.getClient(
                requireActivity(),
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
            )
            googleSignInClient.signOut() // Cierra sesión en Google
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            startActivity(intent)
        }

        btnMascotas.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_perfil_to_navigation_mascotas)
        }
        return view
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
                Log.e("PerfilFragment", "Error de red: ${e.message}")
                requireActivity().runOnUiThread {
                    informacionUsuario.text = "Error de red: ${e.message}"
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                requireActivity().runOnUiThread {
                    if (response.isSuccessful && responseData != null) {
                        val user = JSONObject(responseData)
                        if (user.has("error")) {
                            informacionUsuario.text = user.getString("error")
                        } else {
                            val nombre = user.getJSONObject("data").getString("nombre")
                            val email = user.getJSONObject("data").getString("email")
                            val telefono = user.getJSONObject("data").getString("telefono")
                            val direccion = user.getJSONObject("data").getString("direccion")
                            val tipo = user.getJSONObject("data").getString("type")
                            val tipoUsuario = if (tipo == "1") "Usuario" else "Administrador"
                            val info = "Nombre: $nombre\nEmail: $email\nTeléfono: $telefono\nDirección: $direccion\nTipo: $tipoUsuario"
                            informacionUsuario.text = info
                            emailUsuarioTextView.text = email
                        }
                    } else {
                        informacionUsuario.text =
                            "Error al obtener datos del usuario: ${response.message}"
                    }
                }
            }
        })
    }
}
