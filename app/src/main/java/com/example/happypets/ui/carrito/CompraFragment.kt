package com.example.happypets.ui.carrito

import android.app.AlertDialog
import android.os.Bundle
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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.happypets.Carrito
import com.example.happypets.CarritoManager
import com.example.happypets.Compra
import com.example.happypets.R
import com.example.happypets.databinding.FragmentCarritoBinding
import com.example.happypets.databinding.FragmentCompraBinding
import org.w3c.dom.Text

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

            // Procesar la compra
            val mensaje = "¡Compra pagada exitosamente!\n" +
                    "ID Envio: $idCompra\n"
            Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show()

            // Limpiar los campos y vaciar el carrito
            limpiarCampos()
            carrito.productos.clear()
            carrito.calcularSubTotal()
            carrito.calcularIVA()
            carrito.calcularTotal()

            // findNavController().navigate(R.id.action_compraFragment_to_navigation_carrito)
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

