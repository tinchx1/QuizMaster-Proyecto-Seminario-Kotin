package ar.unlp.quizmaster

import android.content.SharedPreferences
import com.google.gson.Gson

object UserManager {
    lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()

    fun commit(user: User) {
        val json = gson.toJson(user)
        val editor = sharedPreferences.edit()
        editor?.putString(user.name, json)
        editor?.apply()
    }

    fun add(userName: String) {
        // Guarda solo si un usuario con ese nombre no existe
        if (!sharedPreferences.contains(userName)) {
            val user = User(userName)
            commit(user)
        }
    }

    fun get(userName: String): User {
        assert(sharedPreferences.contains(userName))
        val json = sharedPreferences.getString(userName, null)
        return gson.fromJson(json, User::class.java)
    }
}