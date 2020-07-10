package com.example.cse438.cse438_assignment4.util

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cse438.cse438_assignment4.Player
import com.example.cse438.cse438_assignment4.R
//create the view holder
class LeaderboardViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.leaderboard_items, parent, false)) {
    private val listUserName : TextView
    private val listWinTimes : TextView
    private val listLoseTimes : TextView

    init {
        listUserName = itemView.findViewById(R.id.user_name)
        listWinTimes = itemView.findViewById(R.id.win_times)
        listLoseTimes = itemView.findViewById(R.id.lose_times)
    }

    fun bind(player: Player) {
        listUserName.text = player.getUsername()
        listWinTimes.text = player.getWinTimes().toString()
        listLoseTimes.text = player.getLoseTimes().toString()
    }

}

//create the listener for the recycler view
class LeaderboardAdapter(private val list: ArrayList<Player>?)
    : RecyclerView.Adapter<LeaderboardViewHolder>() {
    private var listEvents : ArrayList<Player>? = list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return LeaderboardViewHolder(inflater, parent)
    }

    //bind the object
    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        val event: Player = listEvents!!.get(position)
        holder.bind(event)
    }

    //set the count
    override fun getItemCount(): Int = list!!.size

}