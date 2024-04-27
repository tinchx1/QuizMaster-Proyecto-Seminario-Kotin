package ar.unlp.quizmaster

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONObject
import java.io.InputStream

class Preguntas : AppCompatActivity() {

    private lateinit var buttons: Array<Button>
    private lateinit var questions: List<Question>
    private var questionIndex = 0
    private var correctAnswers = 0
    private var comodinState = false
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

        buttons = arrayOf(
            findViewById(R.id.opcion1),
            findViewById(R.id.opcion2),
            findViewById(R.id.opcion3),
            findViewById(R.id.opcion4)
        )

        buttons.forEach { it ->
            it.setOnClickListener { answer(it as Button) }
        }

        questions = questionsFromJSON(category)
    }

    override fun onStart() {
        super.onStart()
        askQuestion(questions[questionIndex])
        findViewById<TextView>(R.id.num_preguntas).text = questions.size.toString()
    }


    private fun questionsFromJSON(category: String): List<Question> {
        val inputStream: InputStream = assets.open("questions.json")
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        val text = String(buffer, Charsets.UTF_8)
        val jsonObject = JSONObject(text)
        val questionsJson = jsonObject.getJSONArray(category)
        return List(questionsJson.length()) { i ->
            Question(questionsJson.getJSONObject(i))
        }
    }

    private fun askQuestion(q: Question) {
        findViewById<TextView>(R.id.texto_pregunta).text = q.questionText
        buttons.forEachIndexed { index, button -> button.text = q.options[index] }
    }

    public fun handleComodin(v: View) {
        if (((questionIndex + 1) < questions.size) && (!comodinState)) {
            comodinState = true
            questionIndex++
            askQuestion(questions[questionIndex])
        } else if (questionIndex + 1 == questions.size) {
            Toast.makeText(this, "Es tu última pregunta", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Ya has usado el comodín", Toast.LENGTH_SHORT).show()
        }
    }

    private fun answer(button: Button) {
        val correctButton = buttons[questions[questionIndex].correctAnswerIndex]
        val previousColorStateList = button.backgroundTintList

        buttons.forEach { it.isClickable = false }

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

            if (++questionIndex < questions.size) {
                askQuestion(questions[questionIndex])
                buttons.forEach { it.isClickable = true }
            } else
                this.finish()
        }, 3000)

    }
}