package com.example.happypets.ui.carrito

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.happypets.Carrito
import com.example.happypets.CarritoManager
import com.example.happypets.Compra
import com.example.happypets.MailSender
import com.example.happypets.R
import com.example.happypets.UserManager
import com.example.happypets.databinding.FragmentCompraBinding
import kotlinx.coroutines.launch

class CompraFragment : Fragment() {

    private lateinit var Direccion: EditText
    private lateinit var Metodo_Pago: Spinner
    private lateinit var Tipo_Envio: Spinner
    private lateinit var ButtonPagar: Button
    private lateinit var SubtotalCompra: TextView
    private lateinit var IvaCompra: TextView
    private lateinit var TotalCompra: TextView

    private lateinit var carrito: Carrito

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

        carrito = CarritoManager.obtenerCarrito()

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

        SubtotalCompra.text = "$ ${String.format("%.2f", carrito.subTotal)}"
        IvaCompra.text = "$ ${String.format("%.2f", carrito.iva)}"
        TotalCompra.text = "$ ${String.format("%.2f", carrito.total)}"

        ButtonPagar.setOnClickListener {
            PagarCompra()
        }

        return view
    }

    private fun PagarCompra() {
        val direccion = Direccion.text.toString()
        val metodoPago = Metodo_Pago.selectedItem.toString()
        val tipoEnvio = Tipo_Envio.selectedItem.toString()

        if (direccion.isEmpty() || direccion.isBlank() || Metodo_Pago.selectedItemPosition == 0 || Tipo_Envio.selectedItemPosition == 0) {
            Toast.makeText(requireContext(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        } else {
            val idCompra = carrito.id + 1

            val nuevaCompra = Compra(
                idCompra,
                carrito.productos,
                direccion,
                metodoPago,
                tipoEnvio
            )
            val email = requireActivity().intent.getStringExtra("email")

// Obtener el correo electrónico del usuario desde UserManager
            val userManager = UserManager(requireContext())
            val usuario = email?.let { userManager.getUserByEmail(it) }

// Verificar si se encontró un usuario para el email proporcionado
            val usuarioEmail = usuario?.email ?: run {
                Log.e("CompraFragment", "Usuario no encontrado para el email: $email")
                return   // o realiza alguna acción alternativa en caso de no encontrar el usuario
            }

// Envío de correo electrónico al usuario
            val emailSubject = "Compra realizada en Happy Pets"
            val emailBody = "Detalles de la compra:\n" +
                    "ID Compra: $idCompra\n" +
                    "Productos: ${carrito.productos.joinToString { it.nombre }}\n" +
                    "Dirección de entrega: $direccion\n" +
                    "Método de pago: $metodoPago\n" +
                    "Tipo de envío: $tipoEnvio\n" +
                    "Subtotal: ${carrito.subTotal}\n" +
                    "IVA: ${carrito.iva}\n" +
                    "Total: ${carrito.total}"

            val mailSender = MailSender(usuarioEmail, emailSubject, emailBody)

// Manejar el envío del correo dentro de un contexto coroutine
            lifecycleScope.launch {
                try {
                    mailSender.send()
                    // Éxito al enviar el correo
                    val mensaje = "¡Compra pagada exitosamente!\n" +
                            "ID Compra: $idCompra\n"
                    Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show()

                    // Limpiar campos y vaciar carrito
                    limpiarCampos()
                    carrito.productos.clear()
                    carrito.calcularSubTotal()
                    carrito.calcularIVA()
                    carrito.calcularTotal()

                    // Navegar de regreso al carrito o a la pantalla deseada
                    //findNavController().navigate(R.id.action_compraFragment_to_navigation_carrito)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Error al enviar el correo electrónico", Toast.LENGTH_SHORT).show()
                }
            }

        }
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


