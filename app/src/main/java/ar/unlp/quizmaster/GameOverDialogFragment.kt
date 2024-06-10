package ar.unlp.quizmaster

import android.app.Dialog
import android.content.DialogInterface.OnClickListener
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class GameOverDialogFragment(
    private val userName: String,
    private val correct: Int,
    private val answered: Int,
    private val comodinUsed: Boolean,
    private val betterScore: Boolean,
    private val restart: OnClickListener
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false

        val top = UserManager.topUsers()
        val inTop: Boolean = top.any { it.name == userName }

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

        val topFiveText: String = if (inTop) {
            getString(
                R.string.comparte_tu_logro_con_tus_amigos,
                if (betterScore) getString(R.string.puntaje_superado)
                else getString(R.string.quedaste_en_el_top_5)
            )
        } else ""

        val askRestartCategory = getString(R.string.jugar_esta_categoría_de_nuevo)

        val context = requireContext()
        val builder = AlertDialog.Builder(context)
            .setTitle(getString(R.string.fin_del_juego))
            .setMessage(
                "$correctText\n\n$comodinUsedText\n\n$topFiveText$askRestartCategory"
            )
            .setPositiveButton(getString(R.string.reiniciar), restart)
            .setNegativeButton(getString(R.string.aceptar)) { _, _ ->
                val i = Intent(context, Ranking::class.java)
                startActivity(i)
                activity?.finish()
            }

        if (inTop)
            builder.setNeutralButton("Compartir") { _, _ ->
                val rankingText = top.mapIndexed { index, user ->
                    getString(R.string.preguntas_correctas, index + 1, user.name, user.correct)
                }.joinToString(separator = "\n")
                val i = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, rankingText)
                }
                startActivity(i)
                activity?.finish()
            }

        return builder.create()
    }
}