package com.example.happypets.ui.catalogo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.happypets.R
import com.example.happypets.databinding.FragmentProductoUsuarioBinding

class ProductoUsuarioFragment : Fragment() {

    private var _binding: FragmentProductoUsuarioBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductoUsuarioBinding.inflate(inflater, container, false)
        val root: View = binding.root

        arguments?.let { bundle ->
            val nombre = bundle.getString("nombre")
            val descripcion = bundle.getString("descripcion")
            val precio = bundle.getDouble("precio")
            val id = bundle.getInt("id")

            binding.textView13.text = nombre
            binding.DescripcionProducto.text = descripcion
            binding.PrecioProducto.text = "Precio: $$precio"
            binding.imageView18.setImageResource(R.drawable.producto_1) // Cambiar si es necesario manualmente

            // Poner imangen basado en el id
            val imageResourceId = when (id) {
                1 -> R.drawable.producto_1
                2 -> R.drawable.producto_1
                //Mapa por default
                else -> R.drawable.icono1
            }
            binding.imageView18.setImageResource(imageResourceId)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
