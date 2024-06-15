package com.example.happypets.ui.citas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.happypets.Cita
import com.example.happypets.CitaManager
import com.example.happypets.R
import com.example.happypets.UserManager
import com.example.happypets.databinding.FragmentCitasBinding
import java.text.SimpleDateFormat
import java.util.Locale

class CitasFragment : Fragment() {

    private lateinit var citasManager: CitaManager
    private lateinit var calendarioUsuario: CalendarView
    private lateinit var horarioSppiner: Spinner
    private lateinit var mascotaSpinner: Spinner
    private lateinit var agendarCitaButton: Button
    private lateinit var misCitasButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        citasManager = CitaManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_citas, container, false)
        calendarioUsuario = view.findViewById(R.id.CalendarioUsuario)
        horarioSppiner = view.findViewById(R.id.HorarioSpinner)
        mascotaSpinner = view.findViewById(R.id.MascotaSpinner)
        agendarCitaButton = view.findViewById(R.id.AgendarCitaButton)
        misCitasButton = view.findViewById(R.id.MisCitasButton)

        val email = requireActivity().intent.getStringExtra("email")
        val usuario = email?.let { UserManager(requireContext()).getUserByEmail(it) }

        val mascotas = usuario?.mascota
        val mascotaNames = mascotas?.map { it.nombre }

        mascotaSpinner.adapter = mascotaNames?.let {
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, it)
        }

        val horarios = Array(9) { i -> "${i + 10}:00" }
        horarioSppiner.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, horarios)

        var selectedDate = ""
        calendarioUsuario.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
        }

        agendarCitaButton.setOnClickListener {
            val hora = horarioSppiner.selectedItem as String
            val mascota = mascotaSpinner.selectedItem as String
            val fecha = selectedDate

            // Validar campos
            if (fecha.isEmpty() || hora.isEmpty() || mascota.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Por favor, llene todos los campos",
                    Toast.LENGTH_SHORT
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

            val id = citasManager.getAllCitas(requireContext()).size + 1
            val cita = Cita(id, usuario!!, mascotas!!.find { it.nombre == mascota }!!, fecha, hora)
            citasManager.saveCita(cita)

            Toast.makeText(requireContext(), "Cita agendada", Toast.LENGTH_SHORT).show()
        }

        misCitasButton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_citas_user_to_navigation_mostrar_citas)
        }


        return view
    }
}