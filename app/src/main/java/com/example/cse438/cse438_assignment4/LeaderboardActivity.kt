package com.example.cse438.cse438_assignment4

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cse438.cse438_assignment4.util.LeaderboardAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_leaderboard.*

class LeaderboardActivity : AppCompatActivity() {

    var leaderList = ArrayList<Player>()
    val leaderAdapter = LeaderboardAdapter(leaderList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        leaderboard_recycler.layoutManager = LinearLayoutManager(this)
        leaderboard_recycler.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        leaderboard_recycler.adapter = leaderAdapter

        //Get the firebase instance
        val db = FirebaseFirestore.getInstance()
        db.collection("players").orderBy("wins", Query.Direction.DESCENDING).addSnapshotListener {
                snapshots, error ->

            for (doc in snapshots?.documents!!) {
                var player = Player()
                for (data in doc.data!!) {
                    player.assignValue(data.key, data.value)
                }
                leaderList.add(player)
            }

            runOnUiThread {
                leaderAdapter.notifyDataSetChanged()
            }
        }

        //A back button listener
        leader_back_button.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }
    }

}