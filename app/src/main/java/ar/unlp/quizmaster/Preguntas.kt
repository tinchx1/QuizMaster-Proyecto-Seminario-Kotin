package ar.unlp.quizmaster

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import org.json.JSONObject
import java.io.InputStream

class Preguntas : AppCompatActivity() {

    private lateinit var comodin: ImageView
    private lateinit var options: Array<Button>
    private lateinit var questions: Array<Question>
    private var questionIndex = 0
    private var answeredQuestions = 0
    private var correctAnswers = 0
    private var user: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_preguntas)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_preguntas)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val userName = intent.getStringExtra("userName") ?: ""
        this.user = obtenerUsuario(userName)
        val category = intent.getStringExtra("category") ?: ""
        setTitle("${this.user?.userName}   $category")
        options = arrayOf(
            findViewById(R.id.opción1),
            findViewById(R.id.opción2),
            findViewById(R.id.opción3),
            findViewById(R.id.opción4)
        )
        comodin = findViewById(R.id.comodín)

        options.forEach { it ->
            it.setOnClickListener { answer(it as Button) }
        }

        questions = questionsFromJSON(category)

        savedInstanceState?.let {
            questionIndex = it.getInt("questionIndex")
            answeredQuestions = it.getInt("answeredQuestions")
            correctAnswers = it.getInt("correctAnswers")
            comodin.isEnabled = it.getBoolean("comodínState")
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun guardarUsuario(context: Context, user: User) {
        val preferencias: SharedPreferences =
            context.getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferencias.edit()
        val gson = Gson()
        val json = gson.toJson(user) // Convertir objetos User a cadena JSON
        editor.putString(user.userName, json)
        editor.apply()
    }

    private fun obtenerUsuario(userName: String): User? {
        val preferencias: SharedPreferences =
            this.getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = preferencias.getString(userName, null)
        Log.d("MyApp", "json: $json")
        return if (json != null) {
            gson.fromJson(json, User::class.java)
        } else {
            null
        }
    }

    override fun onStart() {
        super.onStart()
        findViewById<TextView>(R.id.puntaje).text = correctAnswers.toString()
        findViewById<TextView>(R.id.num_preguntas).text = answeredQuestions.toString()
        askQuestion(questions[questionIndex])
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
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
        options.forEachIndexed { index, button -> button.text = q.options[index] }
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
        if (questionIndex < questions.size)
            askQuestion(questions[questionIndex])
        else {
            GameOverDialogFragment(correctAnswers, answeredQuestions, !comodin.isEnabled) { _, _ ->
                questionIndex = 0
                correctAnswers = 0
                answeredQuestions = 0
                comodin.isEnabled = true
                comodin.alpha = 1f
                onStart()
            }.show(supportFragmentManager, "GameOverDialog")
        }
    }

    private fun answer(button: Button) {
        val correctButton = options[questions[questionIndex].correctAnswerIndex]
        val previousColorStateList = button.backgroundTintList
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
            button.backgroundTintList = previousColorStateList
            if (!correct)
                correctButton.backgroundTintList = previousColorStateList
            nextOrFinish()
            options.forEach { it.isClickable = true }
            comodin.isClickable = true
        }, 3000)

    }
}