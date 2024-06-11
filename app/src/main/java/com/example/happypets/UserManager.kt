package com.example.happypets

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class UserManager(private val context: Context) {
    private val gson = Gson()
    private val fileName = "users.json"

    fun saveUser(user: Usuario) {
        val users = getUsers().toMutableList()
        users.add(user)
        saveUsersToFile(users)
    }

    fun getUsers(): List<Usuario> {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) return emptyList()
        val json = file.readText()
        val type = object : TypeToken<List<Usuario>>() {}.type
        return gson.fromJson(json, type)
    }

    private fun saveUsersToFile(users: List<Usuario>) {
        val json = gson.toJson(users)
        val file = File(context.filesDir, fileName)
        file.writeText(json)
    }
}
