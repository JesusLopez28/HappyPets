package com.example.happypets.ui.productos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.happypets.Config
import com.example.happypets.R
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class RegistrarProducto : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_registrar_producto, container, false)

        val nombreEditText: EditText = view.findViewById(R.id.Nombre_Producto)
        val descripcionEditText: EditText = view.findViewById(R.id.Descripcion_producto)
        val precioEditText: EditText = view.findViewById(R.id.Precio_producto)
        val stockEditText: EditText = view.findViewById(R.id.Stock_producto)
        val categoriaSpinner: Spinner = view.findViewById(R.id.spinner_categoria)
        val agregarButton: Button = view.findViewById(R.id.button_registro)
        val atrasAgregarProductoButton: ImageButton =
            view.findViewById(R.id.AtrasAgregarProductoButton)

        atrasAgregarProductoButton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_registrar_producto_to_navigation_productos)
        }

        // Definir las categorías directamente en el código
        val categorias = arrayOf("Alimento", "Juguetes", "Ropa", "Salud", "Hogar", "Paseo")

        // Configurar el Spinner de categorías
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categorias)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categoriaSpinner.adapter = adapter

        agregarButton.setOnClickListener {
            val nombre = nombreEditText.text.toString()
            val descripcion = descripcionEditText.text.toString()
            val precio = precioEditText.text.toString().toDoubleOrNull() ?: 0.0
            val stock = stockEditText.text.toString().toIntOrNull() ?: 0
            val categoria = categoriaSpinner.selectedItem.toString()

            if (nombre.isNotEmpty() && descripcion.isNotEmpty() && precio > 0 && stock >= 0 && categoria.isNotEmpty()) {
                if (precio <= 0) {
                    Toast.makeText(
                        requireContext(),
                        "El precio debe ser mayor a 0",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                if (stock < 0) {
                    Toast.makeText(
                        requireContext(),
                        "El stock no puede ser negativo",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                registrarProducto(nombre, descripcion, precio, stock, categoria)

                Toast.makeText(
                    requireContext(),
                    "Producto agregado exitosamente",
                    Toast.LENGTH_SHORT
                ).show()
                clearFields()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Por favor completa todos los campos correctamente",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        return view
    }

    private fun clearFields() {
        view?.findViewById<EditText>(R.id.Nombre_Producto)?.text?.clear()
        view?.findViewById<EditText>(R.id.Descripcion_producto)?.text?.clear()
        view?.findViewById<EditText>(R.id.Precio_producto)?.text?.clear()
        view?.findViewById<EditText>(R.id.Stock_producto)?.text?.clear()
        view?.findViewById<Spinner>(R.id.spinner_categoria)?.setSelection(0)
    }

    private fun registrarProducto(
        nombre: String,
        descripcion: String,
        precio: Double,
        stock: Int,
        categoria: String
    ) {
        val url = "${Config.BASE_URL}/producto/register.php"

        val formBody = FormBody.Builder()
            .add("nombre", nombre)
            .add("descripcion", descripcion)
            .add("precio", precio.toString())
            .add("stock", stock.toString())
            .add("categoria", categoria)
            .build()

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "Error al registrar el producto",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                requireActivity().runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            "Producto registrado correctamente",
                            Toast.LENGTH_SHORT
                        ).show()
                        clearFields()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Error al registrar el producto",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }
}
