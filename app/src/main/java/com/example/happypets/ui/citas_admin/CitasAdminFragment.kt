package com.example.happypets.ui.citas_admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.happypets.Config
import com.example.happypets.R
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class CitasAdminFragment : Fragment() {

    private lateinit var calendario: CalendarView
    private lateinit var buscar: Button
    private lateinit var listaCitas: ListView
    private lateinit var btnScanQR: ImageButton
    private lateinit var textFieldCitasEscaneadas: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_citas_admin, container, false)

        calendario = view.findViewById(R.id.CalendarioAdmin)
        buscar = view.findViewById(R.id.BuscarCitaButton)
        listaCitas = view.findViewById(R.id.ListaCitasAdmin)
        btnScanQR = view.findViewById(R.id.imageButton)
        textFieldCitasEscaneadas = view.findViewById(R.id.textoCitaEscaneada)

        var selectedDate = ""

        // Configuración del calendario
        calendario.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
        }

        // Configurar el botón para buscar citas
        buscar.setOnClickListener {
            if (selectedDate.isEmpty()) {
                Toast.makeText(requireContext(), "Seleccione una fecha", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            fetchCitas(selectedDate)
        }

        // Configurar el botón para escanear QR
        btnScanQR.setOnClickListener {
            iniciarEscaneoQR()
        }

        return view
    }

    private fun fetchCitas(fecha: String) {
        val url = "${Config.BASE_URL}/cita/get_citas_by_date.php?fecha=$fecha"
        val client = OkHttpClient()

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Error al cargar citas", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                requireActivity().runOnUiThread {
                    if (response.isSuccessful) {
                        val responseData = response.body?.string()
                        try {
                            val jsonResponse = JSONObject(responseData)
                            val success = jsonResponse.getBoolean("success")

                            if (success) {
                                val citasArray = jsonResponse.getJSONArray("data")
                                val citasList = mutableListOf<String>()
                                for (i in 0 until citasArray.length()) {
                                    val cita = citasArray.getJSONObject(i)
                                    val usuario = cita.getString("usuario")
                                    val mascota = cita.getString("mascota")
                                    val hora = cita.getString("hora")
                                    citasList.add("Usuario: $usuario, Mascota: $mascota, Hora: $hora")
                                }

                                val adapter = ArrayAdapter(
                                    requireContext(),
                                    android.R.layout.simple_list_item_1,
                                    citasList
                                )
                                listaCitas.adapter = adapter
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "No hay citas para esta fecha",
                                    Toast.LENGTH_SHORT
                                ).show()
                                listaCitas.adapter = null
                            }

                        } catch (e: Exception) {
                            Toast.makeText(
                                requireContext(),
                                "Error al procesar las citas",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e("CitasAdminFragment", "Error procesando el JSON: ${e.message}")
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "No se pudo obtener respuesta del servidor",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }


    private fun iniciarEscaneoQR() {
        val intentIntegrator = IntentIntegrator.forSupportFragment(this)
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        intentIntegrator.setPrompt("Escanea un código QR")
        intentIntegrator.setCameraId(0) // Usa la cámara trasera
        intentIntegrator.setBeepEnabled(true)
        intentIntegrator.setBarcodeImageEnabled(false)
        intentIntegrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result: IntentResult =
            IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                val qrText = result.contents

                try {
                    // Dividir el texto en partes
                    val parts = qrText.split(", ")

                    // Extraer valores específicos
                    val nombre = parts[0].split(": ")[1]
                    val raza = parts[1].split(": ")[1]
                    val edad = parts[2].split(": ")[1]

                    // Mostrar en el TextView
                    val text = "Nombre: $nombre\nRaza: $raza\nEdad: $edad"
                    textFieldCitasEscaneadas.text = text

                    // Log para depuración
                    Log.e("citaAdministrador", "Nombre: $nombre, Raza: $raza, Edad: $edad")

                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(),
                        "Error en el formato del QR",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("Error", "Formato incorrecto en el QR: ${e.message}")
                }
            } else {
                Toast.makeText(requireContext(), "Escaneo cancelado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
