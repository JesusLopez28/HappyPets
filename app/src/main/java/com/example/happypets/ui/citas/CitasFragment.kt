package com.example.happypets.ui.citas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.happypets.Config
import com.example.happypets.R
import com.example.happypets.databinding.FragmentCitasBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

class CitasFragment : Fragment() {

    private lateinit var calendarioUsuario: CalendarView
    private lateinit var horarioSpinner: Spinner
    private lateinit var mascotaSpinner: Spinner
    private lateinit var agendarCitaButton: Button
    private lateinit var misCitasButton: Button

    private val mascotas = mutableListOf<Map<String, Any>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_citas, container, false)

        calendarioUsuario = view.findViewById(R.id.CalendarioUsuario)
        horarioSpinner = view.findViewById(R.id.HorarioSpinner)
        mascotaSpinner = view.findViewById(R.id.MascotaSpinner)
        agendarCitaButton = view.findViewById(R.id.AgendarCitaButton)
        misCitasButton = view.findViewById(R.id.MisCitasButton)

        // Fetch user's pets
        fetchMascotas()

        // Setup horarios
        val horarios = Array(9) { i -> "${i + 10}:00" }
        horarioSpinner.adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item, horarios
        )

        var selectedDate = ""
        calendarioUsuario.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
        }

        agendarCitaButton.setOnClickListener {
            val hora = horarioSpinner.selectedItem as String
            val mascotaSeleccionada = mascotaSpinner.selectedItem as String
            val fecha = selectedDate

            // Validar campos
            if (fecha.isEmpty() || hora.isEmpty() || mascotaSeleccionada.isEmpty()) {
                Toast.makeText(
                    requireContext(), "Por favor, llene todos los campos", Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Validar que la fecha no sea anterior a la actual
            val currentDate = System.currentTimeMillis()
            val selectedDateMillis =
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(fecha)?.time ?: 0
            if (selectedDateMillis < currentDate) {
                Toast.makeText(
                    requireContext(),
                    "La fecha seleccionada no puede ser anterior a la actual",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Encontrar el ID de la mascota seleccionada
            val mascotaId = mascotas.find { it["nombre"] == mascotaSeleccionada }?.get("id") as? Int
            if (mascotaId != null) {
                agendarCita(mascotaId, fecha, hora)
            } else {
                Toast.makeText(
                    requireContext(), "Error al seleccionar la mascota", Toast.LENGTH_SHORT
                ).show()
            }
        }

        misCitasButton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_citas_user_to_navigation_mostrar_citas)
        }

        return view
    }

    private fun fetchMascotas() {
        val url = "${Config.BASE_URL}/mascota/get_mascotas.php"
        val client = OkHttpClient()

        // Obtener el email del usuario desde el intent
        val email = requireActivity().intent.getStringExtra("email")

        val formBody = FormBody.Builder().add("email", email ?: "").build()

        val request = Request.Builder().url(url).post(formBody).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    if (isAdded) {
                        Toast.makeText(
                            requireContext(), "Error al cargar las mascotas", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                requireActivity().runOnUiThread {
                    if (isAdded) {
                        if (response.isSuccessful) {
                            val responseData = response.body?.string()
                            try {
                                val jsonResponse = JSONObject(responseData)
                                val mascotasArray = jsonResponse.getJSONArray("data")

                                mascotas.clear()
                                for (i in 0 until mascotasArray.length()) {
                                    val mascota = mascotasArray.getJSONObject(i)
                                    mascotas.add(
                                        mapOf(
                                            "id" to mascota.getInt("id"),
                                            "nombre" to mascota.getString("nombre")
                                        )
                                    )
                                }

                                // Actualizar el spinner de mascotas
                                val mascotaNames = mascotas.map { it["nombre"] as String }
                                mascotaSpinner.adapter = ArrayAdapter(
                                    requireContext(),
                                    android.R.layout.simple_spinner_item,
                                    mascotaNames
                                )
                            } catch (e: Exception) {
                                Toast.makeText(
                                    requireContext(),
                                    "Error al procesar las mascotas",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                requireContext(), "Error al cargar las mascotas", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        })
    }

    private fun agendarCita(mascotaId: Int, fecha: String, hora: String) {
        val url = "${Config.BASE_URL}/cita/agendar_cita.php"
        val client = OkHttpClient()

        val formBody =
            FormBody.Builder().add("mascota_id", mascotaId.toString()).add("fecha", fecha)
                .add("hora", hora)
                .add("email", requireActivity().intent.getStringExtra("email") ?: "").build()

        val request = Request.Builder().url(url).post(formBody).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    if (isAdded) {
                        Toast.makeText(
                            requireContext(), "Error al agendar la cita", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                requireActivity().runOnUiThread {
                    if (isAdded) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                requireContext(), "Cita agendada exitosamente", Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                requireContext(), "Error al agendar la cita", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        })
    }
}