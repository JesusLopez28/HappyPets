package com.example.happypets

import android.content.Context
import android.content.SharedPreferences

class CitaManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun saveCita(cita: Cita) {
        editor.putInt("cita_${cita.id}_id", cita.id)
        editor.putString("cita_${cita.id}_fecha", cita.fecha)
        editor.putString("cita_${cita.id}_hora", cita.hora)
        editor.putInt("cita_${cita.id}_usuario_id", cita.usuario.id)
        editor.putString("cita_${cita.id}_mascota_name", cita.mascota.nombre)
        editor.apply()
    }

    fun getCitaById(id: Int): Cita? {
        val fecha = sharedPreferences.getString("cita_${id}_fecha", null)
        val hora = sharedPreferences.getString("cita_${id}_hora", null)
        val usuarioId = sharedPreferences.getInt("cita_${id}_usuario_id", -1)
        val mascotaName = sharedPreferences.getString("cita_${id}_mascota_name", null)
        if (fecha == null || hora == null || usuarioId == -1 || mascotaName == null) {
            return null
        }
        val context = sharedPreferences as Context
        val usuario = UserManager(context).getUserById(usuarioId) ?: return null
        // Obtener la mascota del usuario
        var mascota = Mascotas()
        for (m in usuario.mascota) {
            if (m.nombre == mascotaName) {
                mascota = m
                break
            }
        }
        return Cita(id, usuario, mascota, fecha, hora)
    }

    fun getCitasByUserId(userId: Int): ArrayList<Cita> {
        val citas = ArrayList<Cita>()
        while (true) {
            val id = sharedPreferences.getInt("cita_${citas.size}_id", -1)
            if (id == -1) {
                break
            }
            val fecha = sharedPreferences.getString("cita_${id}_fecha", null)
            val hora = sharedPreferences.getString("cita_${id}_hora", null)
            val usuarioId = sharedPreferences.getInt("cita_${id}_usuario_id", -1)
            val mascotaName = sharedPreferences.getString("cita_${id}_mascota_name", null)
            if (fecha == null || hora == null || usuarioId == -1 || mascotaName == null) {
                break
            }
            if (usuarioId == userId) {
                val context = sharedPreferences as Context
                val usuario = UserManager(context).getUserById(usuarioId) ?: return ArrayList()
                // Obtener la mascota del usuario
                var mascota = Mascotas()
                for (m in usuario.mascota) {
                    if (m.nombre == mascotaName) {
                        mascota = m
                        break
                    }
                }
                citas.add(Cita(id, usuario, mascota, fecha, hora))
            }
        }
        return citas
    }

    fun deleteCitaById(id: Int): Boolean {
        val fecha = sharedPreferences.getString("cita_${id}_fecha", null)
        val hora = sharedPreferences.getString("cita_${id}_hora", null)
        val usuarioId = sharedPreferences.getInt("cita_${id}_usuario_id", -1)
        val mascotaName = sharedPreferences.getString("cita_${id}_mascota_name", null)
        if (fecha == null || hora == null || usuarioId == -1 || mascotaName == null) {
            return false
        }
        editor.remove("cita_${id}_id")
        editor.remove("cita_${id}_fecha")
        editor.remove("cita_${id}_hora")
        editor.remove("cita_${id}_usuario_id")
        editor.remove("cita_${id}_mascota_name")
        editor.apply()
        return true
    }
}