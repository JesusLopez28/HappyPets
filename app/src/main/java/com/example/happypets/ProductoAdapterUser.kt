package com.example.happypets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.happypets.databinding.CardviewProductoBinding

class ProductoAdapterUser(
    private var productos: List<Map<String, Any>>,
    private val listener: ProductoClickListener? = null
) : RecyclerView.Adapter<ProductoAdapterUser.ProductoViewHolder>() {

    interface ProductoClickListener {
        fun onProductoClick(producto: Map<String, Any>)
        fun onAgregarCarritoClick(position: Int)
    }

    class ProductoViewHolder(val binding: CardviewProductoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val nombreTextView: TextView = binding.NombreProducto
        val precioTextView: TextView = binding.PrecioProducto
        val productoImageView: ImageView = binding.imageView3
        val agregarCarritoButton: View = binding.button3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val binding =
            CardviewProductoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]
        val nombre = producto["nombre"] as? String ?: "Sin nombre"
        val precio = producto["precio"] as? Double ?: 0.0
        val id = producto["id"] as? Int ?: 0

        holder.nombreTextView.text = nombre
        holder.precioTextView.text = "$${precio}"

        // Cargar imagen desde recursos usando el ID (si aplica)
        val imageResourceName = "producto_$id"
        val imageResourceId = holder.productoImageView.context.resources.getIdentifier(
            imageResourceName, "drawable", holder.productoImageView.context.packageName
        )

        if (imageResourceId != 0) {
            holder.productoImageView.setImageResource(imageResourceId)
        } else {
            holder.productoImageView.setImageResource(R.drawable.icono1)
        }

        holder.itemView.setOnClickListener {
            listener?.onProductoClick(producto)
        }

        holder.agregarCarritoButton.setOnClickListener {
            listener?.onAgregarCarritoClick(position)
        }
    }

    override fun getItemCount() = productos.size

    fun updateList(newList: List<Map<String, Any>>) {
        productos = newList
        notifyDataSetChanged()
    }
}
