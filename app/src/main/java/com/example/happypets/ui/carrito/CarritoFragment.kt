package com.example.happypets.ui.carrito

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.happypets.Config
import com.example.happypets.R
import com.example.happypets.databinding.FragmentCarritoBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class CarritoFragment : Fragment() {

    private var _binding: FragmentCarritoBinding? = null
    private val binding get() = _binding!!

    // Use mutable list of maps to store cart products
    private val productos = mutableListOf<MutableMap<String, Any>>()
    private lateinit var carritoAdapter: CarritoAdapter

    // Variables to store cart totals
    private var subTotal: Double = 0.0
    private var iva: Double = 0.0
    private var total: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarritoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Configure RecyclerView adapter
        carritoAdapter = CarritoAdapter(productos) { producto ->
            eliminarProductoDelCarrito(producto)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = carritoAdapter
        }

        // Fetch cart data when fragment is created
        obtenerCarrito()

        val comprarButton: Button = root.findViewById(R.id.ComprarButtonCarrito)
        comprarButton.setOnClickListener {
            findNavController().navigate(R.id.action_carritoFragment_to_compraFragment)
        }

        return root
    }

    private fun obtenerCarrito() {
        // Replace with actual user email or get from shared preferences
        val email = requireActivity().intent.getStringExtra("email")

        val url = "${Config.BASE_URL}/carrito/get_cart.php?email=$email"
        val client = OkHttpClient()

        val formBody = FormBody.Builder()
            .add("email", email.toString())
            .build()

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "Error al cargar el carrito",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                requireActivity().runOnUiThread {
                    if (response.isSuccessful && responseBody != null) {
                        try {
                            val jsonResponse = JSONObject(responseBody)

                            if (jsonResponse.has("carrito")) {
                                val carritoData = jsonResponse.getJSONObject("carrito")
                                val productosArray = carritoData.getJSONArray("productos")

                                // Clear previous products
                                productos.clear()

                                // Parse products
                                for (i in 0 until productosArray.length()) {
                                    val producto = productosArray.getJSONObject(i)
                                    productos.add(
                                        mutableMapOf(
                                            "id" to producto.getInt("id"),
                                            "nombre" to producto.getString("nombre"),
                                            "precio" to producto.getDouble("precio")
                                        )
                                    )
                                }

                                // Update totals
                                subTotal = carritoData.getDouble("subTotal")
                                iva = carritoData.getDouble("iva")
                                total = carritoData.getDouble("total")

                                // Update UI
                                carritoAdapter.notifyDataSetChanged()
                                actualizarTotales()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                requireContext(),
                                "Error al parsear el carrito",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Error al obtener el carrito",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }

    private fun eliminarProductoDelCarrito(producto: MutableMap<String, Any>) {
        val email = requireActivity().intent.getStringExtra("email")
        val url = "${Config.BASE_URL}/carrito/eliminar_producto.php"
        val client = OkHttpClient()

        val formBody = FormBody.Builder()
            .add("email", email.toString())
            .add("producto_id", producto["id"].toString())
            .build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "Error al eliminar producto",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                requireActivity().runOnUiThread {
                    if (response.isSuccessful) {
                        // Remove product from local list
                        productos.remove(producto)
                        carritoAdapter.notifyDataSetChanged()
                        obtenerCarrito() // Refresh cart to get updated totals
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Error al eliminar producto",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }

    private fun actualizarTotales() {
        binding.apply {
            SubtotalCarrito.text = "$ ${String.format("%.2f", subTotal)}"
            IvaCarrito.text = "$ ${String.format("%.2f", iva)}"
            TotalCarrito.text = "$ ${String.format("%.2f", total)}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}