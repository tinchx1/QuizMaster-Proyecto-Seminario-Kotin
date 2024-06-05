package ar.unlp.quizmaster

import android.app.Dialog
import android.content.DialogInterface.OnClickListener
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class GameOverDialogFragment(
    private val correct: Int,
    private val answered: Int,
    private val comodinUsed: Boolean,
    private val restart: OnClickListener
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false

        val correctText =
            resources.getQuantityString(
                R.plurals.mensaje_respuestas_correctas,
                correct,
                correct,
                answered,
                answered
            )
        val comodinUsedText =
            if (comodinUsed) getString(R.string.comodín_utilizado)
            else getString(R.string.comodín_no_utilizado)
        val askRestartCategory = getString(R.string.jugar_esta_categoría_de_nuevo)

        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.fin_del_juego))
            .setMessage("$correctText\n\n$comodinUsedText\n\n$askRestartCategory")
            .setPositiveButton(getString(R.string.reiniciar), restart)
            .setNegativeButton(getString(R.string.aceptar)) { _, _ ->
                activity?.finish()
            }.create()
    }
}