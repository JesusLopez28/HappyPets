package com.example.happypets

import android.content.Context

class ProductoManager(private val context: Context) {

    private val sharedPreferences = context.getSharedPreferences("productos_prefs", Context.MODE_PRIVATE)

    fun generateProductId(): Int {
        val currentId = sharedPreferences.getInt("current_id", 0)
        val newId = currentId + 1
        sharedPreferences.edit().putInt("current_id", newId).apply()
        return newId
    }

    fun saveProduct(producto: Producto) {
        val editor = sharedPreferences.edit()
        val id = producto.id
        editor.putInt("producto_${id}_id", producto.id)
        editor.putString("producto_${id}_nombre", producto.nombre)
        editor.putString("producto_${id}_descripcion", producto.descripcion)
        editor.putFloat("producto_${id}_precio", producto.precio.toFloat())
        editor.putInt("producto_${id}_stock", producto.stock)
        editor.putString("producto_${id}_categoria", producto.categoria)
        editor.apply()
    }

    fun getAllProducts(): List<Producto> {
        val productos = mutableListOf<Producto>()
        val allEntries = sharedPreferences.all
        val ids = allEntries.keys.filter { it.contains("producto_") && it.contains("_id") }
            .map { it.split("_")[1].toInt() }

        for (id in ids) {
            val nombre = sharedPreferences.getString("producto_${id}_nombre", null) ?: continue
            val descripcion = sharedPreferences.getString("producto_${id}_descripcion", null) ?: continue
            val precio = sharedPreferences.getFloat("producto_${id}_precio", 0.0f).toDouble()
            val stock = sharedPreferences.getInt("producto_${id}_stock", 0)
            val categoria = sharedPreferences.getString("producto_${id}_categoria", null) ?: continue

            productos.add(Producto(id, nombre, descripcion, precio, stock, categoria))
        }

        return productos
    }

    fun updateProduct(producto: Producto) {
        saveProduct(producto) // Reutilizamos saveProduct para actualizar el producto
    }

    fun deleteProduct(id: Int) {
        val editor = sharedPreferences.edit()
        editor.remove("producto_${id}_id")
        editor.remove("producto_${id}_nombre")
        editor.remove("producto_${id}_descripcion")
        editor.remove("producto_${id}_precio")
        editor.remove("producto_${id}_stock")
        editor.remove("producto_${id}_categoria")
        editor.apply()
    }

    fun clearAllProducts() {
        sharedPreferences.edit().clear().apply()
    }
}
