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
import ar.unlp.quizmaster.Preguntas
import ar.unlp.quizmaster.R

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

        findViewById<ScrollView>(R.id.main).children.filter { it is Button }.forEach { v ->
            v.setOnClickListener { launchCategory(v) }
        }
    }

    private fun launchCategory(v: View) {
        val button = findViewById<Button>(v.id)
        val textCategory = button.text.toString()
        val i = Intent(this, Preguntas::class.java)
        i.putExtra("category", textCategory)
        startActivity(i)
    }
}
