package com.example.happypets.ui.citas_admin

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
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
import com.example.happypets.Cita
import com.example.happypets.CitaManager
import com.example.happypets.R
import com.example.happypets.UserManager
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class CitasAdminFragment : Fragment() {

    private lateinit var citasManager: CitaManager
    private lateinit var calendario : CalendarView
    private lateinit var buscar : Button
    private lateinit var listaCitas : ListView
    private lateinit var btnScanQR: ImageButton
    private lateinit var textFieldCitasEscaneadas : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        citasManager = CitaManager(requireContext())


    }

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

        // Configurar el botón para escanear
        btnScanQR.setOnClickListener {
            iniciarEscaneoQR()
        }

        var slectedDate = ""
        calendario.setOnDateChangeListener { _, year, month, dayOfMonth ->
            slectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
        }

        // Buscar citas para la fecha seleccionada
        buscar.setOnClickListener {
            val fecha = slectedDate
            val citas = citasManager.getCitasByDate(requireContext(), fecha)
            if (citas.isEmpty()) {
                listaCitas.adapter = null
                Toast.makeText(requireContext(), "No hay citas para esta fecha", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Ir llenado un adapter con las citas encontradas
            val citasList = mutableListOf<String>()
            for (cita in citas) {
                val citaString = "Usuario: ${cita.usuario.nombre}, Mascota: ${cita.mascota.nombre}, Hora: ${cita.hora}"
                citasList.add(citaString)
            }

            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, citasList)
            listaCitas.adapter = adapter
        }

        return view
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
        val result: IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                val qrText = result.contents

                try {
                    // Dividir el texto en partes
                    val parts = qrText.split(", ")

                    // Extraer valores específicos
                    val nombre = parts[0].split(": ")[1] // Nombre de la mascota
                    val raza = parts[1].split(": ")[1]   // Raza de la mascota
                    val edad = parts[2].split(": ")[1]   // Edad de la mascota

                    // Mostrar en el EditText
                    val text = "Nombre: $nombre\nRaza: $raza\nEdad: $edad"
                    textFieldCitasEscaneadas.setText(text)

                    // Log para depuración
                    Log.e("citaAdministrador", "Nombre: $nombre, Raza: $raza, Edad: $edad")

                } catch (e: Exception) {
                    // En caso de que el formato no sea el esperado
                    Toast.makeText(requireContext(), "Error en el formato del QR", Toast.LENGTH_SHORT).show()
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