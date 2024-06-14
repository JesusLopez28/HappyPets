package com.example.happypets.ui.perfil_admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.happypets.R
import com.example.happypets.UserManager

class PerfilAdminFragment : Fragment() {

    companion object {
        private const val ARG_EMAIL = "email"

        fun newInstance(email: String) = PerfilAdminFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_EMAIL, email)
            }
        }
    }

    private val viewModel: PerfilAdminViewModel by viewModels()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_perfil_admin, container, false)
        val email = arguments?.getString(ARG_EMAIL)
        if (email != null) {
            val userManager = UserManager(requireContext())
            val adminUser = userManager.getUserByEmail(email)
            if (adminUser != null) {
                view.findViewById<TextView>(R.id.textView6).text = adminUser.nombre
                view.findViewById<TextView>(R.id.textView7).text = adminUser.email
            }
        }
        return view
    }
}

