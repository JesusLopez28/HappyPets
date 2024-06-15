package com.example.happypets.ui.carrito

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.happypets.CarritoManager
import com.example.happypets.Producto
import com.example.happypets.databinding.FragmentCarritoBinding
import com.example.happypets.CarritoAdapter
import com.example.happypets.R
import androidx.navigation.fragment.findNavController

class CarritoFragment : Fragment(), CarritoAdapter.CarritoItemListener {

    private var _binding: FragmentCarritoBinding? = null
    private val binding get() = _binding!!
    private lateinit var carritoAdapter: CarritoAdapter
    private val carrito = CarritoManager.obtenerCarrito()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarritoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Configurar el adaptador del RecyclerView
        carritoAdapter = CarritoAdapter(carrito.productos, this)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = carritoAdapter
        }

        // Mostrar subtotal, IVA y total en la vista
        actualizarTotales()

        val comprarButton: Button = root.findViewById(R.id.ComprarButtonCarrito)
        comprarButton.setOnClickListener {
            findNavController().navigate(R.id.action_carritoFragment_to_compraFragment)
        }

        return root
    }

    private fun actualizarTotales() {
        binding.apply {
            SubtotalCarrito.text = "$ ${String.format("%.2f", carrito.subTotal)}"
            IvaCarrito.text = "$ ${String.format("%.2f", carrito.iva)}"
            TotalCarrito.text = "$ ${String.format("%.2f", carrito.total)}"
        }
    }

    override fun onEliminarProductoClick(producto: Producto) {
        carrito.quitarProducto(producto)
        carritoAdapter.updateList(carrito.productos)
        actualizarTotales()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



