package com.example.happypets.ui.productos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.happypets.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ProductosFragment : Fragment() {

    private val viewModel: ProductosViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_productos, container, false)

        val agregarProductoButton: FloatingActionButton = view.findViewById(R.id.AgregarProductoButton)
        agregarProductoButton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_productos_to_registrarProducto)
        }

        return view
    }
}

