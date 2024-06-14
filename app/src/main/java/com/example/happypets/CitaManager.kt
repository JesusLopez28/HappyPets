package com.example.happypets

import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.Locale

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

    // getCitasByDate
    fun getCitasByDate(context: Context, fecha: String): List<Cita> {
        val citas = mutableListOf<Cita>()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val inputDate = dateFormat.parse(fecha.replace(" ", ""))

        sharedPreferences.all.forEach { (key, value) ->
            if (key.contains("cita_") && key.contains("_fecha")) {
                val citaId = key.split("_")[1].toInt()
                val storedFecha = (value as String).replace(" ", "")
                val storedDate = dateFormat.parse(storedFecha)

                if (inputDate == storedDate) {
                    val usuarioId = sharedPreferences.getInt("cita_${citaId}_usuario_id", 0)
                    val usuario = UserManager(context).getUserById(usuarioId)
                    val mascotaName = sharedPreferences.getString("cita_${citaId}_mascota_name", "")
                    val mascota = usuario?.mascota?.find { it.nombre == mascotaName }
                    val hora = sharedPreferences.getString("cita_${citaId}_hora", "")
                    if (usuario != null && mascota != null) {
                        citas.add(Cita(citaId, usuario, mascota, fecha, hora!!))
                    }
                }
            }
        }
        return citas
    }


}