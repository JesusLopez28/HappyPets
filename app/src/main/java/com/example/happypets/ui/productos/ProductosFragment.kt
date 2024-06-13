package com.example.happypets.ui.productos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.happypets.Producto
import com.example.happypets.ProductoAdapter
import com.example.happypets.ProductoManager
import com.example.happypets.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ProductosFragment : Fragment(), ProductoAdapter.ProductoClickListener {

    private val viewModel: ProductosViewModel by viewModels()
    private lateinit var productoManager: ProductoManager
    private lateinit var adapter: ProductoAdapter
    private lateinit var productos: MutableList<Producto>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_productos, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView_Productos)
        val agregarProductoButton: FloatingActionButton = view.findViewById(R.id.AgregarProductoButton)

        agregarProductoButton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_productos_to_registrarProducto)
        }

        // Configurar el RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        productoManager = ProductoManager(requireContext())
        productos = productoManager.getAllProducts().toMutableList()
        adapter = ProductoAdapter(productos, this)
        recyclerView.adapter = adapter

        return view
    }

    override fun onMasButtonClick(position: Int) {
        val producto = productos[position]
        producto.stock -= 1
        productoManager.updateProduct(producto)
        adapter.notifyItemChanged(position)
    }

    override fun onMenosButtonClick(position: Int) {
        val producto = productos[position]
        if (producto.stock > 0) {
            producto.stock += 1
            productoManager.updateProduct(producto)
            adapter.notifyItemChanged(position)
        }
    }

    override fun onEliminarButtonClick(position: Int) {
        val producto = productos[position]
        productoManager.deleteProduct(producto.id)
        productos.removeAt(position)
        adapter.notifyItemRemoved(position)
    }
}



