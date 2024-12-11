package com.example.happypets.ui.carrito

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.happypets.Config
import com.example.happypets.MailSender
import com.example.happypets.MainActivity
import com.example.happypets.R
import com.example.happypets.UserManager
import com.example.happypets.databinding.FragmentCompraBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class CompraFragment : Fragment() {

    private lateinit var Direccion: EditText
    private lateinit var Metodo_Pago: Spinner
    private lateinit var Tipo_Envio: Spinner
    private lateinit var ButtonPagar: Button
    private lateinit var SubtotalCompra: TextView
    private lateinit var IvaCompra: TextView
    private lateinit var TotalCompra: TextView
    private lateinit var atrasCompraButton: ImageButton

    private val carrito = mutableListOf<MutableMap<String, Any>>()
    private var subTotal: Double = 0.0
    private var iva: Double = 0.0
    private var total: Double = 0.0
    private lateinit var email: String

    private var _binding: FragmentCompraBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCompraBinding.inflate(inflater, container, false)
        val view = binding.root

        Direccion = binding.DireccionEntrega
        Metodo_Pago = binding.spinnerMetodoPago
        Tipo_Envio = binding.spinnerTipoEnvio
        ButtonPagar = binding.PagarButton
        SubtotalCompra = binding.SubtotalCompra
        IvaCompra = binding.IvaCompra
        TotalCompra = binding.TotalCompra
        atrasCompraButton = binding.AtrasCompraButton

        // Obtener email del intent
        email = requireActivity().intent.getStringExtra("email") ?: ""

        // Configurar spinners
        val MetodoPagoAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.metodos_pago,
            android.R.layout.simple_spinner_item
        )
        MetodoPagoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        Metodo_Pago.adapter = MetodoPagoAdapter

        val TipoEnvioAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.tipos_envio,
            android.R.layout.simple_spinner_item
        )
        TipoEnvioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        Tipo_Envio.adapter = TipoEnvioAdapter

        // Obtener carrito desde el backend
        obtenerCarrito()

        ButtonPagar.setOnClickListener {
            PagarCompra()
        }

        atrasCompraButton.setOnClickListener {
            findNavController().navigate(R.id.action_compraFragment_to_carritoFragment)
        }

        return view
    }

    private fun obtenerCarrito() {
        val url = "${Config.BASE_URL}/carrito/get_cart.php?email=$email"
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "Error al cargar el carrito",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                requireActivity().runOnUiThread {
                    if (response.isSuccessful && responseBody != null) {
                        try {
                            val jsonResponse = JSONObject(responseBody)

                            if (jsonResponse.has("carrito")) {
                                val carritoData = jsonResponse.getJSONObject("carrito")
                                val productosArray = carritoData.getJSONArray("productos")

                                // Limpiar lista anterior
                                carrito.clear()

                                // Parsear productos
                                for (i in 0 until productosArray.length()) {
                                    val producto = productosArray.getJSONObject(i)
                                    carrito.add(
                                        mutableMapOf(
                                            "id" to producto.getInt("id"),
                                            "nombre" to producto.getString("nombre"),
                                            "precio" to producto.getDouble("precio")
                                        )
                                    )
                                }

                                // Actualizar totales
                                subTotal = carritoData.getDouble("subTotal")
                                iva = carritoData.getDouble("iva")
                                total = carritoData.getDouble("total")

                                // Actualizar UI con totales
                                SubtotalCompra.text = "$ ${String.format("%.2f", subTotal)}"
                                IvaCompra.text = "$ ${String.format("%.2f", iva)}"
                                TotalCompra.text = "$ ${String.format("%.2f", total)}"
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                requireContext(),
                                "Error al parsear el carrito",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Error al obtener el carrito",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }

    private fun PagarCompra() {
        val direccion = Direccion.text.toString()
        val metodoPago = Metodo_Pago.selectedItem.toString()
        val tipoEnvio = Tipo_Envio.selectedItem.toString()

        if (direccion.isEmpty() || direccion.isBlank() || Metodo_Pago.selectedItemPosition == 0 || Tipo_Envio.selectedItemPosition == 0) {
            Toast.makeText(
                requireContext(),
                "Por favor completa todos los campos",
                Toast.LENGTH_SHORT
            ).show()
            return
        } else {
            val idCompra = System.currentTimeMillis().toInt() // Generar ID único

            // Preparar datos para la solicitud de registro de compra
            val productosArray = JSONArray()
            carrito.forEach { producto ->
                val productJson = JSONObject().apply {
                    put("id", producto["id"])
                    put("nombre", producto["nombre"])
                    put("precio", producto["precio"])
                }
                productosArray.put(productJson)
            }

            val client = OkHttpClient()
            val url = "${Config.BASE_URL}/compra/registrar_compra.php"

            val formBody = FormBody.Builder()
                .add("email", email)
                .add("direccion", direccion)
                .add("metodo_pago", metodoPago)
                .add("tipo_envio", tipoEnvio)
                .build()

            val request = Request.Builder()
                .url(url)
                .post(formBody)
                .build()

            lifecycleScope.launch {
                try {
                    val response = withContext(Dispatchers.IO) {
                        client.newCall(request).execute()
                    }

                    if (response.isSuccessful) {
                        // Envío de correo electrónico
                        val emailSubject = "Compra realizada en Happy Pets"
                        val emailBody = "Detalles de la compra:\n" +
                                "ID Compra: $idCompra\n" +
                                "Productos: ${carrito.joinToString { it["nombre"].toString() }}\n" +
                                "Dirección de entrega: $direccion\n" +
                                "Método de pago: $metodoPago\n" +
                                "Tipo de envío: $tipoEnvio\n" +
                                "Subtotal: $subTotal\n" +
                                "IVA: $iva\n" +
                                "Total: $total"

                        val mailSender = MailSender(email, emailSubject, emailBody)

                        mailSender.send()

                        withContext(Dispatchers.Main) {
                            val mensaje = "¡Compra pagada exitosamente!\n" +
                                    "ID Compra: $idCompra\n"
                            Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show()

                            limpiarCampos()
                            carrito.clear()

                            mostrarNotificacion(idCompra)

                            // Abrir Google Maps para direcciones
                            val originInput = "C. Nueva Escocia 1885, Providencia 5a Secc., 44638"
                            val destinationInput = direccion

                            if (originInput.isNotEmpty() && destinationInput.isNotEmpty()) {
                                val gmmIntentUri =
                                    Uri.parse("https://www.google.com/maps/dir/?api=1&origin=$originInput&destination=$destinationInput")
                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                mapIntent.setPackage("com.google.android.apps.maps")
                                startActivity(mapIntent)
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                requireContext(),
                                "Error al registrar la compra",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        e.printStackTrace()
                        Toast.makeText(
                            requireContext(),
                            "Error al procesar la compra: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun mostrarNotificacion(idCompra: Int) {
        val notificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = System.currentTimeMillis().toInt() // ID único para cada notificación
        val channelId = "compra_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(channelId) == null) {
                val channel = NotificationChannel(
                    channelId,
                    "Compras",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Notificaciones de compras"
                }
                notificationManager.createNotificationChannel(channel)
            }
        }

        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationBuilder = NotificationCompat.Builder(requireContext(), channelId)
            .setSmallIcon(R.drawable.campana)
            .setContentTitle("Compra Exitosa")
            .setContentText("Tu compra con ID $idCompra ha sido procesada exitosamente.")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    private fun limpiarCampos() {
        Direccion.text.clear()
        Metodo_Pago.setSelection(0)
        Tipo_Envio.setSelection(0)
        SubtotalCompra.setText("$0.00")
        IvaCompra.setText("$0.00")
        TotalCompra.setText("$0.00")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


