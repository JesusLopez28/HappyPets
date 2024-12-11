package com.example.happypets.ui.usuarios

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.happypets.Config
import com.example.happypets.R
import com.example.happypets.UsuarioAdapter
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class UsuariosFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UsuarioAdapter
    private val usuarios = mutableListOf<Map<String, Any>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_usuarios, container, false)

        recyclerView = view.findViewById(R.id.recyclerview_Usuarios)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Obtener los usuarios desde la API
        fetchUsuarios()

        // Configurar el adaptador
        adapter = UsuarioAdapter(usuarios)
        recyclerView.adapter = adapter

        return view
    }

    private fun fetchUsuarios() {
        val url = "${Config.BASE_URL}/usuario/get_all_users.php"

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("UsuariosFragment", "Error de red: ${e.message}")
                requireActivity().runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "Error al cargar los usuarios",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                requireActivity().runOnUiThread {
                    if (response.isSuccessful && responseData != null) {
                        try {
                            val jsonResponse = JSONObject(responseData)
                            val usuariosArray = jsonResponse.getJSONArray("data")
                            usuarios.clear()

                            for (i in 0 until usuariosArray.length()) {
                                val usuario = usuariosArray.getJSONObject(i)
                                val usuarioMap = mapOf(
                                    "id" to usuario.getString("id"),
                                    "nombre" to usuario.getString("nombre"),
                                    "type" to usuario.getInt("type")
                                )
                                usuarios.add(usuarioMap)
                            }

                            adapter.notifyDataSetChanged()

                        } catch (e: Exception) {
                            Log.e("UsuariosFragment", "Error al parsear el JSON: ${e.message}")
                            Toast.makeText(
                                requireContext(),
                                "Error al procesar los datos",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Error al obtener datos: ${response.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }
}
