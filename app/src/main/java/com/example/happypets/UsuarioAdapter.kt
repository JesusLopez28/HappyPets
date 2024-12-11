package com.example.happypets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.example.happypets.R
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class UsuarioAdapter(private val usuarios: MutableList<Map<String, Any>>) :
    RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder>() {

    private lateinit var holder: UsuarioViewHolder

    class UsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreTextView: TextView = itemView.findViewById(R.id.Nombre_Usuario)
        val toggleButton: ToggleButton = itemView.findViewById(R.id.toggleButton_admin)
        val eliminarButton: ImageButton = itemView.findViewById(R.id.eliminaUsuario)
    }

    private val client = OkHttpClient() // Cliente OkHttp

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.cardview_usuario, parent, false)
        return UsuarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = usuarios[position].toMutableMap()
        this.holder = holder

        // Establecer el nombre del usuario
        val nombre = usuario["nombre"] as? String ?: "Sin nombre"
        holder.nombreTextView.text = nombre

        // Establecer el estado del ToggleButton
        val tipo = usuario["type"] as? Int ?: 1
        holder.toggleButton.isChecked = tipo == 2

        val id = Integer.parseInt(usuario["id"].toString())

        // Configurar el listener para el ToggleButton
        holder.toggleButton.setOnCheckedChangeListener { _, isChecked ->
            val nuevoTipo = if (isChecked) 2 else 1
            usuario["type"] = nuevoTipo
            actualizarUsuarioEnApi(id, nuevoTipo)
        }

        // Configurar el listener para el botón de eliminar
        holder.eliminarButton.setOnClickListener {
            eliminarUsuarioDeApi(id, position)
        }
    }

    override fun getItemCount() = usuarios.size

    // Función para eliminar usuario a través de la API
    private fun eliminarUsuarioDeApi(usuarioId: Int, position: Int) {
        val url = "${Config.BASE_URL}/usuario/delete_user.php"
        val requestBody = FormBody.Builder()
            .add("id", usuarioId.toString())
            .build()

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Manejo de error en la eliminación
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    // Eliminar el usuario localmente si la respuesta es exitosa
                    val result = JSONObject(response.body?.string())
                    usuarios.removeAt(position)
                    holder.itemView.post {
                        notifyItemRemoved(position)
                        Toast.makeText(
                            holder.itemView.context,
                            "Usuario eliminado",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }

    // Función para actualizar el tipo de usuario a través de la API
    private fun actualizarUsuarioEnApi(usuarioId: Int, nuevoTipo: Int) {
        val url = "${Config.BASE_URL}/usuario/update_user_type.php"
        val requestBody = FormBody.Builder()
            .add("id", usuarioId.toString())
            .add("type", nuevoTipo.toString())
            .build()

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                if (response.isSuccessful && responseData != null) {
                    val result = JSONObject(responseData)
                    if (result.has("error")) {
                        holder.itemView.post {
                            Toast.makeText(
                                holder.itemView.context,
                                result.getString("error"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        val usuario = usuarios.find { it["id"] == usuarioId }
                        usuario?.set("type", nuevoTipo)

                        holder.itemView.post {
                            Toast.makeText(
                                holder.itemView.context,
                                "Tipo de usuario actualizado",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        })
    }
}

private fun <K, V> Map<K, V>?.set(v: V, nuevoTipo: V) {
    throw UnsupportedOperationException("Not yet implemented")
}
