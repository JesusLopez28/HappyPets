package com.example.happypets.ui.catalogo

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.happypets.MyApplication
import com.example.happypets.Producto
import com.example.happypets.R
import com.example.happypets.databinding.FragmentProductoUsuarioBinding

class ProductoUsuarioFragment : Fragment() {

    private var _binding: FragmentProductoUsuarioBinding? = null
    private val binding get() = _binding!!
    private val carrito = MyApplication.carrito

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProductoUsuarioBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Obtener los datos del producto del bundle
        val args = arguments
        var producto: Producto? = null
        if (args != null) {
            val id = args.getInt("id")
            val nombre = args.getString("nombre")
            val descripcion = args.getString("descripcion")
            val precio = args.getDouble("precio")
            val stock = args.getInt("stock")
            val categoria = args.getString("categoria")

            producto = Producto(id, nombre!!, descripcion!!, precio, stock, categoria!!)

            // Mostrar los datos en la vista
            binding.textView13.text = nombre
            binding.DescripcionProducto.text = descripcion
            binding.PrecioProducto.text = "Precio: $$precio"

            val videoResourceName = "video_producto_$id"
            val videoResourceId = resources.getIdentifier(videoResourceName, "raw", requireContext().packageName)

            if (videoResourceId != 0) {
                val videoUri = Uri.parse("android.resource://${requireContext().packageName}/$videoResourceId")
                binding.videoView.setVideoURI(videoUri)
                binding.videoView.setMediaController(MediaController(requireContext()))
                binding.videoView.start()
            } else {
                Log.e("ProductoUsuarioFragment", "Video no encontrado: $videoResourceName")
                Toast.makeText(requireContext(), "Video no disponible", Toast.LENGTH_SHORT).show()
            }
            Log.d("ProductoUsuarioFragment", "ID del producto: $id")
            Log.d("ProductoUsuarioFragment", "Nombre del recurso: $videoResourceName")
            Log.d("ProductoUsuarioFragment", "ID del recurso: $videoResourceId")

        }

        /*binding.AgregarProducto2Button.setOnClickListener {
            producto?.let {
                Log.d("Carrito", "Agregando producto al carrito: ${it.nombre}")
                carrito.agregarProducto(it)
                Log.d("Carrito", "Producto agregado al carrito: ${carrito.productos}")
            }
        }*/

        binding.AtrasProductoUsuarioButton.setOnClickListener() {
            findNavController().navigate(R.id.action_productoUsuarioFragment_to_navigation_catalogo)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

