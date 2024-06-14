package com.example.happypets

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.happypets.databinding.ActivityMain2Binding
import com.example.happypets.ui.perfil_admin.PerfilAdminFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main2)
        navView.setupWithNavController(navController)

        val email = intent.getStringExtra("email")
        if (email != null) {
            val user = UserManager(this).getUserByEmail(email)
            if (user != null && user.type == 1) {  // Aseguramos que es un administrador
                val fragment = PerfilAdminFragment.newInstance(email)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main2, fragment)
                    .commit()
            } else {
                // Handle non-admin case or show a default fragment
            }
        }
    }
}

