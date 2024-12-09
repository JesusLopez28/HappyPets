package com.example.happypets

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.happypets.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        // Verificar si el Intent tiene el extra 'email'
       /* val email = intent.getStringExtra("email")
        if (email != null) {
            val user = UserManager(this).getUserByEmail(email)
        } else {
            Log.e("MainActivity", "Email extra is missing in Intent")
            Toast.makeText(this, "Error: Missing email", Toast.LENGTH_SHORT).show()
        }*/
    }
}
