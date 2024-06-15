package com.example.happypets.ui.perfil_admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.happypets.R
import com.example.happypets.UserManager
import android.widget.TextView
import android.util.Log
import android.widget.Button

class PerfilAdminFragment : Fragment() {

    private lateinit var userManager: UserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userManager = UserManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_perfil_admin, container, false)
        val adminInfoTextView: TextView = view.findViewById(R.id.adminInfoTextView)
        val adminRoleTextView: TextView = view.findViewById(R.id.adminRoleTextView)
        val btnCerrarSesion: Button = view.findViewById(R.id.btnCerrarSesion)

        val email = requireActivity().intent.getStringExtra("email")

        email?.let {
            val user = userManager.getUserByEmail(it)
            user?.let { admin ->
                adminInfoTextView.text = "Tipo: ${admin.type}\nNombre: ${admin.nombre}\nEmail: ${admin.email}\nTeléfono: ${admin.telefono}\nDirección: ${admin.direccion}\n"
                adminRoleTextView.text = "${admin.nombre}"
            } ?: run {
                Log.e("PerfilAdminFragment", "User not found for email: $email")
            }
        } ?: run {
            Log.e("PerfilAdminFragment", "Email is null")
        }

        btnCerrarSesion.setOnClickListener {
            requireActivity().finish()
        }

        return view
    }
}



