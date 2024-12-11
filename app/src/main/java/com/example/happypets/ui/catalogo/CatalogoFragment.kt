package com.example.happypets.ui.catalogo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.happypets.CarritoManager
import com.example.happypets.Config
import com.example.happypets.ProductoAdapterUser
import com.example.happypets.R
import com.example.happypets.databinding.FragmentCatalogoBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class CatalogoFragment : Fragment(), ProductoAdapterUser.ProductoClickListener {

    private var _binding: FragmentCatalogoBinding? = null
    private val binding get() = _binding!!
    private lateinit var productoAdapter: ProductoAdapterUser
    private var productosList = mutableListOf<Map<String, Any>>()
    private var filteredList = mutableListOf<Map<String, Any>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatalogoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Configurar RecyclerView
        productoAdapter = ProductoAdapterUser(filteredList, this)
        binding.RecyclerCatalogo1.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = productoAdapter
        }

        // Configurar barra de búsqueda
        binding.BuscarProductoView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                filteredList = if (newText.isNullOrEmpty()) {
                    productosList
                } else {
                    productosList.filter {
                        (it["nombre"] as String).contains(newText, ignoreCase = true)
                    }.toMutableList()
                }
                productoAdapter.updateList(filteredList)
                return true
            }
        })

        fetchProductos()

        return root
    }

    private fun fetchProductos() {
        val url = "${Config.BASE_URL}/producto/get_all_products.php"
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    if (isAdded) {
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
                    if (isAdded) {
                        if (response.isSuccessful && responseData != null) {
                            try {
                                val jsonResponse = JSONObject(responseData)
                                val productosArray = jsonResponse.getJSONArray("data")
                                productosList.clear()
                                for (i in 0 until productosArray.length()) {
                                    val producto = productosArray.getJSONObject(i)
                                    productosList.add(
                                        mapOf(
                                            "id" to producto.getInt("id"),
                                            "nombre" to producto.getString("nombre"),
                                            "precio" to producto.getDouble("precio"),
                                            "stock" to producto.getInt("stock"),
                                            "descripcion" to producto.getString("descripcion"),
                                            "categoria" to producto.getString("categoria")
                                        )
                                    )
                                }
                                filteredList = productosList.toMutableList()
                                productoAdapter.updateList(filteredList)
                            } catch (e: Exception) {
                                Toast.makeText(
                                    requireContext(),
                                    "Error al procesar los datos",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Error al cargar los productos",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        })
    }

    override fun onProductoClick(producto: Map<String, Any>) {
        val bundle = Bundle().apply {
            putInt("id", producto["id"] as Int)
            putString("nombre", producto["nombre"] as String)
            putString("descripcion", producto["descripcion"] as String)
            putDouble("precio", producto["precio"] as Double)
            putInt("stock", producto["stock"] as Int)
            putString("categoria", producto["categoria"] as String)
        }
        findNavController().navigate(
            R.id.action_navigation_catalogo_to_productoUsuarioFragment,
            bundle
        )
    }


    override fun onAgregarCarritoClick(position: Int) {
        val producto = filteredList[position]
        // peticion para agregar producto al carrito
        val url = "${Config.BASE_URL}/carrito/add_to_cart.php"
        val email = requireActivity().intent.getStringExtra("email")
        val requestBody = FormBody.Builder()
            .add("id_producto", producto["id"].toString())
            .add(
                "email", email!!
            )
            .build()

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    if (isAdded) {
                        Toast.makeText(
                            requireContext(),
                            "Error al agregar producto al carrito",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                requireActivity().runOnUiThread {
                    if (isAdded) {
                        if (response.isSuccessful && responseData != null) {
                            Toast.makeText(
                                requireContext(),
                                "Producto agregado al carrito",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Error al agregar producto al carrito",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}











