package com.example.happypets.ui.carrito

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.happypets.R
import com.example.happypets.databinding.CardviewProductoCarritoBinding

class CarritoAdapter(
    private var productos: MutableList<MutableMap<String, Any>>,
    private val onEliminarClick: (MutableMap<String, Any>) -> Unit
) : RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder>() {

    inner class CarritoViewHolder(val binding: CardviewProductoCarritoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val nombreTextView = binding.NombreProductoCarrito
        val precioTextView = binding.PrecioProductoCarrito
        val productoImageView = binding.imagenProductoCarrito
        val eliminarProductoButton = binding.EliminarProductoCarrito
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarritoViewHolder {
        val binding = CardviewProductoCarritoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CarritoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarritoViewHolder, position: Int) {
        val producto = productos[position]
        holder.nombreTextView.text = producto["nombre"].toString()
        holder.precioTextView.text = "$${producto["precio"]}"

        val imageResourceName = "producto_${producto["id"]}"
        val imageResourceId = holder.productoImageView.context.resources.getIdentifier(
            imageResourceName,
            "drawable",
            holder.productoImageView.context.packageName
        )

        if (imageResourceId != 0) {
            holder.productoImageView.setImageResource(imageResourceId)
        } else {
            holder.productoImageView.setImageResource(R.drawable.icono1)
        }

        holder.eliminarProductoButton.setOnClickListener {
            onEliminarClick(producto)
        }
    }

    override fun getItemCount() = productos.size
}