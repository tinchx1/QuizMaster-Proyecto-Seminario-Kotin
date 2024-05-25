package ar.unlp.quizmaster

import android.app.Dialog
import android.content.DialogInterface.OnClickListener
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class GameOverDialogFragment(
    private val correct: Int,
    private val comodinUsed: Boolean,
    private val restart: OnClickListener
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false

        val correctText = resources.getQuantityString(R.plurals.mensaje_respuestas_correctas, correct, correct)
        val comodinUsedText =
            if (comodinUsed) getString(R.string.comodín_utilizado)
            else getString(R.string.comodín_no_utilizado)

        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.fin_del_juego))
            .setMessage("$correctText\n\n$comodinUsedText")
            .setPositiveButton(getString(R.string.reiniciar), restart)
            .setNegativeButton(getString(R.string.salir)) { _, _ ->
                activity?.finish()
            }.create()
    }
}