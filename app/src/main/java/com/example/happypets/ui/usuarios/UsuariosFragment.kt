package com.example.happypets.ui.usuarios

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.happypets.R
import com.example.happypets.Usuario
import com.example.happypets.UserManager
import com.example.happypets.UsuarioAdapter

class UsuariosFragment : Fragment() {

    private val viewModel: UsuariosViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_usuarios, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerview_Usuarios)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Obtener la lista de usuarios usando UserManager
        val userManager = UserManager(requireContext())
        val usuarios = userManager.getAllUsers()

        // Configurar el adaptador
        val adapter = UsuarioAdapter(usuarios.toMutableList())
        recyclerView.adapter = adapter

        return view
    }
}



