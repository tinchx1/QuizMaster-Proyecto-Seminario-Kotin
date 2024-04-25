package ar.unlp.quizmaster

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.TextView
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
            findViewById(R.id.opcion4))

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
        Log.d("Questions", questionsJson.toString())

        return List(questionsJson.length()) { i ->
            Question(questionsJson.getJSONObject(i))
        }
    }

    private fun askQuestion(q: Question) {
        findViewById<TextView>(R.id.texto_pregunta).text = q.questionText
        buttons.forEachIndexed { index, button -> button.text = q.options[index] }
    }

    private fun answer(button: Button) {
        val correct = questions[questionIndex].isCorrect(button.text.toString())
        val correctButton = buttons[questions[questionIndex].correctAnswerIndex]
        val defaultColor = getColor(R.color.primaryButton)

        buttons.forEach { it.isClickable = false }
        if(correct) {
            button.setBackgroundColor(getColor(R.color.correct))
            correctAnswers++
            findViewById<TextView>(R.id.puntaje).text = correctAnswers.toString()
        } else {
            button.setBackgroundColor(getColor(R.color.incorrect))
            correctButton.setBackgroundColor(getColor(R.color.missed))
        }
        Handler().postDelayed({
            button.setBackgroundColor(defaultColor)
            if(!correct)
                correctButton.setBackgroundColor(defaultColor)
            if(++questionIndex < questions.size) {
                askQuestion(questions[questionIndex])
                buttons.forEach { it.isClickable = true }
            }
            else
                this.finish()
        }, 3000)

    }
}