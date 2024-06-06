package ar.unlp.quizmaster

data class User(val name: String) {
    var correct: Int = 0
    var answered: Int = 0

    /**
     * Incrementa las respuestas totales y nuevas del usuario.
     *
     * @param correct Respuestas correctas nuevas
     * @param answered Respuestas nuevas
     * @return El usuario con los nuevos valores
     */
    fun increment(correct: Int, answered: Int) {
        this.correct += correct
        this.answered += answered
    }
}