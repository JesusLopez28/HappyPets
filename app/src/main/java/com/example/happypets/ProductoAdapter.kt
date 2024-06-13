package com.example.happypets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.happypets.R

class ProductoAdapter(
    private val productos: List<Producto>,
    private val listener: ProductoClickListener
) : RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    interface ProductoClickListener {
        fun onMasButtonClick(position: Int)
        fun onMenosButtonClick(position: Int)
        fun onEliminarButtonClick(position: Int)
    }

    class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreTextView: TextView = itemView.findViewById(R.id.Nombre_Producto)
        val precioTextView: TextView = itemView.findViewById(R.id.Precio_Producto)
        val stockTextView: TextView = itemView.findViewById(R.id.Cantidad_Stock)
        val masButton: ImageButton = itemView.findViewById(R.id.MasButton)
        val menosButton: ImageButton = itemView.findViewById(R.id.MenosButton)
        val eliminarButton: ImageButton = itemView.findViewById(R.id.EliminarButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_producto_admin, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]
        holder.nombreTextView.text = producto.nombre
        holder.precioTextView.text = "$${producto.precio}"
        holder.stockTextView.text = producto.stock.toString()

        holder.masButton.setOnClickListener {
            listener.onMasButtonClick(position)
        }

        holder.menosButton.setOnClickListener {
            listener.onMenosButtonClick(position)
        }

        holder.eliminarButton.setOnClickListener {
            listener.onEliminarButtonClick(position)
        }
    }

    override fun getItemCount() = productos.size
}

