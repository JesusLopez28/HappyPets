package com.example.happypets.ui.catalogo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.happypets.Producto
import com.example.happypets.ProductoAdapterUser
import com.example.happypets.ProductoManager
import com.example.happypets.databinding.FragmentCatalogoBinding

class CatalogoFragment : Fragment() {

    private var _binding: FragmentCatalogoBinding? = null
    private val binding get() = _binding!!
    private lateinit var productoManager: ProductoManager
    private lateinit var productoAdapter: ProductoAdapterUser
    private var productosList = listOf<Producto>()
    private var filteredList = listOf<Producto>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatalogoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        productoManager = ProductoManager(requireContext())
        productosList = productoManager.getAllProducts()
        filteredList = productosList

        productoAdapter = ProductoAdapterUser(filteredList)

        binding.RecyclerCatalogo1.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = productoAdapter
        }

        binding.BuscarProductoView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filteredList = if (newText.isNullOrEmpty()) {
                    productosList
                } else {
                    productosList.filter {
                        it.nombre.contains(newText, ignoreCase = true)
                    }
                }
                productoAdapter.updateList(filteredList)
                return true
            }
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}






