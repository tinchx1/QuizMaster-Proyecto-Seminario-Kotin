package ar.unlp.quizmaster

data class User(val name: String) {
    var correct: Int = 0

    /**
     * Incrementa las respuestas totales y nuevas del usuario.
     *
     * @param correct Respuestas correctas nuevas
     */
    fun change(correct: Int) {
        if (correct > this.correct) {
            this.correct = correct
        }
    }
}