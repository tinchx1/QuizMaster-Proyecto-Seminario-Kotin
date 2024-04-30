package ar.unlp.quizmaster

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONObject
import java.io.InputStream

class Preguntas : AppCompatActivity() {

    private lateinit var options: Array<Button>
    private lateinit var questions: Array<Question>
    private var questionIndex = 0
    private var correctAnswers = 0
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
        findViewById<TextView>(R.id.texto_categoria).text = category

        options = arrayOf(
            findViewById(R.id.opcion1),
            findViewById(R.id.opcion2),
            findViewById(R.id.opcion3),
            findViewById(R.id.opcion4)
        )

        options.forEach { it ->
            it.setOnClickListener { answer(it as Button) }
        }

        questions = questionsFromJSON(category)
    }

    override fun onStart() {
        super.onStart()
        askQuestion(questions[questionIndex])
        findViewById<TextView>(R.id.num_preguntas).text = questions.size.toString()
    }


    private fun questionsFromJSON(category: String): Array<Question> {
        val inputStream: InputStream = assets.open("questions.json")
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        val text = String(buffer, Charsets.UTF_8)
        val jsonObject = JSONObject(text)
        val questionsJson = jsonObject.getJSONArray(category)
        return Array(questionsJson.length()) { i ->
            Question(questionsJson.getJSONObject(i))
        }
    }

    private fun askQuestion(q: Question) {
        findViewById<TextView>(R.id.texto_pregunta).text = q.questionText
        options.forEachIndexed { index, button -> button.text = q.options[index] }
    }

    public fun handleComodin(v: View) {
        val comodin = findViewById<Button>(R.id.comodin)
        findViewById<TextView>(R.id.num_preguntas).text = (questions.size - 1).toString()
        comodin.isEnabled = false
        nextOrFinish()
    }

    private fun nextOrFinish() {
        questionIndex++
        if (questionIndex < questions.size)
            askQuestion(questions[questionIndex])
        else
            this.finish()
    }

    private fun answer(button: Button) {
        val correctButton = options[questions[questionIndex].correctAnswerIndex]
        val previousColorStateList = button.backgroundTintList
        options.forEach { it.isClickable = false }

        val correct = questions[questionIndex].isCorrect(button.text.toString())
        if (correct) {
            val correctColorStateList = ColorStateList.valueOf(getColor(R.color.correct))
            button.backgroundTintList = correctColorStateList
            correctAnswers++
            findViewById<TextView>(R.id.puntaje).text = correctAnswers.toString()
        } else {
            val incorrectColorStateList = ColorStateList.valueOf(getColor(R.color.incorrect))
            val missedColorStateList = ColorStateList.valueOf(getColor(R.color.missed))
            button.backgroundTintList = incorrectColorStateList
            correctButton.backgroundTintList = missedColorStateList
        }
        Handler().postDelayed({
            // Restaurar colores
            button.backgroundTintList = previousColorStateList
            if (!correct)
                correctButton.backgroundTintList = previousColorStateList
            nextOrFinish()
            options.forEach { it.isClickable = true }
        }, 3000)

    }
}