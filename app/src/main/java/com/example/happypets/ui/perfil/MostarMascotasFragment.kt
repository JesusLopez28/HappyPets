package com.example.happypets.ui.perfil

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.happypets.Mascotas
import com.example.happypets.R
import com.example.happypets.UserManager
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.io.File
import java.io.FileOutputStream

class MostarMascotasFragment : Fragment() {
    private lateinit var userManager: UserManager
    private lateinit var listViewMisMascotas: ListView
    private lateinit var nombreMascota: EditText
    private lateinit var razaMascota: Spinner
    private lateinit var edadMascota: EditText
    private lateinit var agregarMascotaButton: Button
    private lateinit var atrasMostrasMascotasButton: ImageButton

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
        atrasMostrasMascotasButton = view.findViewById(R.id.AtrasMostrasMascotasButton)

        atrasMostrasMascotasButton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_mascotas_to_navigation_perfil)
        }

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

        val mascotaString = mascota.toString()

        if(mascotaString.isNotEmpty()){
            try {
                // Generar el código QR
                val barcodeEncoder = BarcodeEncoder()
                val bitmap: Bitmap = barcodeEncoder.encodeBitmap(
                    mascotaString,
                    BarcodeFormat.QR_CODE,
                    400,
                    400
                )

                // Guardar el QR localmente
                saveQRCode(bitmap, "qr_code_${mascota.nombre}.png")
                Toast.makeText(requireContext(), "QR de mascota generado y guardado", Toast.LENGTH_SHORT).show()

            } catch (e: WriterException) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error al generar QR", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Por favor ingresa texto", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveQRCode(bitmap: Bitmap, fileName: String) {
        try {
            val file = File(requireContext().getExternalFilesDir(null), fileName)
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            Toast.makeText(requireContext(), "QR guardado en: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error al guardar el QR", Toast.LENGTH_SHORT).show()
        }
    }
}