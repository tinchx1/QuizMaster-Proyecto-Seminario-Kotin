package ar.unlp.quizmaster

import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class Ranking : AppCompatActivity() {
    private lateinit var adapter: ArrayAdapter<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val topRankingItems = UserManager.topUsers()
        val listView: ListView = findViewById(R.id.ranking_list)
        val topRankingItemsNames =
            topRankingItems.mapIndexed { i, user ->
                resources.getQuantityString(R.plurals.preguntas_correctas, user.correct, i + 1, user.name, user.correct)
            }
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, topRankingItemsNames)
        listView.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}