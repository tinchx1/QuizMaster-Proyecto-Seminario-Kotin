package ar.unlp.quizmaster

import android.content.SharedPreferences
import com.google.gson.Gson

object UserManager {
    lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()

    /**
     * @return Si el usuario superó su propio puntaje
     */
    fun commit(user: User): Boolean {
        val better = sharedPreferences.contains(user.name) && user.correct > get(user.name).correct
        val json = gson.toJson(user)
        val editor = sharedPreferences.edit()
        editor?.putString(user.name, json)
        editor?.apply()
        return better
    }

    fun get(userName: String): User {
        assert(sharedPreferences.contains(userName))
        val json = sharedPreferences.getString(userName, null)
        return gson.fromJson(json, User::class.java)
    }

    fun topUsers(): List<User> {
        return sharedPreferences.all.values
            .map { gson.fromJson(it.toString(), User::class.java) }
            .filter { it.correct > 0 }
            .sortedByDescending { it.correct }
            .take(5)
    }
}