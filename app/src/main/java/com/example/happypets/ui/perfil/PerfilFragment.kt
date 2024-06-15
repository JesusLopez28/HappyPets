package com.example.happypets.ui.perfil

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.happypets.R
import com.example.happypets.UserManager
import com.example.happypets.databinding.FragmentPerfilBinding

class PerfilFragment : Fragment() {

    private lateinit var userManager: UserManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userManager = UserManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_perfil, container, false)
        val informacionUsuario = view.findViewById<TextView>(R.id.InformacionUsuario)
        val emailUsuarioTextView = view.findViewById<TextView>(R.id.EmailUsuarioText)
        val btnCerrarSesion: Button = view.findViewById(R.id.btnCerrarSesion)
        val btnMascotas: Button = view.findViewById(R.id.btnVerMascotas)

        val email = requireActivity().intent.getStringExtra("email")

        email?.let {
            val user = userManager.getUserByEmail(it)
            user?.let { usuario ->
                informacionUsuario.text =
                    "Nombre: ${usuario.nombre}\nEmail: ${usuario.email}\nTeléfono: ${usuario.telefono}\nDirección: ${usuario.direccion}\n"
                emailUsuarioTextView.text = "${usuario.email}"
            } ?: run {
                Log.e("PerfilFragment", "Usuario no encontrado para el email: $email")
            }
        } ?: run {
            Log.e("PerfilFragment", "El email es nulo")
        }

        btnCerrarSesion.setOnClickListener {
            requireActivity().finish()
        }

        btnMascotas.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_perfil_to_navigation_mascotas)
        }
        return view
    }
}