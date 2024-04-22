package com.example.quizmaster

import org.json.JSONObject

class Question(jsonObject: JSONObject) {
    val questionText: String
    val correctAnswerIndex: Int
    val options: Array<String>

    init {
        questionText = jsonObject.getString("question")
        correctAnswerIndex = jsonObject.getInt("correctAnswerIndex")
        val questionsArray = jsonObject.getJSONArray("options")
        options = Array(4) { i ->
            questionsArray.getString(i)
        }
    }

    public fun isCorrect(str: String): Boolean {
        return str == options[correctAnswerIndex] // equals
    }
}