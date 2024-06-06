package ar.unlp.quizmaster

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {
    private var userName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        createUsernameDialog()
    }

    private fun createUsernameDialog() {
        val editText = EditText(this).apply {
            val paddingDp = 16
            val paddingPx = (paddingDp * resources.displayMetrics.density).toInt()
            setPadding(paddingPx, paddingTop, paddingPx, paddingBottom)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            isSingleLine = true
        }

        val linearLayout = LinearLayout(this).apply {
            val marginDp = 16
            val marginPx = (marginDp * resources.displayMetrics.density).toInt()
            setPadding(marginPx, 0, marginPx, 0)
            addView(editText)
        }

        val alertDialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.title_alertdialog_name))
            .setMessage(getString(R.string.message_alertdialog_name))
            .setView(linearLayout)
            .setCancelable(false)
            .setPositiveButton(getString(R.string.aceptar)) { _, _ ->
                userName = editText.text.trim().toString()
                crearUsuario(userName)
            }
            .create()

        editText.doAfterTextChanged { text ->
            text?.let { alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = it.isNotBlank() }
        }

        alertDialog.show()
        /*
          Una vez mostrado, desactivar botón positivo.
          El EditText, luego, lo (des)activará si el nombre de usuario es válido o no.
        */
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
    }

    private fun crearUsuario(userName: String) {
        val preferencias: SharedPreferences =
            this.getSharedPreferences("Users", Context.MODE_PRIVATE)
        // Guarda solo si un usuario con ese nombre no existe
        if (!preferencias.contains(userName)) {
            val user = User(userName)
            val json = Gson().toJson(user)
            val editor = preferencias.edit()
            editor.putString(userName, json)
            editor.apply()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.help) {
            val i = Intent(this, Help::class.java)
            startActivity(i)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @Suppress("unused") // Utilizado en app/src/main/res/values/themes.xml
    fun launchCategory(v: View) {
        val button = v as Button
        val textCategory = button.text.toString()
        val i = Intent(this, Preguntas::class.java)
        i.putExtra("category", textCategory)
        i.putExtra("userName", userName)
        startActivity(i)
    }
}
