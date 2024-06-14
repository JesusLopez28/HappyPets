package com.example.happypets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.happypets.databinding.CardviewProductoBinding

class ProductoAdapterUser(
    private var productos: List<Producto>,
    private val listener: ProductoClickListener? = null
) : RecyclerView.Adapter<ProductoAdapterUser.ProductoViewHolder>() {

    interface ProductoClickListener {
        fun onAgregarCarritoClick(position: Int)
    }

    class ProductoViewHolder(val binding: CardviewProductoBinding) : RecyclerView.ViewHolder(binding.root) {
        val nombreTextView: TextView = binding.NombreProducto
        val precioTextView: TextView = binding.PrecioProducto
        val productoImageView: ImageView = binding.imageView3
        val agregarCarritoButton: View = binding.button3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val binding = CardviewProductoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]
        holder.nombreTextView.text = producto.nombre
        holder.precioTextView.text = "$${producto.precio}"

        // Load image from drawable resources using the product ID
        val imageResourceName = "producto_${producto.id}"
        val imageResourceId = holder.productoImageView.context.resources.getIdentifier(imageResourceName, "drawable", holder.productoImageView.context.packageName)

        if (imageResourceId != 0) { // Check if resource exists
            holder.productoImageView.setImageResource(imageResourceId)
        } else {
            holder.productoImageView.setImageResource(R.drawable.icono1)// Optional placeholder image
        }

        holder.agregarCarritoButton.setOnClickListener {
            listener?.onAgregarCarritoClick(position)
        }
    }

    override fun getItemCount() = productos.size

    fun updateList(newList: List<Producto>) {
        productos = newList
        notifyDataSetChanged()
    }
}





