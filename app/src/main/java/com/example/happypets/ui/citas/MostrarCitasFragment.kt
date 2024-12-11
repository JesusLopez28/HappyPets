package com.example.happypets.ui.citas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.happypets.Config
import com.example.happypets.R
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class MostrarCitasFragment : Fragment() {

    private lateinit var listViewMisCitas: ListView
    private lateinit var atrasMostrarCitasutton: ImageButton
    private val client = OkHttpClient()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_mostrar_citas, container, false)
        listViewMisCitas = view.findViewById(R.id.ListViewMisCitas)
        atrasMostrarCitasutton = view.findViewById(R.id.AtrasMostrarCitasutton)

        atrasMostrarCitasutton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_mostrar_citas_to_navigation_citas_user)
        }

        val email = requireActivity().intent.getStringExtra("email")
        if (email != null) {
            fetchCitas(email)
        } else {
            Toast.makeText(
                requireContext(),
                "Error al obtener el email del usuario",
                Toast.LENGTH_SHORT
            ).show()
        }

        return view
    }

    private fun fetchCitas(email: String) {
        val url = "${Config.BASE_URL}/cita/get_citas.php"
        val formBody = FormBody.Builder().add("email", email).build()
        val request = Request.Builder().url(url).post(formBody).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "Error al cargar las citas",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                requireActivity().runOnUiThread {
                    if (response.isSuccessful) {
                        val responseData = response.body?.string()
                        try {
                            val citasArray = JSONArray(JSONObject(responseData).getString("data"))
                            if (citasArray.length() == 0) {
                                Toast.makeText(requireContext(), "No hay citas", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                val citasList = mutableListOf<String>()
                                for (i in 0 until citasArray.length()) {
                                    val cita = citasArray.getJSONObject(i)
                                    val citaString =
                                        "Mascota: ${cita.getString("mascota_nombre")}, Fecha: ${
                                            cita.getString(
                                                "fecha"
                                            )
                                        }, Hora: ${cita.getString("hora")}"
                                    citasList.add(citaString)
                                }
                                val adapter = ArrayAdapter(
                                    requireContext(),
                                    android.R.layout.simple_list_item_1,
                                    citasList
                                )
                                listViewMisCitas.adapter = adapter
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                requireContext(),
                                "Error al procesar las citas",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Error al cargar las citas",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }
}
