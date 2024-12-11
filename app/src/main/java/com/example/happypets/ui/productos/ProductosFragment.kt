package com.example.happypets.ui.productos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.happypets.Config
import com.example.happypets.Producto
import com.example.happypets.ProductoAdapter
import com.example.happypets.ProductoManager
import com.example.happypets.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class ProductosFragment : Fragment(), ProductoAdapter.ProductoClickListener {

    private val viewModel: ProductosViewModel by viewModels()
    private lateinit var adapter: ProductoAdapter
    private val productos = mutableListOf<Map<String, Any>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_productos, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView_Productos)
        val agregarProductoButton: FloatingActionButton =
            view.findViewById(R.id.AgregarProductoButton)

        agregarProductoButton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_productos_to_registrarProducto)
        }

        fetchProductos()

        // Configurar el RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ProductoAdapter(productos, this)
        recyclerView.adapter = adapter

        return view
    }

    private fun fetchProductos() {
        val url = "${Config.BASE_URL}/producto/get_all_products.php"

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    if (isAdded) {  // Verificar si el fragmento está agregado
                        Toast.makeText(
                            requireContext(),
                            "Error al cargar los productos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                requireActivity().runOnUiThread {
                    if (isAdded) {  // Verificar si el fragmento está agregado
                        if (response.isSuccessful && responseData != null) {
                            try {
                                val jsonResponse = JSONObject(responseData)
                                val productosArray = jsonResponse.getJSONArray("data")
                                for (i in 0 until productosArray.length()) {
                                    val producto = productosArray.getJSONObject(i)
                                    val id = producto.getInt("id")
                                    val nombre = producto.getString("nombre")
                                    val precio = producto.getDouble("precio")
                                    val stock = producto.getInt("stock")

                                    productos.add(
                                        mapOf(
                                            "id" to id,
                                            "nombre" to nombre,
                                            "precio" to precio,
                                            "stock" to stock,
                                        )
                                    )
                                }
                                adapter.notifyDataSetChanged()
                            } catch (e: Exception) {
                                if (isAdded) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Error al cargar los productos",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } else {
                            if (isAdded) {
                                Toast.makeText(
                                    requireContext(),
                                    "Error al cargar los productos",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        })
    }

    override fun onMasButtonClick(position: Int) {
        val producto = productos[position]
        val nuevoStock = producto["stock"] as Int + 1  // Incrementar stock
        actualizarStockProducto(producto["id"] as Int, nuevoStock, position)
    }

    override fun onMenosButtonClick(position: Int) {
        val producto = productos[position]
        val stockActual = producto["stock"] as Int
        if (stockActual > 0) {
            val nuevoStock = stockActual - 1  // Decrementar stock
            actualizarStockProducto(producto["id"] as Int, nuevoStock, position)
        }
    }

    override fun onEliminarButtonClick(position: Int) {
        val producto = productos[position]
        eliminarProducto(producto["id"] as Int, position)
    }

    // Función para actualizar el stock de un producto
    private fun actualizarStockProducto(productoId: Int, nuevoStock: Int, position: Int) {
        val url = "${Config.BASE_URL}/producto/update_producto_stock.php"
        val client = OkHttpClient()
        val formBody = FormBody.Builder()
            .add("id", productoId.toString())
            .add("stock", nuevoStock.toString())
            .build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    if (isAdded) {  // Verificar si el fragmento está agregado
                        Toast.makeText(
                            requireContext(),
                            "Error al actualizar stock",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                requireActivity().runOnUiThread {
                    if (isAdded) {  // Verificar si el fragmento está agregado
                        if (response.isSuccessful) {
                            // Actualizar la lista de productos
                            productos[position] = productos[position].toMutableMap().apply {
                                put("stock", nuevoStock)
                            }
                            adapter.notifyItemChanged(position)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Error al actualizar stock",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        })
    }

    // Función para eliminar un producto
    private fun eliminarProducto(productoId: Int, position: Int) {
        val url = "${Config.BASE_URL}/producto/eliminar_producto.php"
        val client = OkHttpClient()
        val formBody = FormBody.Builder()
            .add("id", productoId.toString())
            .build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    if (isAdded) {  // Verificar si el fragmento está agregado
                        Toast.makeText(
                            requireContext(),
                            "Error al eliminar el producto",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                requireActivity().runOnUiThread {
                    if (isAdded) {  // Verificar si el fragmento está agregado
                        if (response.isSuccessful) {
                            productos.removeAt(position)  // Eliminar producto de la lista
                            adapter.notifyItemRemoved(position)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Error al eliminar el producto",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        })
    }
}
