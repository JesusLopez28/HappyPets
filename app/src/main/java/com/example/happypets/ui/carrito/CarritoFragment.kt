package com.example.happypets.ui.carrito

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.happypets.Carrito
import com.example.happypets.CarritoManager
import com.example.happypets.databinding.FragmentCarritoBinding
import com.example.happypets.CarritoAdapter

class CarritoFragment : Fragment() {

    private var _binding: FragmentCarritoBinding? = null
    private val binding get() = _binding!!
    private lateinit var carritoAdapter: CarritoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarritoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val carrito = CarritoManager.obtenerCarrito()

        // Configurar el adaptador del RecyclerView
        carritoAdapter = CarritoAdapter(carrito.productos)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = carritoAdapter
        }

        // Mostrar subtotal, IVA y total en la vista
        binding.apply {
            SubtotalCarrito.text = "$ ${String.format("%.2f", carrito.subTotal)}"
            IvaCarrito.text = "$ ${String.format("%.2f", carrito.iva)}"
            TotalCarrito.text = "$ ${String.format("%.2f", carrito.total)}"
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


