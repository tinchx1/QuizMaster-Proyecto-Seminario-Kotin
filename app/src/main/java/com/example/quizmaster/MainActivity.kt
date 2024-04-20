package com.example.quizmaster

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ScrollView>(R.id.linear_main).children.filter { it is Button }.forEach {
            v -> v.setOnClickListener { launchCategory(v) }
        }
    }

    private fun launchCategory(v: View) {
        val i = Intent(this, preguntas::class.java)
        val questionsFilename = when(v.id) {
            R.id.cat_ciencia -> "ciencia.json"
            R.id.cat_historia -> "historia.json"
            R.id.cat_geografia -> "geografia.json"
            R.id.cat_deporte -> "deporte.json"
            R.id.cat_entretenimiento -> "entretenimiento.json"
            R.id.cat_tecnologia -> "tecnologia.json"
            R.id.cat_arte_cultura -> "arte_cultura.json"
            R.id.cat_literatura -> "literatura.json"
            R.id.cat_filosofia -> "filosofia.json"
            R.id.cat_gastronomia -> "gastronomia.json"
            R.id.cat_musica -> "musica.json"
            R.id.cat_idiomas -> "idiomas.json"
            else -> "medicina.json" // R.id.cat_medicina
        }
        i.putExtra("questionsFilename", questionsFilename)
        startActivity(i)
    }
}
