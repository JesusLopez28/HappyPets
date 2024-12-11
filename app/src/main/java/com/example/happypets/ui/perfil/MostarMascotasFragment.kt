package com.example.happypets.ui.perfil

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.happypets.Config
import com.example.happypets.Mascotas
import com.example.happypets.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class MostarMascotasFragment : Fragment() {
    private lateinit var listViewMisMascotas: ListView
    private lateinit var nombreMascota: EditText
    private lateinit var razaMascota: Spinner
    private lateinit var edadMascota: EditText
    private lateinit var agregarMascotaButton: Button
    private lateinit var atrasMostrasMascotasButton: ImageButton
    private val mascotas = mutableListOf<Map<String, Any>>()

    private val client = OkHttpClient()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_mostrar_mascotas, container, false)
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

        val email = requireActivity().intent.getStringExtra("email")
        email?.let {
            fetchMascotas(it)
        }

        agregarMascotaButton.setOnClickListener {
            agregarMascota(email!!)
        }
        return view
    }

    // Función para obtener las mascotas del servidor
    fun fetchMascotas(email: String) {
        val url = "${Config.BASE_URL}/mascota/get_mascotas.php"
        val client = OkHttpClient()

        val requestBody = FormBody.Builder()
            .add("email", email)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "Error al obtener mascotas",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                requireActivity().runOnUiThread {
                    if (response.isSuccessful && responseData != null) {
                        try {
                            val jsonResponse = JSONObject(responseData)
                            val mascotasArray = jsonResponse.getJSONArray("data")
                            mascotas.clear()  // Limpiar lista de mascotas actual
                            for (i in 0 until mascotasArray.length()) {
                                val mascota = mascotasArray.getJSONObject(i)
                                val id = mascota.getInt("id")
                                val nombre = mascota.getString("nombre")
                                val raza = mascota.getString("raza")
                                val edad = mascota.getInt("edad")

                                mascotas.add(
                                    mapOf(
                                        "id" to id,
                                        "nombre" to nombre,
                                        "raza" to raza,
                                        "edad" to edad
                                    )
                                )
                            }

                            val mascotasList = mascotas.map {
                                "Nombre: ${it["nombre"]}, Raza: ${it["raza"]}, Edad: ${it["edad"]}"
                            }
                            val adapter = ArrayAdapter(
                                requireContext(), android.R.layout.simple_list_item_1, mascotasList
                            )
                            listViewMisMascotas.adapter = adapter
                        } catch (e: Exception) {
                            Toast.makeText(
                                requireContext(),
                                "Error al cargar las mascotas",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Error al cargar las mascotas",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }

    // Función para agregar una mascota a la base de datos a través de la API
    fun agregarMascota(email: String) {
        val nombre = nombreMascota.text.toString()
        val raza = razaMascota.selectedItem.toString()
        val edad = edadMascota.text.toString().toIntOrNull()

        if (nombre.isEmpty() || raza.isEmpty() || edad == null) {
            Toast.makeText(requireContext(), "Llena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "${Config.BASE_URL}/mascota/agregar_mascota.php"
        val requestBody = FormBody.Builder()
            .add("email", email)
            .add("nombre", nombre)
            .add("raza", raza)
            .add("edad", edad.toString())
            .build()

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Error al agregar mascota", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                requireActivity().runOnUiThread {
                    if (response.isSuccessful) {
                        fetchMascotas(email) // Actualizar lista de mascotas después de agregar una
                        Toast.makeText(requireContext(), "Mascota agregada con éxito", Toast.LENGTH_SHORT).show()

                        // Generar y guardar QR
                        val qrContent = "Nombre: $nombre, Raza: $raza, Edad: $edad"
                        generarYGuardarQR(qrContent)
                    } else {
                        Toast.makeText(requireContext(), "Error al agregar mascota", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun generarYGuardarQR(contenido: String) {
        try {
            val width = 500
            val height = 500
            val bitMatrix: BitMatrix = MultiFormatWriter().encode(contenido, BarcodeFormat.QR_CODE, width, height)
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }

            // Guardar el QR en la galería
            val savedUri = guardarImagenEnGaleria(bitmap)
            requireActivity().runOnUiThread {
                if (savedUri != null) {
                    Toast.makeText(requireContext(), "QR guardado en galería", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Error al guardar QR", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), "Error al generar QR", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun guardarImagenEnGaleria(bitmap: Bitmap): Uri? {
        val resolver = requireContext().contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "MascotaQR_${System.currentTimeMillis()}.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/MascotasQR")
        }

        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
        }
        return uri
    }
}
