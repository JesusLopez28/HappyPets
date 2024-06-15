package com.example.happypets.ui.perfil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.happypets.Mascotas
import com.example.happypets.R
import com.example.happypets.UserManager

class MostarMascotasFragment : Fragment() {
    private lateinit var userManager: UserManager
    private lateinit var listViewMisMascotas: ListView
    private lateinit var nombreMascota: EditText
    private lateinit var razaMascota: Spinner
    private lateinit var edadMascota: EditText
    private lateinit var agregarMascotaButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        var view = inflater.inflate(R.layout.fragment_mostrar_mascotas, container, false)
        listViewMisMascotas = view.findViewById(R.id.ListViewMascotas)
        nombreMascota = view.findViewById(R.id.NombreMascota_Mascota)
        razaMascota = view.findViewById(R.id.spinner_raza_Mascota)
        edadMascota = view.findViewById(R.id.edad_Mascota)
        agregarMascotaButton = view.findViewById(R.id.AgregarMascotaButton)

        val razaAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.raza,
            android.R.layout.simple_spinner_item
        )

        razaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        razaMascota.adapter = razaAdapter

        userManager = UserManager(requireContext())
        val email = requireActivity().intent.getStringExtra("email")
        val mascotas = userManager.getMascotasByEmail(requireContext(), email!!)
        if (mascotas.isEmpty()) {
            listViewMisMascotas.adapter = null
            return view
        } else {
            val mascotasList = mutableListOf<String>()
            for (mascota in mascotas) {
                val mascotaString =
                    "Nombre: ${mascota.nombre}, Raza: ${mascota.raza}, Edad: ${mascota.edad}"
                mascotasList.add(mascotaString)
            }
            val adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mascotasList)
            listViewMisMascotas.adapter = adapter
        }

        agregarMascotaButton.setOnClickListener {
            agregarMascota()
        }
        return view
    }

    fun agregarMascota() {
        // Validar campos
        if (nombreMascota.text.isEmpty() || edadMascota.text.isEmpty()) {
            Toast.makeText(requireContext(), "Llena todos los campos", Toast.LENGTH_SHORT).show()
        }

        val email = requireActivity().intent.getStringExtra("email")
        val user = userManager.getUserByEmail(email!!)
        val userId = user!!.id
        val mascota = Mascotas(
            nombreMascota.text.toString(),
            razaMascota.selectedItem.toString(),
            edadMascota.text.toString().toInt(),
            userId
        )

        userManager.agregarMascota(userId, mascota)
        val mascotas = userManager.getMascotasByEmail(requireContext(), email)
        val mascotasList = mutableListOf<String>()
        for (mascota in mascotas) {
            val mascotaString =
                "Nombre: ${mascota.nombre}, Raza: ${mascota.raza}, Edad: ${mascota.edad}"
            mascotasList.add(mascotaString)
        }
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mascotasList)
        listViewMisMascotas.adapter = adapter
    }
}