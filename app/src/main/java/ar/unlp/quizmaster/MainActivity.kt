package ar.unlp.quizmaster

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private var activityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result -> if (result.resultCode == RESULT_OK) {
            val correctAnswers = result.data?.getIntExtra("correctAnswers", Int.MIN_VALUE) ?: Int.MIN_VALUE
            val text = resources.getQuantityString(R.plurals.correct_answers_message, correctAnswers, correctAnswers) + " " +
                if (correctAnswers > 0) getString(R.string.congrats) else getString(R.string.better_luck_next_time)
            Snackbar.make(findViewById(R.id.main), text, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ScrollView>(R.id.linear_main).children.filter { it is Button }.forEach { v ->
            v.setOnClickListener { launchCategory(v) }
        }
    }

    private fun launchCategory(v: View) {
        val button = findViewById<Button>(v.id)
        val textCategory = button.text.toString()
        val i = Intent(this, Preguntas::class.java)
        i.putExtra("category", textCategory)
        activityResult.launch(i)
    }
}
