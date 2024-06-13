package com.example.happypets.ui.productos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.happypets.Producto
import com.example.happypets.ProductoManager
import com.example.happypets.R

class RegistrarProducto : Fragment() {

    private lateinit var productoManager: ProductoManager

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

        productoManager = ProductoManager(requireContext())

        // Definir las categorías directamente en el código
        val categorias = arrayOf("Alimento", "Juguetes", "Ropa", "Salud", "Hogar", "Paseo")

        // Configurar el Spinner de categorías
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categorias)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categoriaSpinner.adapter = adapter

        agregarButton.setOnClickListener {
            val nombre = nombreEditText.text.toString()
            val descripcion = descripcionEditText.text.toString()
            val precio = precioEditText.text.toString().toDoubleOrNull() ?: 0.0
            val stock = stockEditText.text.toString().toIntOrNull() ?: 0
            val categoria = categoriaSpinner.selectedItem.toString()

            if (nombre.isNotEmpty() && descripcion.isNotEmpty() && precio > 0 && stock >= 0 && categoria.isNotEmpty()) {
                val producto = Producto(
                    id = productoManager.generateProductId(),
                    nombre = nombre,
                    descripcion = descripcion,
                    precio = precio,
                    stock = stock,
                    categoria = categoria
                )
                productoManager.saveProduct(producto)
                Toast.makeText(requireContext(), "Producto agregado exitosamente", Toast.LENGTH_SHORT).show()
                clearFields()
            } else {
                Toast.makeText(requireContext(), "Por favor completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
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
}
