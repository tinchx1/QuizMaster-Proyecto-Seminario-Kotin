package com.example.quizmaster

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONObject
import java.io.InputStream

class Preguntas : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_preguntas)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_preguntas)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val category = intent.getStringExtra("category") ?: ""
        val textCategory = findViewById<TextView>(R.id.texto_categoria)
        textCategory.text = category
        loadQuestionsFromJSON(category)
    }

    private fun loadQuestionsFromJSON(category: String) {
        val inputStream: InputStream = assets.open("questions.json")
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        val text = String(buffer, Charsets.UTF_8)
        val jsonObject = JSONObject(text)
        val questions = jsonObject.getJSONArray(category)
        Log.d("Questions", questions.toString())
    }
}