package com.example.happypets

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.happypets.databinding.ActivityMain2Binding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main2) as NavHostFragment
        val navController = navHostFragment.navController
        navView.setupWithNavController(navController)

        val email = intent.getStringExtra("email")

        // Paso el email a través del NavController
        val navGraph = navController.navInflater.inflate(R.navigation.mobile_navigation2)
        navGraph.setStartDestination(R.id.navigation_perfil_admin)
        navController.setGraph(navGraph, Bundle().apply {
            putString("email", email)
        })
    }
}




