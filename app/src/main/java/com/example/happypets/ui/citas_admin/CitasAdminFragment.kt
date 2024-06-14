package com.example.happypets.ui.citas_admin

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.ListView
import android.widget.Toast
import com.example.happypets.Cita
import com.example.happypets.CitaManager
import com.example.happypets.R
import com.example.happypets.UserManager

class CitasAdminFragment : Fragment() {

    private lateinit var citasManager: CitaManager
    private lateinit var calendario : CalendarView
    private lateinit var buscar : Button
    private lateinit var listaCitas : ListView
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
            for (cita in citas) {
                val citaString = "Usuario: ${cita.usuario.nombre}, Mascota: ${cita.mascota.nombre}, Hora: ${cita.hora}"
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    listOf(citaString)
                )
                listaCitas.adapter = adapter
            }
        }

        return view
    }
}