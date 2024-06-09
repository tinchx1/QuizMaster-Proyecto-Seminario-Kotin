package ar.unlp.quizmaster

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.io.InputStream

class Preguntas : AppCompatActivity() {

    private lateinit var comodin: ImageView
    private lateinit var options: Array<Button>
    private lateinit var questions: Array<Question>
    private var questionIndex = 0
    private var answeredQuestions = 0
    private var correctAnswers = 0
    private lateinit var currentUser: User // Nunca debería ser null
    private var timer: CountDownTimer? = null
    private var timeLeft: Long = 30000
    private var isPaused = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preguntas)

        currentUser = UserManager.get(intent.getStringExtra("userName")!!)
        val category = intent.getStringExtra("category")!!
        setTitle("${currentUser.name}   $category")
        options = arrayOf(
            findViewById(R.id.opción1),
            findViewById(R.id.opción2),
            findViewById(R.id.opción3),
            findViewById(R.id.opción4)
        )
        comodin = findViewById(R.id.comodín)

        options.forEach {
            it.setOnClickListener { _ -> answer(it) }
        }

        questions = questionsFromJSON(category)

        savedInstanceState?.let {
            questionIndex = it.getInt("questionIndex")
            answeredQuestions = it.getInt("answeredQuestions")
            correctAnswers = it.getInt("correctAnswers")
            comodin.isEnabled = it.getBoolean("comodínState")
            timeLeft = it.getLong("timeLeft")
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onPause() {
        super.onPause()
        timer?.cancel()
        isPaused = true
    }

    override fun onStart() {
        super.onStart()
        findViewById<TextView>(R.id.puntaje).text = correctAnswers.toString()
        findViewById<TextView>(R.id.num_preguntas).text = answeredQuestions.toString()
        askQuestion(questions[questionIndex])
        isPaused = false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_preguntas, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        if (item.itemId == R.id.help) {
            val i = Intent(this, Help::class.java)
            startActivity(i)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.apply {
            putInt("questionIndex", questionIndex)
            putInt("answeredQuestions", answeredQuestions)
            putInt("correctAnswers", correctAnswers)
            putBoolean("comodínState", comodin.isEnabled)
            putLong("timeLeft", timeLeft)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // luego del onCreate y del onStart
        if (!comodin.isEnabled) comodin.alpha = 0.5f
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
        options.forEachIndexed { index, button ->
            button.text = q.options[index]
            button.isClickable = true
        }
        comodin.isClickable = true
        timer?.cancel()
        timer = object : CountDownTimer(timeLeft, 1000) {
            val timer = findViewById<TextView>(R.id.timer)
            fun remaining(millis: Long): String =
                getString(R.string.seconds, (millis / 1000 + 1))

            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished
                timer.text = remaining(millisUntilFinished)
                // ... Último tick: millisUntilFinished = 0 * 1000
            }

            override fun onFinish() {
                // ... Entonces, luego del último tick, millis, en teoría,
                // sería -1 * 1000
                timer.text = remaining(-1000)
                handleIncorrectAnswer()
            }
        }.start()
    }

    private fun handleIncorrectAnswer() {
        val correctButton = options[questions[questionIndex].correctAnswerIndex]
        val missedColorStateList = ColorStateList.valueOf(getColor(R.color.missed))
        correctButton.backgroundTintList = missedColorStateList
        options.forEach { it.isClickable = false }
        val numPreguntas = findViewById<TextView>(R.id.num_preguntas)
        numPreguntas.text = (++answeredQuestions).toString()

        Handler(Looper.getMainLooper()).postDelayed({
            correctButton.backgroundTintList = null
            nextOrFinish()
        }, 3000)
    }

    fun handleComodin(v: View) {
        v.apply {
            isEnabled = false
            alpha = 0.5f
        }
        nextOrFinish()
    }

    private fun nextOrFinish() {
        questionIndex++
        if (questionIndex < questions.size) {
            timeLeft = 30000
            askQuestion(questions[questionIndex])
        } else {
            GameOverDialogFragment(correctAnswers, answeredQuestions, !comodin.isEnabled) { _, _ ->
                questionIndex = 0
                correctAnswers = 0
                answeredQuestions = 0
                comodin.isEnabled = true
                comodin.alpha = 1f
                onStart()
            }.show(supportFragmentManager, "GameOverDialog")
            currentUser.change(correctAnswers, answeredQuestions)
            UserManager.commit(currentUser)
        }
    }

    /**
     * Callback para los botones de opción
     */
    private fun answer(button: Button) {
        timer?.cancel()
        val correctButton = options[questions[questionIndex].correctAnswerIndex]
        val comodin = findViewById<ImageView>(R.id.comodín)

        val numPreguntas = findViewById<TextView>(R.id.num_preguntas)
        numPreguntas.text = (++answeredQuestions).toString()

        options.forEach { it.isClickable = false }
        comodin.isClickable = false

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
        Handler(Looper.getMainLooper()).postDelayed({
            // Restaurar colores
            button.backgroundTintList = null
            if (!correct)
                correctButton.backgroundTintList = null
            nextOrFinish()
        }, 3000)

    }
}