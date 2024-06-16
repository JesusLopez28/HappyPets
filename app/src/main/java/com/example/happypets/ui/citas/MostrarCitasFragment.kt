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
import com.example.happypets.CitaManager
import com.example.happypets.ProductoManager
import com.example.happypets.R
import com.example.happypets.databinding.FragmentMostrarCitasBinding

class MostrarCitasFragment : Fragment() {

    private lateinit var citaManager: CitaManager
    private lateinit var listViewMisCitas: ListView
    private lateinit var atrasMostrarCitasutton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        var view = inflater.inflate(R.layout.fragment_mostrar_citas, container, false)
        listViewMisCitas = view.findViewById(R.id.ListViewMisCitas)
        citaManager = CitaManager(requireContext())
        atrasMostrarCitasutton = view.findViewById(R.id.AtrasMostrarCitasutton)

        atrasMostrarCitasutton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_mostrar_citas_to_navigation_citas_user)
        }

        val email = requireActivity().intent.getStringExtra("email")
        val citas = citaManager.getCitasByEmail(requireContext(), email!!)
        if (citas.isEmpty()) {
            listViewMisCitas.adapter = null
            Toast.makeText(requireContext(), "No hay citas", Toast.LENGTH_SHORT).show()
            return view
        }else {
            val citasList = mutableListOf<String>()
            for (cita in citas) {
                val citaString = "Mascota: ${cita.mascota.nombre}, Fecha: ${cita.fecha}, Hora: ${cita.hora}"
                citasList.add(citaString)
            }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, citasList)
            listViewMisCitas.adapter = adapter
        }
        return view
    }
}
