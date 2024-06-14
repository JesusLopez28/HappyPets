package com.example.happypets

import android.content.Context
import android.content.SharedPreferences

class UserManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun saveUser(user: Usuario) {
        editor.putString("user_${user.id}_nombre", user.nombre)
        editor.putString("user_${user.id}_email", user.email)
        editor.putString("user_${user.id}_telefono", user.telefono)
        editor.putString("user_${user.id}_password", user.password)
        editor.putString("user_${user.id}_direccion", user.direccion)
        editor.putString("user_${user.id}_mascota", user.mascota.toString())
        editor.putInt("user_${user.id}_type", user.type)
        editor.apply()
    }

    fun getUserById(id: Int): Usuario? {
        val nombre = sharedPreferences.getString("user_${id}_nombre", null)
        val email = sharedPreferences.getString("user_${id}_email", null)
        val telefono = sharedPreferences.getString("user_${id}_telefono", null)
        val password = sharedPreferences.getString("user_${id}_password", null)
        val direccion = sharedPreferences.getString("user_${id}_direccion", null)
        val mascota = sharedPreferences.getString("user_${id}_mascota", null)
        val type = sharedPreferences.getInt("user_${id}_type", -1)

        return if (nombre != null && email != null && telefono != null && password != null &&
            direccion != null && mascota != null && type != -1) {
            val mascotaList = arrayListOf<Mascotas>()
            Usuario(id, nombre, email, telefono, password, direccion, mascotaList, type)
        } else {
            null
        }
    }

    fun getUserByEmail(email: String): Usuario? {
        val allEntries = sharedPreferences.all
        for ((key, value) in allEntries) {
            if (key.endsWith("_email") && value == email) {
                val id = key.split("_")[1].toInt()
                return getUserById(id)
            }
        }
        return null
    }

    fun getAllUsers(): List<Usuario> {
        val users = mutableListOf<Usuario>()
        val allEntries = sharedPreferences.all
        val userIds = allEntries.keys.filter { it.endsWith("_nombre") }.map { it.split("_")[1].toInt() }

        for (id in userIds) {
            val user = getUserById(id)
            if (user != null) {
                users.add(user)
            }
        }
        return users
    }

    fun updateUser(user: Usuario) {
        // Aquí actualizamos los datos del usuario en SharedPreferences
        editor.putString("user_${user.id}_nombre", user.nombre)
        editor.putString("user_${user.id}_email", user.email)
        editor.putString("user_${user.id}_telefono", user.telefono)
        editor.putString("user_${user.id}_password", user.password)
        editor.putString("user_${user.id}_direccion", user.direccion)
        editor.putString("user_${user.id}_mascota", user.mascota.toString())
        editor.putInt("user_${user.id}_type", user.type)
        editor.apply()
    }

    fun removeUser(id: Int) {
        // Aquí eliminamos al usuario de SharedPreferences
        editor.remove("user_${id}_nombre")
        editor.remove("user_${id}_email")
        editor.remove("user_${id}_telefono")
        editor.remove("user_${id}_password")
        editor.remove("user_${id}_direccion")
        editor.remove("user_${id}_mascota")
        editor.remove("user_${id}_type")
        editor.apply()
    }
}
