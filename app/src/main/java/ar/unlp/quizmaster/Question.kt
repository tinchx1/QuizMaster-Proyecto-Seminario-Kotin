package ar.unlp.quizmaster

import org.json.JSONObject

class Question(jsonObject: JSONObject) {
    val questionText: String = jsonObject.getString("question")
    val correctAnswerIndex: Int
    val options: Array<String>

    init {
        correctAnswerIndex = jsonObject.getInt("correctAnswerIndex")
        val questionsArray = jsonObject.getJSONArray("options")
        options = Array(4) { i ->
            questionsArray.getString(i)
        }
    }

    fun isCorrect(str: String): Boolean {
        return str == options[correctAnswerIndex] // equals
    }
}