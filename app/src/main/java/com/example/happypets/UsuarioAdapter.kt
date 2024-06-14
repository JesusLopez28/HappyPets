package com.example.happypets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.example.happypets.R
import com.example.happypets.Usuario
import com.example.happypets.UserManager

class UsuarioAdapter(private val usuarios: MutableList<Usuario>) : RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder>() {

    class UsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreTextView: TextView = itemView.findViewById(R.id.Nombre_Usuario)
        val toggleButton: ToggleButton = itemView.findViewById(R.id.toggleButton_admin)
        val eliminarButton: ImageButton = itemView.findViewById(R.id.eliminaUsuario)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_usuario, parent, false)
        return UsuarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = usuarios[position]
        holder.nombreTextView.text = usuario.nombre
        holder.toggleButton.isChecked = usuario.type == 1 // Si type es 1, es admin; de lo contrario, es usuario normal

        holder.toggleButton.setOnCheckedChangeListener { _, isChecked ->
            usuario.type = if (isChecked) 1 else 2 // Cambiar el tipo de usuario según el estado del ToggleButton
            val context = holder.itemView.context
            val userManager = UserManager(context)
            userManager.updateUser(usuario) // Actualizar el usuario en SharedPreferences
        }


        holder.eliminarButton.setOnClickListener {
            val context = holder.itemView.context
            val userManager = UserManager(context)
            userManager.removeUser(usuario.id)
            usuarios.removeAt(position)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() = usuarios.size
}



