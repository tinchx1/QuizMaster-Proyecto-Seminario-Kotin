package ar.unlp.quizmaster

import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Ranking : AppCompatActivity() {
    private lateinit var adapter: ArrayAdapter<String>
    private val rankingItems: MutableList<User> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ranking)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_ranking)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        getSharedPreferences("Ranking", MODE_PRIVATE).all.keys.forEach {
            UserManager.get(it).let { user ->
                if (user.correct > 0) {
                    rankingItems.add(user)
                }
            }
        }
        rankingItems.sortWith(compareByDescending { it.correct })
        val topRankingItems = rankingItems.take(5)
        val listView: ListView = findViewById(R.id.ranking_list)
        var i = 0
        val topRankingItemsNames =
            topRankingItems.map {
                i++
                "${i}-                         ${it.name}: ${it.correct}"
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